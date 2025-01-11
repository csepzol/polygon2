package eu.indiegeo.polygon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;


public class EovWgsConverter {
	private int x;
	private int y;
	private double lat;
	private double lon;
	
	EovWgsConverter (String x, String y) {
            if (x.contains(".") || y.contains(".")){
                this.x = Integer.parseInt(x.substring(0, x.indexOf(".")));
                this.y = Integer.parseInt(y.substring(0, y.indexOf(".")));
            } else {
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
            }
            System.out.println(this.x);
            System.out.println(this.y);
	}
	
	public EovWgsConverter(int i, int j) {
		// TODO Auto-generated constructor stub
		this.x = i;
		this.y = j;
	}

	public void convert() {
            try {
                FileInputStream file = new FileInputStream(new File(".\\resources\\eovWGS.xls"));
                HSSFWorkbook workbook = new HSSFWorkbook(file);
                HSSFSheet sheet = workbook.getSheetAt(0);
                workbook.setForceFormulaRecalculation(true);
                Cell cell;
                cell = sheet.getRow(5).getCell(3);
                cell.setCellValue(Integer.toString(this.x));
                cell = sheet.getRow(5).getCell(2);
                cell.setCellValue(Integer.toString(this.y));
                workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
                file.close();
                this.lat = sheet.getRow(5).getCell(6).getNumericCellValue();
                this.lon = sheet.getRow(5).getCell(7).getNumericCellValue();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EovWgsConverter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EovWgsConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
            
//		WorkBook workBook = new WorkBook();
//		try {
//			workBook.readXLSX(".\\resources\\eovWGS.xlsx");
//			workBook.setSheet(0);
//			workBook.setText(5, 3, Integer.toString(this.x));
//			workBook.setText(5, 2, Integer.toString(this.y));
//			this.lat = Double.parseDouble(workBook.getText(5, 6, true).replaceAll(",", "."));
//			this.lon = Double.parseDouble(workBook.getText(5, 7, true).replaceAll(",", "."));
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	}

	public double getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

}
