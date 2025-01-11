/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lenovo
 */
public class TherionExport {
    
    CaveProject cp;
    
    public void export (File file) {
        FileOutputStream fos = null;
        BufferedWriter br = null;
        try {
            fos = new FileOutputStream(file);
            OutputStreamWriter osr = new OutputStreamWriter(fos, StandardCharsets.ISO_8859_1);
            br = new BufferedWriter(osr);
            br.write("encoding  utf-8");br.newLine();
            br.write("survey TesztExport -title \" Export\"");
            for(CaveSurvey survey:cp.caveSurveys){
                br.write("  centerline");br.newLine();
                br.write("# ");br.write(survey.getSurveyName());br.newLine();
                br.write("    date ");br.write(survey.getSurveyDate().replace('-', '.'));br.newLine();
                br.write("    units compass clino degrees");br.newLine();
                br.write("    data normal from to length compass clino");br.newLine();
                ArrayList<CaveEdge> data = survey.getSurveyData();
                for ( CaveEdge edge:data){
                    br.write("    ");
                    br.write(edge.getFrom().getName());br.write("  ");
                    br.write(edge.getTo().getName());br.write("  ");
                    br.write(Double.toString( edge.getLength()));br.write("  ");
                    br.write(Double.toString( edge.getAzimuth()));br.write("  ");
                    br.write(Double.toString( edge.getVertical()));;br.newLine();
                }
                br.write("  endcenterline");br.newLine();
            }
            br.write("endsurvey");br.newLine();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TherionExport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TherionExport.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(SaveCaveFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
