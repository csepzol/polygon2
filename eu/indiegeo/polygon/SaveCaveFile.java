/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lenovo
 */
public class SaveCaveFile {
    private File caveFile;
    private CaveProject cp = new CaveProject();

    public File getCaveFile() {
        return caveFile;
    }

    public void setCaveFile(File caveFile) {
        this.caveFile = caveFile;
    }

    public CaveProject getCp() {
        return cp;
    }

    public void setCp(CaveProject cp) {
        this.cp = cp;
    }
    
    public SaveCaveFile(File caveFile, CaveProject cp){
        FileOutputStream fos = null;
        BufferedWriter br = null;
        try {
            fos = new FileOutputStream(caveFile);
            OutputStreamWriter osr = new OutputStreamWriter(fos, StandardCharsets.ISO_8859_1);
            br = new BufferedWriter(osr);
            br.write("POLYGON Cave Surveying Software");br.newLine();
            br.write("Polygon Program Version   = 2");br.newLine();
            br.write("Polygon Data File Version = 1");br.newLine();
            br.write("1998-2001 ===> Prepostffy Zsolt");br.newLine();
            br.write("-------------------------------");br.newLine();
            br.newLine();
            br.write("*** Project ***");br.newLine();
            br.write("Project name: " + cp.projectName);br.newLine();
            br.write("Project place: " + cp.projectPlace);br.newLine();
            br.write("Project code: " + cp.projectCode);br.newLine();
            br.write("Made by: " + cp.madeBy);br.newLine();
            br.write("Made date: " + Utils.parseHumanDateToExcelFormat(cp.madeDate));br.newLine();
            br.write("Last modi: " + cp.lastModification);br.newLine();
            br.write("AutoCorrect: " + cp.autoCorrect);br.newLine();
            br.write("AutoSize: " + cp.autoSize);br.newLine();
            br.newLine();
            br.write("*** Surveys ***");br.newLine();
            for (CaveSurvey survey:cp.caveSurveys) {
                br.write("Survey name: " + survey.getSurveyName());br.newLine();
                String[] surveyTeam = survey.getSurveyTeam();
                br.write("Survey team: " + surveyTeam[0]);br.newLine();
                for (int i=1; i<surveyTeam.length; i++){
                    br.write(surveyTeam[i]);br.newLine();
                }
                br.write("Survey date: " + Utils.parseHumanDateToExcelFormat(survey.getSurveyDate()));br.newLine();
                br.write("Declination: " + survey.getDeclination());br.newLine();
                br.write("Instruments: " );br.newLine();
                Instruments[] instruments = survey.getInstruments();
                for (int i=0; i<instruments.length; i++){
                    br.write( instruments[i].getType() + " \t"+ instruments[i].getCorrection());br.newLine();
                }
                if (survey.getFixPoint() != null) {
                    br.write("Fix point: " + survey.getFixPoint().getName());br.newLine();
                    double[] fixPointCoordinates = survey.getFixPoint().getCoordinates();
                    br.write(fixPointCoordinates[0] + "\t" +fixPointCoordinates[1] + "\t" + fixPointCoordinates[2]+ "\t0\t0\t0\t0");br.newLine();
                } else {
                    br.write("Fix point: ");br.newLine();
                    double[] fixPointCoordinates = new double[3];
                    br.write(fixPointCoordinates[0] + "\t" +fixPointCoordinates[1] + "\t" + fixPointCoordinates[2]+ "\t0\t0\t0\t0");br.newLine();
                }
                br.write("Survey data");br.newLine();
                String[] surveyHeader = survey.getSurveyHeader();
                for (int i=0; i<surveyHeader.length-1; i++){
                    br.write(surveyHeader[i]+"\t");
                }
                br.write(surveyHeader[surveyHeader.length-1]);br.newLine();
                for (CaveEdge actualEdge : survey.getSurveyData()){
                    br.write(actualEdge.getFrom().getName() + "\t");
                    br.write(actualEdge.getTo().getName() + "\t");
                    br.write(actualEdge.getLength() + "\t");
                    br.write(actualEdge.getAzimuth() + "\t");
                    br.write(actualEdge.getVertical() + "\t");
                    br.write(actualEdge.getLabel() + "\t");
                    br.write("0\t0\t0\t0");
                    for (String actualNote : actualEdge.getNotes()){
                        br.write("\t"+actualNote);
                    }
                    br.newLine();
                }
                br.newLine();
            }
            br.write("End of survey data.");br.newLine();
            br.newLine();
            br.write("*** Surface ***");br.newLine();
            br.write("End of surface data.");br.newLine();
            br.newLine();
            br.write("EOF.");br.newLine();
            br.newLine();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SaveCaveFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SaveCaveFile.class.getName()).log(Level.SEVERE, null, ex);
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
