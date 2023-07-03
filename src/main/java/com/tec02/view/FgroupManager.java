/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.view;

import com.tec02.common.JsonBodyAPI;
import com.tec02.common.Keyword;
import com.tec02.core.APIController;
import com.tec02.gui.frameGui.Component.PopupMenu;
import com.tec02.gui.frameGui.FormNew;
import com.tec02.gui.frameGui.ManageEntityHaveLocation;
import com.tec02.gui.frameGui.ShowFileGroup;
import com.tec02.model.PropertiesModel;

/**
 *
 * @author Administrator
 */
public class FgroupManager {
    private final ManageEntityHaveLocation entityHaveLocation;
    private final APIController api;
    private final FormNew formNew;
    private final ShowFileGroup showFileGroup;
    
    
    public FgroupManager() {
        this.api = APIController.getInstance();
        this.showFileGroup = new ShowFileGroup(api);
        this.entityHaveLocation = new ManageEntityHaveLocation(api, 
                PropertiesModel.getConfig(Keyword.Url.Product.GET),
                PropertiesModel.getConfig(Keyword.Url.Station.GET),
                PropertiesModel.getConfig(Keyword.Url.Line.GET));
        this.entityHaveLocation.setUrlAdd(PropertiesModel.getConfig(Keyword.Url.Fgroup.POST));
        this.entityHaveLocation.setUrlGet(PropertiesModel.getConfig(Keyword.Url.Fgroup.GET_LIST));
        this.entityHaveLocation.setUrlDelete(PropertiesModel.getConfig(Keyword.Url.Fgroup.DELETE));
        this.formNew = new FormNew(entityHaveLocation);
        PopupMenu popupMenu = this.entityHaveLocation.getMenu();
        popupMenu.addItemMenu("New", (e) -> {
            this.formNew.display("Add new file-group");
        });
        popupMenu.addItemMenu("update", (e) -> {
            this.showFileGroup.setUrlGet(PropertiesModel.getConfig(Keyword.Url.Fgroup.GET));
            this.showFileGroup.display("File-Group");
        });
        popupMenu.addItemMenu("Delete", (e) -> {
            this.entityHaveLocation.deleteSeleled();
        });
        this.entityHaveLocation.setMenu(popupMenu);
        entityHaveLocation.addSubFrame(showFileGroup);
        entityHaveLocation.addSubFrame(formNew);
    }
    
    public void display(){
        this.entityHaveLocation.display("Manage File-Group");
    }
}
