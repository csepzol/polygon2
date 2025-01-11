package eu.indiegeo.polygon;

public class MeridianConvergence {
	private double x;
	private double y;
	private double azimuth;
	
	MeridianConvergence(double x, double y, double azimuth){
		this.x = x;
		this.y = y;
		this.azimuth = azimuth;
	}
	
	public double calculateNewAzimuth() {
		double newAzimuth;
		double convergence = Math.atan((Math.cosh((x-200000)/6379296.41898993)*Math.sin((y-650000)/6379296.41898993))/((1/Math.tan(0.82205))-Math.sinh((x-200000)/6379296.41898993)*Math.cos((y-650000)/6379296.41898993)))*180/Math.PI;
		newAzimuth = azimuth-convergence;
		return newAzimuth;
	}
	

}
