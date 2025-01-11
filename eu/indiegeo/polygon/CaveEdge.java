package eu.indiegeo.polygon;

import java.util.ArrayList;

public class CaveEdge {
	
	CavePoint from;
	CavePoint to;
	double length;
	double azimuth;
	double vertical;
        double projectedLength;
        double meridianConvergence;
	String label = "";
	ArrayList<String> notes = new ArrayList<>();
	
	public CaveEdge() {
		super();
	}
	public CaveEdge(CavePoint from, CavePoint to, double length, double azimuth, double vertical) {
		super();
		this.from = from;
		this.to = to;
		this.length = length;
		this.azimuth = azimuth;
		this.vertical = vertical;
                this.projectedLength=length* Math.cos(vertical);
	}

        public double getMeridianConvergence() {
            return meridianConvergence;
        }

        public void setMeridianConvergence(double meridianConvergence) {
            this.meridianConvergence = meridianConvergence;
        }

	public CavePoint getFrom() {
		return from;
	}
        
        public void calculateProjectedLength() {
            this.projectedLength=length* Math.cos(azimuth);
        }

	public void setFrom(CavePoint from) {
		this.from = from;
	}

	public CavePoint getTo() {
		return to;
	}

	public void setTo(CavePoint to) {
		this.to = to;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}

	public double getVertical() {
		return vertical;
	}

	public void setVertical(double vertical) {
		this.vertical = vertical;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ArrayList<String> getNotes() {
		return notes;
	}

	public void setNotes(ArrayList<String> notes) {
		this.notes = notes;
	}
	
	public void addNotes(String note) {
		this.notes.add(note);
	}
        
        @Override
        public String toString(){
            return String.format(this.from.getName() + " " + this.to.getName());
        }
	
        public void edgeSwap(){
            CavePoint temp = this.from;
            this.from = this.to;
            this.to = temp;
            this.vertical = -1 * this.vertical;
            this.azimuth = (this.azimuth + 180) % 360;
        }
}
