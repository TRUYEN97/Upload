/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.model;

/**
 *
 * @author Administrator
 */
public class modelManager {
    private static volatile modelManager manager;
    private final UserModel userModel;

    private modelManager() {
        this.userModel = new UserModel();
    }
    
    public static modelManager getInstance(){
        modelManager ins = modelManager.manager;
        if(ins == null){
            synchronized (modelManager.class) {
                ins = modelManager.manager;
                if (ins == null) {
                    ins = new modelManager();
                    manager = ins;
                }
            }
        }
        return manager;
    }

    public UserModel getUserModel() {
        return userModel;
    }
    
    
}
