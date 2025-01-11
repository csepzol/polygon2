package eu.indiegeo.polygon;

import java.util.ArrayList;

public class CaveProject {
	String projectName;
	String projectPlace;
	String projectCode;
	String madeBy;
	String madeDate; //TODO make date format
	String lastModification;
	int autoCorrect;
	String autoSize;
        private String closestPointSurvey = "";
	ArrayList<CaveSurvey> caveSurveys = new ArrayList<CaveSurvey>();
	
	public CaveProject() {
		this.projectName="";
	}
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
            System.out.println(projectName);
            MainWindow.setCaveTitle(projectName);
		this.projectName = projectName;
	}
	public String getProjectPlace() {
		return projectPlace;
	}
	public void setProjectPlace(String projectPlace) {
		this.projectPlace = projectPlace;
	}
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public String getMadeBy() {
		return madeBy;
	}
	public void setMadeBy(String madeBy) {
		this.madeBy = madeBy;
	}
	public String getMadeDate() {
		return madeDate;
	}
	public void setMadeDate(String madeDate) {
		this.madeDate = madeDate;
	}
	public String getLastModification() {
		return lastModification;
	}
	public void setLastModification(String lastModification) {
		this.lastModification = lastModification;
	}
	public int getAutoCorrect() {
		return autoCorrect;
	}
	public void setAutoCorrect(int autoCorrect) {
		this.autoCorrect = autoCorrect;
	}
	public String getAutoSize() {
		return autoSize;
	}
	public void setAutoSize(String autoSize) {
		this.autoSize = autoSize;
	}
	
	public void addCaveSurvey(CaveSurvey cs) {
		this.caveSurveys.add(cs);
	}
	
	public CaveSurvey findSurveyByName(String name) {
		for (int i=0; i<this.caveSurveys.size(); i++) {
			if (this.caveSurveys.get(i).getSurveyName().equals(name)) {
				return this.caveSurveys.get(i);
			}
		}
		
		return null;
	}
	
        public double[] getZRange(){
            double[] result = new double[3];
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (CaveSurvey survey : this.caveSurveys){
                for (CaveEdge actualEdge : survey.getSurveyData()) {
                    min = min < actualEdge.getTo().getCoordinates(2) ? min : actualEdge.getTo().getCoordinates(2);
                    max = max > actualEdge.getTo().getCoordinates(2) ? max : actualEdge.getTo().getCoordinates(2);
                    min = min < actualEdge.getFrom().getCoordinates(2) ? min : actualEdge.getFrom().getCoordinates(2);
                    max = max > actualEdge.getFrom().getCoordinates(2) ? max : actualEdge.getFrom().getCoordinates(2);
                }
            }
            result[0] = min;
            result[1] = max;
            result[2] = Math.abs(max-min);
            return result;
        }
        
        public double[] getXRange(){
            double[] result = new double[3];
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (CaveSurvey survey : this.caveSurveys){
                for (CaveEdge actualEdge : survey.getSurveyData()) {
                    min = min < actualEdge.getTo().getCoordinates(0) ? min : actualEdge.getTo().getCoordinates(0);
                    max = max > actualEdge.getTo().getCoordinates(0) ? max : actualEdge.getTo().getCoordinates(0);
                    min = min < actualEdge.getFrom().getCoordinates(0) ? min : actualEdge.getFrom().getCoordinates(0);
                    max = max > actualEdge.getFrom().getCoordinates(0) ? max : actualEdge.getFrom().getCoordinates(0);
                }
            }
            result[0] = min;
            result[1] = max;
            result[2] = Math.abs(max-min);
            return result;
        }
        
        public double[] getYRange(){
            double[] result = new double[3];
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (CaveSurvey survey : this.caveSurveys){
                for (CaveEdge actualEdge : survey.getSurveyData()) {
                    min = min < actualEdge.getTo().getCoordinates(1) ? min : actualEdge.getTo().getCoordinates(1);
                    max = max > actualEdge.getTo().getCoordinates(1) ? max : actualEdge.getTo().getCoordinates(1);
                    min = min < actualEdge.getFrom().getCoordinates(1) ? min : actualEdge.getFrom().getCoordinates(1);
                    max = max > actualEdge.getFrom().getCoordinates(1) ? max : actualEdge.getFrom().getCoordinates(1);
                }
            }
            result[0] = min;
            result[1] = max;
            result[2] = Math.abs(max-min);
            return result;
        }
        
        public CavePoint findCavePointByID(String ID){
            for (CaveSurvey survey:this.caveSurveys){
                for ( CaveEdge edge:survey.getSurveyData()) {
                    if (edge.getFrom().getName().equals(ID)) {
                        return edge.getFrom();
                    } else if (edge.getTo().getName().equals(ID)) {
                        return edge.getTo();
                    }
                }
            }
            return null;
        }
        
        public CavePoint findClosestPointToMouse(double[] wcoord0, double[] wcoord1){
            CavePoint result = null;
            Vektor a = new Vektor();
            Vektor b = new Vektor();
            Vektor c = new Vektor();
            double distance = Double.MAX_VALUE;
            for (CaveSurvey survey:this.caveSurveys){
                for (CaveEdge edge:survey.getSurveyData()){
                    // TO
                    a.x = edge.getTo().getCoordinates(0) - wcoord0[0];
                    a.y = edge.getTo().getCoordinates(1) - wcoord0[1];
                    a.z = edge.getTo().getCoordinates(2) - wcoord0[2];
                    b.x = wcoord1[0] - wcoord0[0];
                    b.y = wcoord1[1] - wcoord0[1];
                    b.z = wcoord1[2] - wcoord0[2];
                    c = Vektor.vektorialisSzorzat(a, b);
                    if ( (Vektor.Norma(c)/Vektor.Norma(b))< distance){
                        result=edge.getTo();
                        closestPointSurvey = survey.getSurveyName();
                        distance = Vektor.Norma(c)/Vektor.Norma(b);
                    }
                    //FROM
                     a.x = edge.getFrom().getCoordinates(0) - wcoord0[0];
                    a.y = edge.getFrom().getCoordinates(1) - wcoord0[1];
                    a.z = edge.getFrom().getCoordinates(2) - wcoord0[2];
                    b.x = wcoord1[0] - wcoord0[0];
                    b.y = wcoord1[1] - wcoord0[1];
                    b.z = wcoord1[2] - wcoord0[2];
                    c = Vektor.vektorialisSzorzat(a, b);
                    if ( (Vektor.Norma(c)/Vektor.Norma(b))< distance){
                        result=edge.getFrom();
                        closestPointSurvey = survey.getSurveyName();
                        distance = Vektor.Norma(c)/Vektor.Norma(b);
                    }
                }
            }
            return result;
        }
        
        public CaveEdge findCaveEdge(String fromName, String toName){
            CaveEdge result = null;
            for(CaveSurvey survey : this.caveSurveys){
                for ( CaveEdge edge: survey.getSurveyData()){
                    if (edge.getFrom().getName() == fromName && edge.getTo().getName() == toName) {
                        return edge;
                    } else if (edge.getFrom().getName() == toName && edge.getTo().getName() == fromName) {
                        return edge;
                    }
                }
            }
            return result;
        }
        
        public CaveEdge findCaveEdge( String toName){
            CaveEdge result = null;
            for(CaveSurvey survey : this.caveSurveys){
                for ( CaveEdge edge: survey.getSurveyData()){
                    if (edge.getTo().getName() == toName) {
                        return edge;
                    }
                }
            }
            return result;
        }
        
        public double getProjectFullLength(){
            double length=0;
            for(CaveSurvey survey : this.caveSurveys){
                for ( CaveEdge edge: survey.getSurveyData()){
                    length+=edge.getLength();
                }
            }
            return length;
        }
        
        public String getClosestPointSurvey() {
            return this.closestPointSurvey;
        }
}
