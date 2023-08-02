/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.tec02.view;

import com.tec02.common.JOptionUtil;
import com.tec02.common.JsonBodyAPI;
import com.tec02.common.Keyword;
import com.tec02.common.RequestParam;
import com.tec02.common.RestUtil;
import com.tec02.core.APIController;
import com.tec02.gui.frameGui.AbsDisplayAble;
import com.tec02.gui.frameGui.Component.MyTable;
import com.tec02.gui.frameGui.Component.PopupMenu;
import com.tec02.gui.panelGui.FileUpdatePanel;
import com.tec02.gui.panelGui.TableAndLocation;
import com.tec02.gui.model.PropertiesModel;
import com.tec02.gui.panelGui.ProgramEditPanel;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class ManageProgram extends AbsDisplayAble {

    private TableAndLocation tableAndLocation;
    private final APIController api;
    private final MyTable pcTable;
    private final MyTable fGroupTable;
    private final RestUtil restUtil;
    private final FileUpdatePanel updateFile;

    /**
     * Creates new form ManageProgram
     */
    public ManageProgram() {
        initComponents();
        this.api = APIController.getInstance();
        this.restUtil = new RestUtil(api);
        this.fGroupTable = new MyTable(tbFgroup);
        this.pcTable = new MyTable(tbPc);
        this.updateFile = new FileUpdatePanel(api);
        this.fGroupTable.setDoubleClickAction((input) -> {
            Object fgId = this.fGroupTable.getRowSelectedValue("id");
            if (fgId == null) {
                return;
            }
            String name = this.fGroupTable.getRowSelectedValue("name");
            this.updateFile.setFileInfo(fgId, name);
            JOptionUtil.showObject(this.updateFile, name);
        });
        initTableAndLocation();
    }

    private void initTableAndLocation() {
        this.tableAndLocation = new TableAndLocation(api);
        this.tableAndLocation.setUrlAdd(PropertiesModel.getConfig(Keyword.Url.Program.POST));
        this.tableAndLocation.setUrlGet(PropertiesModel.getConfig(Keyword.Url.Program.GET_LIST));
        this.tableAndLocation.setUrlDelete(PropertiesModel.getConfig(Keyword.Url.Program.DELETE));
        this.tableAndLocation.setDoubleClickAction((input) -> {
            viewProgram();
        });
        PopupMenu selectedMenu = this.tableAndLocation.getSelectedMenu();
        selectedMenu.addItemMenu("View", (e) -> {
            viewProgram();
        });
        selectedMenu.addItemMenu("edit", (e) -> {
            Object id = this.tableAndLocation.getTableSelectedValue("id");
            ProgramEditPanel editPanel = new ProgramEditPanel();
            editPanel.setEnable(this.tableAndLocation.getTableSelectedValueT("enable"));
            editPanel.setAwaysUpdate(this.tableAndLocation.getTableSelectedValueT("awaysUpdate"));
            editPanel.setPassword(this.tableAndLocation.getTableSelectedValueT("password"));
            editPanel.setCommand(this.tableAndLocation.getTableSelectedValueT("command"));
            editPanel.setDescription(this.tableAndLocation.getTableSelectedValueT("description"));
            JOptionUtil.showObject(editPanel, this.tableAndLocation.getTableSelectedValueT("name"));
            if (editPanel.hasChange()) {
                this.restUtil.update(PropertiesModel.getConfig(Keyword.Url.Program.PUT),
                        RequestParam.builder().addParam(Keyword.ID, id),
                        JsonBodyAPI.builder()
                                .put(Keyword.ID, id)
                                .put(Keyword.ENABLE, editPanel.getEnable())
                                .put(Keyword.AWAYS_UPDATE, editPanel.getAwaysUpdate())
                                .put(Keyword.PASSWORD, editPanel.getPassword())
                                .put(Keyword.COMMAND, editPanel.getCommand()));
                this.tableAndLocation.find();
            }
        });
        selectedMenu.addItemMenu("Add file-program", (e) -> {
            Object id = this.tableAndLocation.getTableSelectedValue("id");
            var fileGroups = JOptionUtil.getTableSelectedItem("select file-program",
                    this.restUtil.getList(PropertiesModel.getConfig(Keyword.Url.FileProgram.GET_PROGRAM_LOCATION),
                            RequestParam.builder().addParam("id", id)));
            if (fileGroups == null || fileGroups.isEmpty()) {
                return;
            }
            this.restUtil.update(PropertiesModel.getConfig(Keyword.Url.Program.PUT_FILE_PROGRAM),
                    RequestParam.builder().addParam(Keyword.ID, id),
                    JsonBodyAPI.builder()
                            .put(Keyword.ID, fileGroups.get(Keyword.ID)));
            viewProgram();
        });
        selectedMenu.addItemMenu("Add file-group", (e) -> {
            Object id = this.tableAndLocation.getTableSelectedValue("id");
            var fileGroups = JOptionUtil.getTableSelectedItems("select file-group",
                    this.restUtil.getList(PropertiesModel.getConfig(Keyword.Url.Fgroup.GET_PROGRAM_LOCATION),
                            RequestParam.builder().addParam("id", id)));
            if (fileGroups == null || fileGroups.isEmpty()) {
                return;
            }
            List<Object> ids = new ArrayList<>();
            for (Map fileGroup : fileGroups) {
                ids.add(fileGroup.get("id"));
            }
            this.restUtil.update(PropertiesModel.getConfig(Keyword.Url.Program.PUT_FGROUP),
                    null,
                    JsonBodyAPI.builder().put("id", id).put("ids", ids));
            viewProgram();
        });
        selectedMenu.addItemMenu("remove file-group", (e) -> {
            Object id = this.tableAndLocation.getTableSelectedValue("id");
            var fileGroups = JOptionUtil.getTableSelectedItems("remove file-group",
                    this.restUtil.getList(PropertiesModel.getConfig(Keyword.Url.Fgroup.GET_PROGRAM_ID),
                            RequestParam.builder().addParam("id", id)));
            if (fileGroups == null || fileGroups.isEmpty()) {
                return;
            }
            List<Object> ids = new ArrayList<>();
            for (Map fileGroup : fileGroups) {
                ids.add(fileGroup.get("id"));
            }
            this.restUtil.update(PropertiesModel.getConfig(Keyword.Url.Program.PUT_FGROUP),
                    RequestParam.builder().addParam("action", "1"),
                    JsonBodyAPI.builder().put("id", id).put("ids", ids));
            this.tableAndLocation.update();
        });
        selectedMenu.addItemMenu("Delete", (e) -> {
            this.tableAndLocation.deleteSeleled();
        });
        PopupMenu popupMenu = this.tableAndLocation.getMenu();
        popupMenu.addItemMenu("New", (e) -> {
            JOptionUtil.newWithNameAndDescription("New file group", (name, description) -> {
                this.tableAndLocation.addNew(JsonBodyAPI.builder().put("name", name)
                        .put("description", description));
            }, null);
        });
        this.pnUp.add(this.tableAndLocation);
        this.tableAndLocation.update();
    }

    private void viewProgram() throws HeadlessException {
        Object programId = this.tableAndLocation.getTableModel().getRowSelectedValue("id");
        if (programId == null) {
            return;
        }
        this.fGroupTable.clear();
        this.pcTable.clear();
        this.pcTable.setDatas(this.restUtil.getList(
                PropertiesModel.getConfig(Keyword.Url.Pc.GET_PROGRAM_ID),
                RequestParam.builder().addParam("id", programId)));
        this.fGroupTable.setDatas(this.restUtil.getList(
                PropertiesModel.getConfig(Keyword.Url.Fgroup.GET_PROGRAM_ID),
                RequestParam.builder().addParam("id", programId)));
    }

    public void display() {
        this.display("Manage program");
        this.tableAndLocation.update();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnUp = new javax.swing.JPanel();
        pnL = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbFgroup = new javax.swing.JTable();
        pnR = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbPc = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnUp.setBackground(new java.awt.Color(204, 204, 255));
        pnUp.setLayout(new javax.swing.BoxLayout(pnUp, javax.swing.BoxLayout.LINE_AXIS));

        pnL.setBackground(new java.awt.Color(204, 204, 255));
        pnL.setBorder(javax.swing.BorderFactory.createTitledBorder("file-group"));

        tbFgroup.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbFgroup);

        javax.swing.GroupLayout pnLLayout = new javax.swing.GroupLayout(pnL);
        pnL.setLayout(pnLLayout);
        pnLLayout.setHorizontalGroup(
            pnLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnLLayout.setVerticalGroup(
            pnLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addGap(8, 8, 8))
        );

        pnR.setBackground(new java.awt.Color(204, 204, 255));
        pnR.setBorder(javax.swing.BorderFactory.createTitledBorder("Pc"));

        tbPc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tbPc);

        javax.swing.GroupLayout pnRLayout = new javax.swing.GroupLayout(pnR);
        pnR.setLayout(pnRLayout);
        pnRLayout.setHorizontalGroup(
            pnRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnRLayout.setVerticalGroup(
            pnRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnUp, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnL;
    private javax.swing.JPanel pnR;
    private javax.swing.JPanel pnUp;
    private javax.swing.JTable tbFgroup;
    private javax.swing.JTable tbPc;
    // End of variables declaration//GEN-END:variables
}
