package eu.indiegeo.polygon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JFrame;

public class ReadCaveFile {
	
	private File caveFile;
	CaveProject cp = new CaveProject();
	
	ReadCaveFile(File selectedFile){
		this.caveFile = selectedFile;
		readData(this.caveFile);
	}
	
	
	public void readData(File caveFile) {
		BufferedReader br = null;
		boolean isSurveys = false;
		boolean isPoints = false;
		CaveSurvey actualSurvey = new CaveSurvey();
		int surveyCounter = 0;
		try {
                        FileInputStream fis = new FileInputStream(caveFile);
                        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.ISO_8859_1);
                        br = new BufferedReader(isr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Nem sikerült a beolvasás!");
		} 
		
		String line="";
		try {
			while (br.ready()) {
				line = br.readLine();
				//TODO move to CaveProject class
				if ( line.contains( "*** Project ***" ) ) {
					cp.setProjectName(Utils.getLineData(br.readLine()));
					cp.setProjectPlace(Utils.getLineData(br.readLine()));
					String projectCode = Utils.getLineData(br.readLine());
					cp.setProjectCode(projectCode.isEmpty()?"0":projectCode);
					cp.setMadeBy(Utils.getLineData(br.readLine()));
					cp.setMadeDate(Utils.parseExcelDateToHumanFormat(Utils.getLineData(br.readLine().replaceAll(",", "."))));
					cp.setLastModification(Utils.getLineData(br.readLine()));
					cp.setAutoCorrect(Integer.parseInt(Utils.getLineData(br.readLine())));
					cp.setAutoSize(Utils.getLineData(br.readLine()));
				} else if ( line.contains("*** Surveys ***" ) ) {
					isSurveys = true;
				} else if ( line.contains("*** Surface ***") ) {
					isSurveys = false;					
				}
				if ( isSurveys ) {
					if ( line.contains("Survey name:") ) {
						actualSurvey.setSurveyName(Utils.getLineData(line));
						surveyCounter++;
					}
					if ( line.contains("Survey team:") ) {
						String[] team = actualSurvey.getSurveyTeam();
						team[0] = Utils.getLineData(line);
						for (int i = 1; i<team.length; i++) {
							team[i] = br.readLine();
						}
						actualSurvey.setSurveyTeam(team);
					}
					if ( line.contains("Survey date:") ) {
						actualSurvey.setSurveyDate(Utils.parseExcelDateToHumanFormat(line.substring(12).replaceAll(",", ".")));
					}
					if (line.contains("Declination:") ) {
						actualSurvey.setDeclination(Double.parseDouble(line.substring(12).replaceAll(",", ".")));
					}
					if ( line.contains("Instruments:") ) {
						Instruments[] instruments = actualSurvey.getInstruments();
						for (int i = 0; i<instruments.length; i++) {
							String instrumentLine = br.readLine();
							String[] instrumentLineSplit = instrumentLine.split("\\t");
							instruments[i] = new Instruments( instrumentLineSplit[0], Double.parseDouble(instrumentLineSplit[1]) );
						}
						actualSurvey.setInstruments(instruments);
					}
					if (line.contains("Fix point:") ) {
						if (line.length()>11) {
							FixPoint fixPoint= new FixPoint();
							String[] splitLine=line.split(":\\s+");
							fixPoint.setName(splitLine[1]);
							String actualFPLine = br.readLine();
							String[] splitFPLine = actualFPLine.split("\\t");
							fixPoint.setCoordinates(1, splitFPLine[0]);
							fixPoint.setCoordinates(0, splitFPLine[1]);
							fixPoint.setCoordinates(2, splitFPLine[2]);
//							for (int i=0; i<3; i++) {
//								fixPoint.setCoordinates(i, splitFPLine[i]);
//							}
							actualSurvey.setFixPoint(fixPoint);
						}
					}
					if (isPoints && line.length() != 0) {
						String[] splitPointLine = line.split("\\t");
						CaveEdge actualEdge = new CaveEdge();
						actualEdge.setFrom(new CavePoint(splitPointLine[0]));
						actualEdge.setTo(new CavePoint(splitPointLine[1]));
						actualEdge.setLength(Double.parseDouble(splitPointLine[2].replaceAll(",", ".")));
						actualEdge.setAzimuth(Double.parseDouble(splitPointLine[3].replaceAll(",", ".")));
						actualEdge.setVertical(Double.parseDouble(splitPointLine[4].replaceAll(",", ".")));
                                                actualEdge.calculateProjectedLength();
						//5-Label
						//6-7-8-9 -Right Left Up down
						for (int i=10; i<splitPointLine.length; i++) {
							actualEdge.addNotes(splitPointLine[i]);
						}
						actualSurvey.addCaveEdge(actualEdge);
					}
					if ( line.contains("Survey data") ) {
						isPoints = true;
						line=br.readLine();
						String[]  headerSplitLine = line.split("\\t");
                                                String[] header = {"From", "To", "Length", "Azimuth", "Vertical", "Notes"};
						actualSurvey.setSurveyHeader(header);
					}
					
					if (isPoints && line.length() == 0) {
						isPoints = false;
						cp.addCaveSurvey(actualSurvey);
						actualSurvey = new CaveSurvey();
						//actualSurvey.flushCaveEdges();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	
}
