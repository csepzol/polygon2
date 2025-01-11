/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lenovo
 */
public class ReadSvxFile {
    
    private File surveyFile;
    CaveSurvey cs = new CaveSurvey();
    
    ReadSvxFile (File selectedFile){
        this.surveyFile = selectedFile;
        readData();
    }
    
    private void readData() {
        BufferedReader br = null;
        boolean isSurveys = false;
        boolean isPoints = false;
        CaveSurvey actualSurvey = new CaveSurvey();
        int surveyCounter = 0;
        String[] header = {"From", "To", "Length", "Azimuth", "Vertical", "Notes"};
        cs.setSurveyHeader(header);
        try {
                FileInputStream fis = new FileInputStream(surveyFile);
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.ISO_8859_1);
                br = new BufferedReader(isr);
        } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Nem sikerült a beolvasás!");
        } 

        String line = "";
        
        try {
            while (br.ready()) {
                line = br.readLine();
                line = line.trim();
                if( !line.startsWith(";") ){
                    if( line.contains("*begin") ){
                        cs.setSurveyName(line.substring(6, line.length()));
                    } else if (line.contains("*date")){
                        cs.setSurveyDate(line.substring(6, line.length()).replace(".", "-"));
                    } else if ( !line.startsWith("*") ) {
                        String[] split = line.split("\\s+");
                        if (split.length > 4){
                            CaveEdge newEdge = new CaveEdge();
                            CavePoint newFrom = new CavePoint(split[0]);
                            if (split[0] == "0"){
                                double init[] = {0,0,0};
                                newFrom.setCoordinates(init);
                            }
                            CavePoint newTo = new CavePoint(split[1]);
                            newEdge.setFrom(newFrom);
                            newEdge.setTo(newTo);
                            newEdge.setLength(Double.parseDouble(split[2]));
                            newEdge.setAzimuth(Double.parseDouble(split[3]));
                            newEdge.setVertical(Double.parseDouble(split[4]));
                            ArrayList<String> notes = new ArrayList<String>();
                            String[] split1 = line.split(";");
                            if ( split1.length > 1 ) {
                                notes.add(split1[1]);
                            }
                            newEdge.setNotes(notes);
                            cs.addCaveEdge(newEdge);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ReadSvxFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
