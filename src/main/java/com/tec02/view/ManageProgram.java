/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.tec02.view;

import com.tec02.common.JOptionUtil;
import com.tec02.API.JsonBodyAPI;
import com.tec02.common.Keyword;
import com.tec02.API.RequestParam;
import com.tec02.common.RestUtil;
import com.tec02.API.APIController;
import com.tec02.Jmodel.Component.PopupMenu;
import com.tec02.gui.frameGui.AbsDisplayAble;
import com.tec02.gui.panelGui.TableAndLocation;
import com.tec02.common.PropertiesModel;
import com.tec02.gui.panelGui.ProgramEditPanel;
import java.awt.HeadlessException;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class ManageProgram extends AbsDisplayAble {

    private TableAndLocation tableAndLocation;
    private final APIController api;
    private final RestUtil restUtil;
    private final ProgramEditPanel editPanel;

    /**
     * Creates new form ManageProgram
     */
    public ManageProgram() {
        initComponents();
        this.api = APIController.getInstance();
        this.restUtil = new RestUtil(api);
        this.editPanel = new ProgramEditPanel(api);
        initTableAndLocation();
    }

    private void initTableAndLocation() {
        this.tableAndLocation = new TableAndLocation(api);
        this.tableAndLocation.setUrlAdd(PropertiesModel.getConfig(Keyword.Url.Program.POST));
        this.tableAndLocation.setUrlGet(PropertiesModel.getConfig(Keyword.Url.Program.GET_LIST));
        this.tableAndLocation.setUrlDelete(PropertiesModel.getConfig(Keyword.Url.Program.DELETE));
        this.tableAndLocation.addColoumn(List.of(Keyword.ID, Keyword.NAME,Keyword.ENABLE, 
                Keyword.DESCRIPTION, Keyword.ALWAYS_RUN, Keyword.ALWAYS_UPDATE, Keyword.PASSWORD));
        this.tableAndLocation.setDoubleClickAction((input) -> {
            viewProgram();
        });
        PopupMenu selectedMenu = this.tableAndLocation.getSelectedMenu();
        selectedMenu.addItemMenu("View", (e) -> {
            viewProgram();
        });
        selectedMenu.addItemMenu("Delete", (e) -> {
            this.tableAndLocation.deleteSeleled();
        });
        PopupMenu popupMenu = this.tableAndLocation.getMenu();
        popupMenu.addItemMenu("New", (e) -> {
            JOptionUtil.newWithNameAndDescription("New Program", (name, description) -> {
                this.tableAndLocation.addNew(JsonBodyAPI.builder().put(Keyword.NAME, name)
                        .put(Keyword.DESCRIPTION, description));
            }, null);
        });
        this.pnUp.add(this.tableAndLocation);
        this.tableAndLocation.find();
    }

    private void viewProgram() throws HeadlessException {
        Object programId = this.tableAndLocation.getTableSelectedValue(Keyword.ID);
        if (programId == null) {
            return;
        }
        editPanel.setProgramId(programId);
        editPanel.setEnable(this.tableAndLocation.getTableSelectedValueT(Keyword.ENABLE));
        editPanel.setAlwaysRun(this.tableAndLocation.getTableSelectedValueT(Keyword.ALWAYS_RUN));
        editPanel.setAlwaysUpdate(this.tableAndLocation.getTableSelectedValueT(Keyword.ALWAYS_UPDATE));
        editPanel.setPassword(this.tableAndLocation.getTableSelectedValueT(Keyword.PASSWORD));
        editPanel.setDescription(this.tableAndLocation.getTableSelectedValueT(Keyword.DESCRIPTION));
        JOptionUtil.showObject(editPanel, this.tableAndLocation.getTableSelectedValueT(Keyword.NAME));
        if (editPanel.hasChange()) {
            this.restUtil.update(PropertiesModel.getConfig(Keyword.Url.Program.PUT),
                    RequestParam.builder().addParam(Keyword.ID, programId),
                    JsonBodyAPI.builder()
                            .put(Keyword.ID, programId)
                            .put(Keyword.ENABLE, editPanel.getEnable())
                            .put(Keyword.ALWAYS_RUN, editPanel.getAlwaysRun())
                            .put(Keyword.ALWAYS_UPDATE, editPanel.getAlwaysUpdate())
                            .put(Keyword.PASSWORD, editPanel.getPassword()));
            this.tableAndLocation.find();
        }
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        pnUp = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnUp.setBackground(new java.awt.Color(204, 204, 255));
        pnUp.setLayout(new javax.swing.BoxLayout(pnUp, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnUp, javax.swing.GroupLayout.DEFAULT_SIZE, 858, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnUp, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    private javax.swing.JPanel pnUp;
    // End of variables declaration                   
}
