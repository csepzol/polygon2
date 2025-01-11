package eu.indiegeo.polygon;

import java.util.ArrayList;

public class CaveSurvey {
	private String surveyName;
	private String[] surveyTeam = new String[] {"", "", "", "", "", ""};
	private String surveyDate;
	private double declination;
	private Instruments[] instruments = new Instruments[3];
	private FixPoint fixPoint = null;
        private String fixPointComment = "";
	private String[] surveyHeader;
	private ArrayList<CaveEdge> surveyData = new ArrayList<CaveEdge>();

        public CaveSurvey() {
            this.surveyHeader = new String[]{"From", "To", "Length", "Azimuth", "Vertical", "Note"};
        }

        public String getFixPointComment() {
            return fixPointComment;
        }

        public void setFixPointComment(String fixPointComment) {
            this.fixPointComment = fixPointComment;
        }        
        
	public String getSurveyName() {
		return surveyName;
	}
	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}
	public String[] getSurveyTeam() {
		return surveyTeam;
	}
	public void setSurveyTeam(String[] surveyTeam) {
		this.surveyTeam = surveyTeam;
	}
	public String getSurveyDate() {
		return surveyDate;
	}
	public void setSurveyDate(String surveyDate) {
		this.surveyDate = surveyDate;
	}
	public double getDeclination() {
		return declination;
	}
	public void setDeclination(double declination) {
		this.declination = declination;
	}
	public Instruments[] getInstruments() {
		return instruments;
	}
	public void setInstruments(Instruments[] instruments) {
		this.instruments = instruments;
	}
	public FixPoint getFixPoint() {
		return fixPoint;
	}
	public void setFixPoint(FixPoint fixPoint) {
		this.fixPoint = fixPoint;
	}
	public ArrayList<CaveEdge> getSurveyData() {
		return surveyData;
	}
	public void setSurveyData(ArrayList<CaveEdge> surveyData) {
		this.surveyData = surveyData;
	}
	public String[] getSurveyHeader() {
		return surveyHeader;
	}
	public void setSurveyHeader(String[] surveyHeader) {
		this.surveyHeader = surveyHeader;
	}
	
	public void addCaveEdge(CaveEdge edge) {
		this.surveyData.add(edge);
	}
	
	public void flushCaveEdges() {
		this.surveyData.clear();
	}
}
