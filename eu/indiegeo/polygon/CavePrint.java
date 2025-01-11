/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import com.jogamp.opengl.awt.GLJPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;

/**
 *
 * @author lenovo
 */
public class CavePrint implements Printable{

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        /* Now we perform our rendering */
        GLJPanel caveMap = MainWindow.getCaveMap();
        System.out.println(caveMap.getSurfaceWidth());
        System.out.println(caveMap.getSurfaceHeight());

        caveMap.setupPrint(0.8,0.8, 0, -1, -1);
        caveMap.print(g);
        caveMap.releasePrint();

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
    
}
