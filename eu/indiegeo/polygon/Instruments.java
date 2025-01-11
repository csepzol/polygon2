package eu.indiegeo.polygon;

public class Instruments {
	private String type = "";
	private double correction = 0.0;
	
	public Instruments() {
		this.type = "";
		this.correction = 0.0;
	}
	
	public Instruments(String type, double correction) {
		super();
		this.setType(type);
		this.setCorrection(correction);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getCorrection() {
		return correction;
	}

	public void setCorrection(double correction) {
		this.correction = correction;
	}
	
}
