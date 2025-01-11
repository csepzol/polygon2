/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author lenovo
 */
public class ExcelExport {
    
    CaveProject cp;
    
    public void export(File file){
        //Blank workbook
        HSSFWorkbook workbook = new HSSFWorkbook(); 
        
        for (CaveSurvey survey:cp.caveSurveys){
            HSSFSheet sheet = workbook.createSheet(survey.getSurveyName());
            String[] header = survey.getSurveyHeader();
            int rownum = 0;
            Row row = sheet.createRow(rownum++);
            int cellnum = 0;
            for (String h:header){
                Cell cell = row.createCell(cellnum++);
                cell.setCellValue((String)h);
            }
            for(CaveEdge edge:survey.getSurveyData()){
                cellnum=0;
                row = sheet.createRow(rownum++);
                Cell cell = row.createCell(cellnum++);
                cell.setCellValue((String)edge.getFrom().getName());
                cell = row.createCell(cellnum++);
                cell.setCellValue((String)edge.getTo().getName());
                cell = row.createCell(cellnum++);
                cell.setCellValue(edge.getLength());
                cell = row.createCell(cellnum++);
                cell.setCellValue(edge.getAzimuth());
                cell = row.createCell(cellnum++);
                cell.setCellValue(edge.getVertical());
                cell = row.createCell(cellnum++);
                cell.setCellValue((String)edge.getLabel());
                cellnum+=4;
                for (String note : edge.getNotes()){
                    cell = row.createCell(cellnum++);
                    cell.setCellValue(note);
                }
            }
        }
         
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
            System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}
