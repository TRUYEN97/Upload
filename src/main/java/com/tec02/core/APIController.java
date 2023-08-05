/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.core;

import com.tec02.common.API.RestAPI;


/**
 *
 * @author Administrator
 */
public class APIController extends RestAPI{

    private static volatile APIController api;
    
    private APIController() {
        super();
    }
    
    public static APIController getInstance(){
        APIController ins = APIController.api;
        if (ins == null) {
            synchronized (APIController.class) {
                ins = APIController.api;
                if (ins == null) {
                    APIController.api = ins = new APIController();
                }
            }
        }
        return ins;
    }
    
}
