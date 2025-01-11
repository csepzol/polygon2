package eu.indiegeo.polygon;

import datechooser.beans.DateChooserCombo;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


import javax.swing.JPanel;
import org.jgrapht.alg.cycle.QueueBFSFundamentalCycleBasis;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


public class Utils {
	
	static HashMap<String, FixPoint> fixedPointsMap;
	static HashMap<String, CavePoint> pointsMap;
	

	public static String getLineData(String line) {
		String result = "";
		String[] lineSplit = line.split(": ");
		if (lineSplit.length == 2) {
			result = lineSplit[1];
		} else {
			result = "";
		}
		return result;
	}
	
	public static String getTabbedLineData(String line, int index){
		String result = "";
                if (line != null) {
                    String[] lineSplit = line.split("\t");
                    if ( index < lineSplit.length) {
                            if (lineSplit[index] != null ) {
                                    result = lineSplit[index];
                            }
                    }
                }
		return result;
	}
	
	public static String parseExcelDateToHumanFormat(String date) {
		return LocalDate.of( 1899 , Month.DECEMBER , 30 ).plusDays( (long) Double.parseDouble( date ) ).toString(); 
	}
        
        public static String parseHumanDateToExcelFormat (String date) {
            LocalDate d1 = LocalDate.parse(date);
            LocalDate d2 = LocalDate.of(1899, Month.DECEMBER, 30);
            long between = ChronoUnit.DAYS.between(d2, d1);
            return Long.toString(between);
        }
	
	public static void clearCavesTree(JTree tree) {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            root.removeAllChildren();
            model.reload();
	}
	
