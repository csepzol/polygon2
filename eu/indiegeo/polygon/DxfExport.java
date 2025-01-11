/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lenovo
 */
public class DxfExport {
    DXFDocument document = new DXFDocument("Cave Export");
    DXFGraphics graphics = document.getGraphics();
    
    //TODO make 3d export only works in 2D
    
    public void generateOutput(CaveProject cp) {
        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(3));
        for (CaveSurvey survey:cp.caveSurveys) {
            for (CaveEdge edge:survey.getSurveyData()) {
                graphics.drawLine(edge.getFrom().getEovCoordinates(1), -edge.getFrom().getEovCoordinates(0), 
                        edge.getTo().getEovCoordinates(1), -edge.getTo().getEovCoordinates(0));
                System.out.println(edge.getFrom().getEovCoordinates(0) +" "+ edge.getFrom().getEovCoordinates(1)+" "+ 
                        edge.getTo().getEovCoordinates(0)+" "+ edge.getTo().getEovCoordinates(1));
            }
        }
    } 
    
    public void saveToFile(File file){
        FileWriter fileWriter = null;
        try {
            String dxfText = document.toDXFString();
            fileWriter = new FileWriter(file.getAbsolutePath());
            fileWriter.write(dxfText);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(DxfExport.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(DxfExport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
