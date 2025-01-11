package eu.indiegeo.polygon;

enum projections{
	WGS84,
	EOV
}

public class FixPoint {
	String name;
	double[] coordinates = new double[3];
	projections projection = projections.EOV;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double[] getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(double[] coordinates) {
		this.coordinates = coordinates;
	}
	
	public void setCoordinates(int index, String coordinate) {
		this.coordinates[index]=Double.parseDouble(coordinate.replace(",", "."));
	}
	public projections getProjection() {
		return projection;
	}
	public void setProjection(projections projection) {
		this.projection = projection;
	}
	
	public String toString() {
		return name + " " + coordinates[0] + " " + coordinates[1] + " " + coordinates[2];
	}
}
