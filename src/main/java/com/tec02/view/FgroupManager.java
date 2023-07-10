/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.view;

import com.tec02.common.Keyword;
import com.tec02.core.APIController;
import com.tec02.gui.frameGui.Component.PopupMenu;
import com.tec02.gui.frameGui.FormNew;
import com.tec02.gui.frameGui.ManageEntityHaveLocation;
import com.tec02.gui.frameGui.treeFile.UpdateFile;
import com.tec02.model.PropertiesModel;

/**
 *
 * @author Administrator
 */
public class FgroupManager {

    private final ManageEntityHaveLocation entityHaveLocation;
    private final APIController api;
    private final FormNew formNew;
    private final UpdateFile updateFile;

    public FgroupManager() {
        this.api = APIController.getInstance();
        this.updateFile = new UpdateFile(api);
        this.entityHaveLocation = new ManageEntityHaveLocation(api,
                PropertiesModel.getConfig(Keyword.Url.Product.GET),
                PropertiesModel.getConfig(Keyword.Url.Station.GET),
                PropertiesModel.getConfig(Keyword.Url.Line.GET));
        this.entityHaveLocation.setUrlAdd(PropertiesModel.getConfig(Keyword.Url.Fgroup.POST));
        this.entityHaveLocation.setUrlGet(PropertiesModel.getConfig(Keyword.Url.Fgroup.GET_LIST));
        this.entityHaveLocation.setUrlDelete(PropertiesModel.getConfig(Keyword.Url.Fgroup.DELETE));
        this.formNew = new FormNew(entityHaveLocation);
        PopupMenu popupMenu = this.entityHaveLocation.getMenu();
        this.entityHaveLocation.setDoubleClickAction((input) -> {
            Integer fgId = this.entityHaveLocation.getTableModel().getRowSelectedValue("id");
            if (fgId == null) {
                return;
            }
            this.updateFile.setUrlGetVersion(PropertiesModel.getConfig(Keyword.Url.File.GET_VERSION));
            this.updateFile.setUrlAdd(PropertiesModel.getConfig(Keyword.Url.File.POST));
            this.updateFile.setUrlGet(PropertiesModel.getConfig(Keyword.Url.File.GET));
            this.updateFile.setUrlPut(PropertiesModel.getConfig(Keyword.Url.File.PUT));
            this.updateFile.setUrlGetDownload(PropertiesModel.getConfig(Keyword.Url.File.GET_LAST_VERSION_DOWNLOAD));
            this.updateFile.display(fgId, this.entityHaveLocation.getTableModel().getRowSelectedValue("name"));
        });
        popupMenu.addItemMenu("New", (e) -> {
            this.formNew.display("Add new file-group");
        });
        popupMenu.addItemMenu("Delete", (e) -> {
            this.entityHaveLocation.deleteSeleled();
        });
        this.entityHaveLocation.setMenu(popupMenu);
        entityHaveLocation.addSubFrame(updateFile);
        entityHaveLocation.addSubFrame(formNew);
    }

    public void display() {
        this.entityHaveLocation.display("Manage File-Group");
    }
}
