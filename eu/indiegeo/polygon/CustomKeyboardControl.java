package eu.indiegeo.polygon;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.NewtCameraKeyController;
import org.jzy3d.maths.Coord3d;

import com.jogamp.newt.event.KeyListener;


/**
 *
 * @author ao
 */
public class CustomKeyboardControl extends AbstractCameraController implements KeyListener, ICameraKeyController{

    private final Chart chart;

    public CustomKeyboardControl(Chart chart) {
        this.chart = chart;
        register(chart);
    }
    
    @Override
    public void register(Chart chart){
		super.register(chart);
		chart.getCanvas().addKeyController(this);
	}
	
	@Override
    public void dispose(){
		for(Chart c: targets){
		    c.getCanvas().removeKeyController(this);
		}
		super.dispose();
	}
    
	@Override
	public void keyPressed(com.jogamp.newt.event.KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
	        case KeyEvent.VK_1:
	            chart.getView().setViewPoint(new Coord3d(Math.PI / 3, Math.PI / 6, 0), true);
	            break;
	        case KeyEvent.VK_2:
	            chart.getView().setViewPoint(new Coord3d(0, 0, 0), true);
	            break;
	        case KeyEvent.VK_3:
	            chart.getView().setViewPoint(new Coord3d(0, -Math.PI, 0), true);
	            break;
	        case KeyEvent.VK_4:
	            chart.getView().setViewPoint(new Coord3d(-Math.PI / 2, 0, 0), true);
	            break;
	        case KeyEvent.VK_5:
	    		chart.getAxeLayout().setFaceDisplayed(true);
	            break;
	        case KeyEvent.VK_6:
	    		chart.getAxeLayout().setFaceDisplayed(false);
	            break;
	        default:
	            break;
	    }
	    chart.render();
	}
	@Override
	public void keyReleased(com.jogamp.newt.event.KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
