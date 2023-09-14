/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.tec02.view;

import com.tec02.common.JOptionUtil;
import com.tec02.API.JsonBodyAPI;
import com.tec02.common.Keyword;
import com.tec02.API.APIController;
import com.tec02.gui.frameGui.AbsDisplayAble;
import com.tec02.gui.frameGui.Component.PopupMenu;
import com.tec02.gui.panelGui.FileUpdatePanel;
import com.tec02.gui.panelGui.TableAndLocation;
import com.tec02.common.PropertiesModel;

/**
 *
 * @author Administrator
 */
public class ManageFgroup extends AbsDisplayAble {

    private final TableAndLocation tableAndLocation;
    private final APIController api;
    private final FileUpdatePanel updateFile;

    /**
     * Creates new form ManageFgroup
     */
    public ManageFgroup() {
        initComponents();
        this.api = APIController.getInstance();
        this.updateFile = new FileUpdatePanel(api);
        this.tableAndLocation = new TableAndLocation(api);
        this.tableAndLocation.setUrlAdd(PropertiesModel.getConfig(Keyword.Url.Fgroup.POST));
        this.tableAndLocation.setUrlGet(PropertiesModel.getConfig(Keyword.Url.Fgroup.GET_LIST));
        this.tableAndLocation.setUrlDelete(PropertiesModel.getConfig(Keyword.Url.Fgroup.DELETE));
        this.tableAndLocation.setDoubleClickAction((input) -> {
            showFileGroup();
        });
        PopupMenu selectedMenu = this.tableAndLocation.getSelectedMenu();
        selectedMenu.addItemMenu("View", (e) -> {
            showFileGroup();
        });
        selectedMenu.addItemMenu("Delete", (e) -> {
            this.tableAndLocation.deleteSeleled();
            this.updateFile.clear();
        });
        PopupMenu popupMenu = this.tableAndLocation.getMenu();
        popupMenu.addItemMenu("New", (e) -> {
            JOptionUtil.newWithNameAndDescription("New file group", (name, description) -> {
                this.tableAndLocation.addNew(JsonBodyAPI.builder().put("name", name)
                        .put("description", description));
            }, null);
            showFileGroup();
        });
        this.pnl.add(this.tableAndLocation);
        this.tableAndLocation.find();
    }

    public void showFileGroup() {
        Object fgId = this.tableAndLocation.getTableModel().getRowSelectedValue("id");
        if (fgId == null) {
            return;
        }
        String name = this.tableAndLocation.getTableModel().getRowSelectedValue("name");
        this.updateFile.setFileInfo(fgId, name);
        JOptionUtil.showObject(this.updateFile, "File viewer");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnl.setBackground(new java.awt.Color(204, 255, 255));
        pnl.setLayout(new javax.swing.BoxLayout(pnl, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl, javax.swing.GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnl;
    // End of variables declaration//GEN-END:variables

    public void display() {
        display("Manager file group");
        this.tableAndLocation.update();
    }
}
