/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.view;

import com.tec02.common.JsonBodyAPI;
import com.tec02.common.Keyword;
import com.tec02.core.APIController;
import com.tec02.gui.frameGui.ChangeLine;
import com.tec02.gui.frameGui.Component.PopupMenu;
import com.tec02.gui.frameGui.ManageEntityHaveLocation;
import com.tec02.model.PropertiesModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class PcManager {

    private final ManageEntityHaveLocation entityHaveLocation;
    private final APIController api;
    private final ChangeLine changeLine;

    public PcManager() {
        this.api = APIController.getInstance();
        this.entityHaveLocation = new ManageEntityHaveLocation(api, 
                PropertiesModel.getConfig(Keyword.Url.Product.GET),
                PropertiesModel.getConfig(Keyword.Url.Station.GET),
                PropertiesModel.getConfig(Keyword.Url.Line.GET));
        this.entityHaveLocation.setUrlAdd(PropertiesModel.getConfig(Keyword.Url.Pc.POST));
        this.entityHaveLocation.setUrlGet(PropertiesModel.getConfig(Keyword.Url.Pc.GET));
        this.entityHaveLocation.setUrlDelete(PropertiesModel.getConfig(Keyword.Url.Pc.DELETE));
        this.changeLine = new ChangeLine(api, 
                PropertiesModel.getConfig(Keyword.Url.Line.GET),
                this.entityHaveLocation.getTableModel());
        this.changeLine.setPutUrl(PropertiesModel.getConfig(Keyword.Url.Pc.PUT));
        PopupMenu popupMenu = this.entityHaveLocation.getMenu();
        popupMenu.addItemMenu("New PC", (e) -> {
            String input = JOptionPane.showInputDialog("input");
            if (input == null) {
                return;
            }
            this.entityHaveLocation.addNew(JsonBodyAPI.builder().put("name", input));
        });
        popupMenu.addItemMenu("Delete", (e) -> {
            this.entityHaveLocation.deleteSeleled();
        });
        popupMenu.addItemMenu("Change Line", (e) -> {
            this.changeLine.display("Chang Line");
        });
        this.entityHaveLocation.setMenu(popupMenu);
    }

    public void display() {
        this.entityHaveLocation.display("Manage Pc");
    }

}
