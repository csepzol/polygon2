/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 *
 * @author lenovo
 */
public class OverviewMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {
    int movePrevX, movePrevY;
    
    @Override
    public void mouseClicked(MouseEvent me) {
        //System.out.println("clicked");
        if (me.getButton() == MouseEvent.BUTTON1){
            //System.out.println(me.getX());
            //System.out.println(me.getY());
            MainWindow.getCo().mouseX=me.getX();
            MainWindow.getCo().mouseY=me.getY();
            MainWindow.getCo().mouseClick = true;
//            MainWindow.getCr().yTrans=me.getX();
//            MainWindow.getCr().zTrans=me.getY();
            MainWindow.getCaveOverview().repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
    }
    
}
