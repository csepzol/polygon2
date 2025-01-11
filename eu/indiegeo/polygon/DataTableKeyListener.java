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
public class DataTableKeyListener implements KeyListener{

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode()== KeyEvent.VK_E && ke.isControlDown()){
            MainWindow.getNewColumnButton().doClick();
        }
        if (ke.getKeyCode()== KeyEvent.VK_R && ke.isControlDown()){
            MainWindow.getNewLineButton().doClick();
        }
        if (ke.getKeyCode()== KeyEvent.VK_T && ke.isControlDown()){
            MainWindow.getSaveTableDataButton().doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
    
}
