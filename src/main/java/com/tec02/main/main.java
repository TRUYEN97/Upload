/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main;

import com.tec02.core.APIController;
import com.tec02.model.PropertiesModel;
import com.tec02.model.modelManager;
import com.tec02.view.Gui;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class main {

    public static void main(String[] args) {
        try {
            String jwt = null;
            if (args.length > 0) {
                jwt = args[0];
            }
            PropertiesModel.getInstance();
            APIController.getInstance().setJwtToken(jwt);
            modelManager.getInstance().getUserModel()
                    .setJwtUtil(APIController.getInstance().getJwtUtil());
            Gui gui = new Gui();
            gui.display();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        }
    }
}
