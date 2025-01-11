package eu.indiegeo.polygon;

import java.util.Arrays;

public class CavePoint {
	
	private String name = "";
	private double[] coordinates = new double[3];
	double[] eovCoordinates = new double[3];
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public CavePoint(String name) {
		this.name=name;
		Arrays.fill(coordinates, 0);
		Arrays.fill(eovCoordinates, 0);
	}
	
	public double[] getCoordinates() {
		return coordinates;
	}
	
	public double getCoordinates(int index) {
		return coordinates[index];
	}

	public void setCoordinates(double[] coordinates) {
		this.coordinates = coordinates;
	}

	public double[] getEovCoordinates() {
		return eovCoordinates;
	}
	
	public double getEovCoordinates(int index) {
		return eovCoordinates[index];
	}

	public void setEovCoordinates(double[] eovCoordinates) {
		this.eovCoordinates = eovCoordinates;
	}
	
}
