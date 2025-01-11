/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author lenovo
 */
public class SurveyPanelKeyListener implements KeyListener{

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        System.out.println(ke.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
    
}
