package eu.indiegeo.polygon;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.NewtMouseUtilities;
import org.jzy3d.chart.controllers.mouse.camera.NewtCameraMouseController;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Renderer2d;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.opengl.glu.GLU;

public class CustomMouseController extends NewtCameraMouseController{
	
	private final Chart chart;
	public static GLU gluObj = new GLU();
	

    public CustomMouseController(Chart chart) {
        this.chart = chart;
    }
    
    /** Compute shift or rotate*/
	@Override
    public void mouseDragged(MouseEvent e) {
		Coord2d mouse = new Coord2d(e.getX(),e.getY());
		// Rotate
				if(isLeftDown(e)){
					Coord2d move  = mouse.sub(prevMouse).div(100);
					rotate( move );
				}
				// Shift
				else if(isRightDown(e)){
					Coord2d move  = mouse.sub(prevMouse);
//					if(move.y!=0)
//						shift(move.y/500);
					for (Chart c : targets) {
//						System.out.println(c.getScale().toString());
//						System.out.println(c.getView().getBounds().get.toString());
//						c.getView().se
						//System.out.println(c.getViewPoint().x + " " +c.getViewPoint().y+" "+c.getViewPoint().z);
						org.jzy3d.maths.Scale currentZ = c.getView().getScale();
				        org.jzy3d.maths.Scale newScaleZ = currentZ.add((move.y/500) * currentZ.getRange());
				        org.jzy3d.maths.Scale currentX = new org.jzy3d.maths.Scale(c.getView().getBounds().getXmin(), c.getView().getBounds().getXmax());
				        org.jzy3d.maths.Scale newScaleX = currentX.add((move.x/500) * currentX.getRange());			            
						c.getView().setScaleZ(newScaleZ);
						c.getView().setScaleX(newScaleX);
						//c.getView().setScaleY(newScale2);
					}
				}
		chart.getView().updateBounds();
		prevMouse = mouse;
	}
	
	/** Compute zoom */
	@Override
    public void mouseWheelMoved(MouseEvent e) {
		stopThreadController();
		float factor = NewtMouseUtilities.convertWheelRotation(e, 1.0f, 10.0f);
		//System.out.println(chart.getScene().getGraph().getTransform().toString());
		zoomX(factor);
		zoomY(factor);
		zoomZ(factor);
		Coord3d origo = chart.getView().getCamera().modelToScreen(chart.getView().getCurrentGL(), gluObj, new Coord3d(0,0,0));
		Coord3d scale = chart.getView().getCamera().modelToScreen(chart.getView().getCurrentGL(), gluObj, new Coord3d(10,0,0));
		double distance = Math.sqrt(Math.pow(scale.x-origo.x, 2) + Math.pow(scale.y-origo.y, 2));
		//Utils.addScale(chart, MainWindow.r2d, (int)distance);
		System.out.println(distance);
		chart.getView().updateBounds();
	}

}