	public static void addCavesToTree(JTree tree, CaveProject cp) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode cave = new DefaultMutableTreeNode(cp.projectName);
		root.add(cave);
		for (int i=0; i<cp.caveSurveys.size(); i++) {
			cave.add(new DefaultMutableTreeNode((cp.caveSurveys.get(i).getSurveyName())));
		}
		model.reload(root);
		//model.insertNodeInto(new DefaultMutableTreeNode(cp.projectName), root, root.getChildCount());
		expandAllTree(tree);
	}
	
	public static void expandAllTree(JTree tree) {
		for (int i=0; i<tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}
	
	public static void fillTableWithData(JTable table, CaveSurvey cs) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setColumnCount(cs.getSurveyHeader().length);
		model.setColumnIdentifiers(cs.getSurveyHeader());
		ArrayList<CaveEdge> data = cs.getSurveyData();
		removeAllRowFromTable(table);
		for (int i=0; i<data.size(); i++) {
			CaveEdge actual = data.get(i);
			Object[] tempRow = new Object[]{actual.getFrom().getName(), actual.getTo().getName(), actual.getLength(), 
					actual.getAzimuth(), actual.getVertical()};
			ArrayList<Object> actualRow = new ArrayList<>(Arrays.asList(tempRow));
			for (int j=0; j < actual.getNotes().size(); j++ ) {
				String actualNote = actual.getNotes().get(j);
				if ( actualNote != null && !actualNote.isEmpty()) {
					actualRow.add(actualNote);
				} else {
					actualRow.add("");
				}
			}
			model.addRow(actualRow.toArray());
		}
                table.getColumnModel().getColumn(5).setPreferredWidth(500);
	}
	
	public static void removeAllRowFromTable (JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowCount = model.getRowCount();
		//Remove rows one by one from the end of the table
		for (int i = rowCount - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}
        
        public static void removeNewColumnsFromDataTable (JTable table) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setColumnCount(6);
        }
	
	public static int[] dateParser(String date) {
		int[] out = new int[3];
		String[] dateSplit = date.split("-");
		for (int i=0; i<dateSplit.length; i++) {
			out[i] = Integer.parseInt(dateSplit[i]);
		}
		return out;
	}
	
	
	public static HashMap<String, Component> getSurveyPanelComponents() {
            HashMap<String, Component> componentsIds = new HashMap<String, Component>();
            JPanel panel = MainWindow.getSurveyPanel();
            Component[] components = panel.getComponents();
            for (Component component : components) {
                componentsIds.put(component.getName(), component);
            }
		
            return componentsIds;
	}
	
	public static void fillSurveyPanelWithData(CaveSurvey survey) {
		MainWindow.getSurveyNameField().setText(survey.getSurveyName());
		MainWindow.getDeclinationField().setText(Double.toString(survey.getDeclination()));
		if (survey.getFixPoint() != null) {
			MainWindow.getFixPointCheckbox().setSelected(true);
			MainWindow.getFixedPointIdField().setText(survey.getFixPoint().getName());
			MainWindow.getxField().setText(Double.toString(survey.getFixPoint().coordinates[0]));
			MainWindow.getyField().setText(Double.toString(survey.getFixPoint().coordinates[1]));
			MainWindow.getzField().setText(Double.toString(survey.getFixPoint().coordinates[2]));
		} else {
			MainWindow.getFixPointCheckbox().setSelected(false);
			MainWindow.getFixedPointIdField().setText("");
			MainWindow.getxField().setText("");
			MainWindow.getyField().setText("");
			MainWindow.getzField().setText("");
		}
                MainWindow.getFixPointCommentField().setText(survey.getFixPointComment());
                MainWindow.getSurveyPointCount().setText(Integer.toString(survey.getSurveyData().size()) + " db");
                double fullLength = 0;
                for(CaveEdge edge:survey.getSurveyData()){
                    fullLength+= edge.getLength();
                }
                MainWindow.getSurveyFullLength().setText(String.format("%.2f méter", fullLength));
		int[] actualDate = dateParser(survey.getSurveyDate());
		DateChooserCombo datepicker = MainWindow.getSurveyDatePicker();
                Calendar d = Calendar.getInstance();
                d.set(actualDate[0], actualDate[1]-1, actualDate[2]);
		datepicker.setSelectedDate(d);
                JTable surveyerTable = MainWindow.getSurveyerTable();
                DefaultTableModel model = (DefaultTableModel) surveyerTable.getModel();
                String[] team = survey.getSurveyTeam();
                System.out.println(Arrays.toString(team));
                MainWindow.getSurveyTeamField().setText(team[0]);
                for (int i=1; i< team.length; i++){
                    String[] split = team[i].split("\t");
                    if (split.length>1){
                        model.setValueAt(split[0], i-1, 0);
                        model.setValueAt(split[1], i-1, 1);
                    }
                }
                try{
                Instruments[] instruments = survey.getInstruments();
                MainWindow.getLengthComboBox().setSelectedItem(instruments[0].getType());
                MainWindow.getCompassComboBox().setSelectedItem(instruments[1].getType());
                MainWindow.getInclinoComboBox().setSelectedItem(instruments[2].getType());
                MainWindow.getLengthCorrectionField().setText(Double.toString(instruments[0].getCorrection()));
                MainWindow.getCompassCorrectionField().setText(Double.toString(instruments[1].getCorrection()));
                MainWindow.getInclinoCorrectionField().setText(Double.toString(instruments[2].getCorrection()));
                } catch(Exception ex){
                    System.out.println("instrumentexception");
                }
                
	}
	
	public static void saveSurveyPanelData(CaveSurvey survey) {
		survey.setSurveyName(MainWindow.getSurveyNameField().getText());
		survey.setDeclination(Double.parseDouble(MainWindow.getDeclinationField().getText()));
		if (MainWindow.getFixPointCheckbox().isSelected()) {
			FixPoint fp = new FixPoint();
			fp.setName(MainWindow.getFixedPointIdField().getText());
			fp.setCoordinates(0, MainWindow.getxField().getText());
			fp.setCoordinates(1, MainWindow.getyField().getText());
			fp.setCoordinates(2, MainWindow.getzField().getText());
                        survey.setFixPointComment(MainWindow.getFixPointCommentField().getText());
			survey.setFixPoint(fp);
		} else {
			survey.setFixPoint(null);
		}
		DateChooserCombo datepicker = MainWindow.getSurveyDatePicker();
		survey.setSurveyDate(datepicker.getText());
                JTable surveyerTable = MainWindow.getSurveyerTable();
                DefaultTableModel model = (DefaultTableModel) surveyerTable.getModel();
                String[] team = new String[6];
                team[0]= MainWindow.getSurveyTeamField().getText();
                for (int i=0; i< model.getRowCount(); i++){
                    team[i+1]=model.getValueAt(i, 0) + "\t" + model.getValueAt(i, 1);
                }
                Instruments[] inst = new Instruments[] {new Instruments(), new Instruments(), new Instruments()};
                inst[0].setType(MainWindow.getLengthComboBox().getSelectedItem().toString());
                inst[0].setCorrection(Double.parseDouble(MainWindow.getLengthCorrectionField().getText()));
                inst[1].setType(MainWindow.getCompassComboBox().getSelectedItem().toString());
                inst[1].setCorrection(Double.parseDouble(MainWindow.getCompassCorrectionField().getText()));
                inst[2].setType(MainWindow.getInclinoComboBox().getSelectedItem().toString());
                inst[2].setCorrection(Double.parseDouble(MainWindow.getInclinoCorrectionField().getText()));
		survey.setInstruments(inst);
	}
	
	public static void clearSurveyPanelData() {
		MainWindow.getSurveyNameField().setText("");
		MainWindow.getDeclinationField().setText("0");
		MainWindow.getFixPointCheckbox().setSelected(false);
		MainWindow.getFixedPointIdField().setText("");
		MainWindow.getxField().setText("");
		MainWindow.getyField().setText("");
		MainWindow.getzField().setText("");
                MainWindow.getSurveyFullLength().setText("-");
                MainWindow.getSurveyPointCount().setText("-");
		DateChooserCombo datepicker = MainWindow.getSurveyDatePicker();
                Calendar d = Calendar.getInstance();
                datepicker.setSelectedDate(d);
                MainWindow.getSurveyTeamField().setText("");
                JTable surveyerTable = MainWindow.getSurveyerTable();
                DefaultTableModel model = (DefaultTableModel) surveyerTable.getModel();
                for (int i=0; i< model.getRowCount(); i++){
                    model.setValueAt("",i, 0);
                    model.setValueAt("",i, 1);
                }
                MainWindow.getLengthComboBox().setSelectedIndex(0);
                MainWindow.getCompassComboBox().setSelectedIndex(0);
                MainWindow.getInclinoComboBox().setSelectedIndex(0);
                MainWindow.getLengthCorrectionField().setText(Double.toString(0));
                MainWindow.getCompassCorrectionField().setText(Double.toString(0));
                MainWindow.getInclinoCorrectionField().setText(Double.toString(0));
	}
	
	public static void addSurveyToCaveProject(CaveProject cp) {
		CaveSurvey survey = new CaveSurvey();
		survey.setSurveyName(MainWindow.getSurveyNameField().getText());
		survey.setDeclination(Double.parseDouble(MainWindow.getDeclinationField().getText()));
		if (MainWindow.getFixPointCheckbox().isSelected()) {
			FixPoint fp = new FixPoint();
			fp.setName(MainWindow.getFixedPointIdField().getText());
			fp.setCoordinates(0, MainWindow.getxField().getText());
			fp.setCoordinates(1, MainWindow.getyField().getText());
			fp.setCoordinates(2, MainWindow.getzField().getText());
			survey.setFixPoint(fp);
		} else {
			survey.setFixPoint(null);
		}
		String[] team = new String[6];
                team[0] = MainWindow.getSurveyTeamField().getText();
                for(int i=0; i < MainWindow.getSurveyerTable().getRowCount(); i++){
                    if (MainWindow.getSurveyerTable().getModel().getValueAt(i, 0) != ""){
                        team[i+1] = MainWindow.getSurveyerTable().getModel().getValueAt(i, 0) + "\t" + MainWindow.getSurveyerTable().getModel().getValueAt(i, 1); 
                    } else {
                        team[i+1] = "";
                    }
                }
		survey.setSurveyTeam(team);
		Instruments[] inst = new Instruments[] {new Instruments(), new Instruments(), new Instruments()};
		inst[0].setType(MainWindow.getLengthComboBox().getSelectedItem().toString());
                inst[0].setCorrection(Double.parseDouble(MainWindow.getLengthCorrectionField().getText()));
                inst[1].setType(MainWindow.getCompassComboBox().getSelectedItem().toString());
                inst[1].setCorrection(Double.parseDouble(MainWindow.getCompassCorrectionField().getText()));
                inst[2].setType(MainWindow.getInclinoComboBox().getSelectedItem().toString());
                inst[2].setCorrection(Double.parseDouble(MainWindow.getInclinoCorrectionField().getText()));
                survey.setInstruments(inst);
		DateChooserCombo datepicker = MainWindow.getSurveyDatePicker();
		survey.setSurveyDate(datepicker.getText());
		cp.addCaveSurvey(survey);
	}
	
	public static void fillProjectPanelWithData(CaveProject cp) {
		MainWindow.getProjectNameField().setText(cp.getProjectName());
		MainWindow.getProjectPlaceField().setText(cp.getProjectPlace());
                MainWindow.getProjectCodeField().setText(cp.getProjectCode());
                double[] zRange = cp.getZRange();
                if (zRange[2]!=Double.MAX_VALUE) {
                    MainWindow.getProjectDepth().setText(String.format("%.2f méter", zRange[0]));
                    MainWindow.getProjectAltitude().setText(String.format("%.2f méter", zRange[1]));
                    MainWindow.getProjectVertical().setText(String.format("%.2f méter", zRange[2]));
                    MainWindow.getProjectFullLength().setText(String.format("%.2f méter", cp.getProjectFullLength()));
                }
                int count=0;
                ArrayList<Date> dateList = new ArrayList<Date>();
                for (CaveSurvey survey : cp.caveSurveys){
                    count += survey.getSurveyData().size();
                    try {                    
                        dateList.add(new SimpleDateFormat("yyyy-MM-dd").parse(survey.getSurveyDate()));
                    } catch (ParseException ex) {
                        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                MainWindow.getProjectPointCount().setText(Integer.toString(count));
                if(count != 0){
                    MainWindow.getProjectEndDate().setText(new SimpleDateFormat("yyyy-MM-dd").format(Collections.max(dateList)));
                    MainWindow.getProjectStartDate().setText(new SimpleDateFormat("yyyy-MM-dd").format(Collections.min(dateList)));
                }
	}
        
        public static void clearProjectPanel() {
		MainWindow.getProjectNameField().setText("");
		MainWindow.getProjectPlaceField().setText("");
                MainWindow.getProjectCodeField().setText("");
                MainWindow.getProjectDepth().setText("-");
                MainWindow.getProjectAltitude().setText("-");
                MainWindow.getProjectVertical().setText("-");
                MainWindow.getProjectPointCount().setText("-");
                MainWindow.getProjectEndDate().setText("-");
                MainWindow.getProjectStartDate().setText("-");
                MainWindow.getProjectFullLength().setText("-");
	}
	
	public static void saveProjectPanelData (CaveProject cp) {
//		HashMap<String, Component> componentsIds = getProjectPanelComponents();
		cp.setProjectName(MainWindow.getProjectNameField().getText());
		cp.setProjectPlace(MainWindow.getProjectPlaceField().getText());
                cp.setProjectCode(MainWindow.getProjectCodeField().getText());
	}
	
	public static void calculateFixedCoordinates(CaveProject cp) {
		fixedPointsMap = new HashMap<String,FixPoint>();
		pointsMap = new HashMap<String,CavePoint>();
		CavePoint basePoint = new CavePoint("base");
		for (int i = 0; i < cp.caveSurveys.size(); i++) {
			CaveSurvey actualSurvey = cp.caveSurveys.get(i);
			FixPoint fp = actualSurvey.getFixPoint();
			if (fp != null) {
				fixedPointsMap.put(fp.getName(), fp);
			}
		}
		for (CaveSurvey actualSurvey : cp.caveSurveys) {
//			CaveSurvey actualSurvey = cp.caveSurveys.get(i);
//			ArrayList<CaveEdge> actualData = actualSurvey.getSurveyData();
			for (CaveEdge actualEdge : actualSurvey.getSurveyData()) {
//				CaveEdge actualEdge = actualData.get(j);
				if (actualEdge.getFrom().getName().equals("0") || fixedPointsMap.containsKey(actualEdge.getFrom().getName())) {
					actualEdge.getFrom().setCoordinates(new double[] {0,0,0});
					if (fixedPointsMap.containsKey(actualEdge.getFrom().getName())) {
						FixPoint actualEOV = (FixPoint)fixedPointsMap.get(actualEdge.getFrom().getName());
						actualEdge.getFrom().setEovCoordinates(actualEOV.getCoordinates());
					}
					pointsMap.put(actualEdge.getFrom().getName(), actualEdge.getFrom());
					basePoint.setEovCoordinates(actualEdge.getFrom().getEovCoordinates());
				} else {
                                        if ((CavePoint) pointsMap.get(actualEdge.getFrom().getName()) != null ){
                                            CavePoint actualTo = (CavePoint) pointsMap.get(actualEdge.getFrom().getName());
//                                            System.out.println(actualTo.getName());
                                            actualEdge.setFrom(actualTo);
                                        } else {
                                            CavePoint actualTo = (CavePoint) pointsMap.get(actualEdge.getTo().getName());
//                                            System.out.println(actualTo.getName());
                                            actualEdge.setTo(actualTo);
                                            actualEdge.edgeSwap();
                                        }
				}
                                //TODO edge swap
//                                System.out.print(actualEdge.getFrom().getName() + " " );
//                                System.out.println(actualEdge.getTo().getName());
				double x = actualEdge.getFrom().getCoordinates(0) + (Math.cos(Math.toRadians(actualEdge.getAzimuth() + actualSurvey.getDeclination() + actualEdge.getMeridianConvergence())) * Math.cos(Math.toRadians(actualEdge.getVertical())) * actualEdge.getLength());
				double y = actualEdge.getFrom().getCoordinates(1) + (Math.sin(Math.toRadians(actualEdge.getAzimuth() + actualSurvey.getDeclination() + actualEdge.getMeridianConvergence())) * Math.cos(Math.toRadians(actualEdge.getVertical())) * actualEdge.getLength());
				double z = actualEdge.getFrom().getCoordinates(2) + ( Math.sin(Math.toRadians(actualEdge.getVertical())) * actualEdge.getLength());
				actualEdge.getTo().setCoordinates(new double[] {x,y,z});
				actualEdge.getTo().setEovCoordinates(new double[] 
						{basePoint.getEovCoordinates(0)+x, basePoint.getEovCoordinates(1)+y, basePoint.getEovCoordinates(2)+z});
				pointsMap.put(actualEdge.getTo().getName(), actualEdge.getTo());
			}
		}
	}
	
	

	public static void fillCoordinatesTable(JTable table, CaveSurvey cs) {
		// TODO Auto-generated method stub
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		ArrayList<CaveEdge> data = cs.getSurveyData();
		removeAllRowFromTable(table);
                DecimalFormat formatter = new DecimalFormat("#0.000");
                DecimalFormat wgsFormatter = new DecimalFormat("#0.00000000");
		for (int i=0; i<data.size(); i++) {
			CaveEdge actual = data.get(i);
			double[] eov = actual.getTo().getEovCoordinates();
			ArrayList<Object> actualRow = new ArrayList<>();
			actualRow.add(actual.getTo().getName());
			actualRow.add(formatter.format(actual.getTo().getCoordinates(0)));
			actualRow.add(formatter.format(actual.getTo().getCoordinates(1)));
			actualRow.add(formatter.format(actual.getTo().getCoordinates(2)));
			for (double i1 : eov) {
				actualRow.add(formatter.format(i1));
			}
			EovWgsConverter ewc = new EovWgsConverter((int)eov[0], (int)eov[1]);
			ewc.convert();
			actualRow.add(wgsFormatter.format(ewc.getLat()));
			actualRow.add(wgsFormatter.format(ewc.getLon()));
			actualRow.add(formatter.format(eov[2]));
			model.addRow(actualRow.toArray());
		}
	}
	
	public static void fillMeridianTable(JTable table, CaveSurvey cs) {
		// TODO Auto-generated method stub
		DefaultTableModel model = (DefaultTableModel) table.getModel();
//		ArrayList<CaveEdge> data = cs.getSurveyData();
		removeAllRowFromTable(table);
		for (CaveEdge actual: cs.getSurveyData()) {
//			CaveEdge actual = data.get(i);
			double azimuth = actual.getAzimuth();
			ArrayList<Object> actualRow = new ArrayList<>();
			actualRow.add(actual.getFrom().getName());
			actualRow.add(actual.getTo().getName());
			actualRow.add(actual.getAzimuth());
			MeridianConvergence mc = new MeridianConvergence(actual.getTo().getEovCoordinates(0), actual.getTo().getEovCoordinates(1), azimuth);
			actualRow.add(mc.calculateNewAzimuth());
                        actual.setMeridianConvergence(mc.calculateNewAzimuth() - actual.getAzimuth());
//                        System.out.println(mc.calculateNewAzimuth() - actual.getAzimuth());
			model.addRow(actualRow.toArray());
		}
	}
        
        public static Set<List<DefaultEdge>> getLoopsInCave(CaveProject cp){
            Set<List<DefaultEdge>> cycles = null;
            SimpleGraph<String, DefaultEdge> graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
            graph.addVertex("0");
             for (CaveSurvey survey:cp.caveSurveys){
                for (CaveEdge edge:survey.getSurveyData()){
                    graph.addVertex(edge.getTo().getName());
                }
             }
             for (CaveSurvey survey:cp.caveSurveys){
                for (CaveEdge edge:survey.getSurveyData()){
                    graph.addEdge(edge.getFrom().getName(), edge.getTo().getName());
                }
            }            
            QueueBFSFundamentalCycleBasis<String, DefaultEdge> cycleDetector = new QueueBFSFundamentalCycleBasis<>(graph);
            CycleBasisAlgorithm.CycleBasis<String, DefaultEdge> cycleBasis = cycleDetector.getCycleBasis();
            cycles = cycleBasis.getCycles();
            MainWindow.setGraph(graph);
                   
            return cycles;
        }
}
