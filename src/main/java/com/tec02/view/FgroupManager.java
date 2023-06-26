/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.view;

import com.tec02.common.Keyword;
import com.tec02.core.APIController;
import com.tec02.gui.frameGui.ManageEntityHaveLocation;
import com.tec02.model.PropertiesModel;

/**
 *
 * @author Administrator
 */
public class FgroupManager {
    private final ManageEntityHaveLocation entityHaveLocation;
    private final APIController api;
    
    public FgroupManager() {
        this.api = APIController.getInstance();
        this.entityHaveLocation = new ManageEntityHaveLocation(api, 
                PropertiesModel.getConfig(Keyword.Url.Product.GET),
                PropertiesModel.getConfig(Keyword.Url.Station.GET),
                PropertiesModel.getConfig(Keyword.Url.Line.GET));
        this.entityHaveLocation.setUrlAdd(PropertiesModel.getConfig(Keyword.Url.Fgroup.POST));
        this.entityHaveLocation.setUrlGet(PropertiesModel.getConfig(Keyword.Url.Fgroup.GET));
        this.entityHaveLocation.setUrlDelete(PropertiesModel.getConfig(Keyword.Url.Fgroup.DELETE));
    }
    
    public void display(){
        this.entityHaveLocation.display("Manage File-Group");
    }
}
