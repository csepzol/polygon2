/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author lenovo
 */
public class CaveOverview implements GLEventListener{
    
    private GLU glu = new GLU();
    private GLUT glut = new GLUT();
    private CaveProject cp;
    double scale = 1;
    float xRot = 0;
    float yRot = -90;
    float yTrans = 0;
    float zTrans = 0;
    double[] wcoord = new double[4];
    double[] wcoord0 = new double[4];// wx, wy, wz;// returned xyz coords
    double[] wcoord1 = new double[4];
    int[] viewport = new int[4];
    double[] mvmatrix = new double[16];
    double[] projmatrix = new double[16];
    double[] zRange;
    double[] xRange;
    double[] yRange;
    int mouseX = 0;
    int mouseY = 0;
    boolean mouseClick = false;

    public void setCp(CaveProject cp) {
        this.cp = cp;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.5f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        glu.gluLookAt(1, 0, 0, 0, 0, 0, 0, 0, 1);
        gl.glEnable(GL.GL_LINE_SMOOTH);
        
        if (cp != null) {
            zRange = cp.getZRange();
            yRange = cp.getYRange();
            xRange = cp.getXRange();
            scale=getScaleFactor(drawable, gl);
        }
        
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        glut.glutWireSphere(3/scale, 10, 10);
              
//        if (mouseClick) {
//                getMousecoordinates(drawable,gl,mouseX,mouseY);
//                mouseClick = false;
//            }
        //gl.glScaled(scale, scale, scale);
        gl.glScaled(scale, scale, scale);

        gl.glRotatef(xRot, 0.0f, 0.0f, 1.0f);
        gl.glRotatef(yRot, 0.0f, 1.0f, 0.0f);
        if (cp != null){
            zRange = cp.getZRange();
            yRange = cp.getYRange();
            xRange = cp.getXRange();            
            gl.glTranslated(-(xRange[1]-(xRange[2]/2)),  -(yRange[1]-(yRange[2]/2)), -(zRange[1]-(zRange[2]/2)));
            gl.glColor3f(0.5f, 0.5f, 0.5f);
            for(CaveSurvey survey : cp.caveSurveys){
                for (CaveEdge edge : survey.getSurveyData()){
                    gl.glLineWidth(1.0f);
                    gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3f((float)edge.getFrom().getCoordinates(0), (float)edge.getFrom().getCoordinates(1), (float)edge.getFrom().getCoordinates(2));  
                    gl.glVertex3f((float)edge.getTo().getCoordinates(0), (float)edge.getTo().getCoordinates(1), (float)edge.getTo().getCoordinates(2));
                    gl.glEnd();
                    gl.glLineWidth(1);
                }
            }            
            gl.glTranslated((xRange[1]-(xRange[2]/2)),  (yRange[1]-(yRange[2]/2)),  (zRange[1]-(zRange[2]/2)));           
        }
        gl.glRotatef(-yRot, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(-xRot, 0.0f, 0.0f, 1.0f);
        
        drawRectangel(gl);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();  
        if(height <= 0)  
            height = 1;  
        
        final float h = (float) width / (float) height;  
        gl.glViewport(0, 0, width, height);  
        gl.glMatrixMode(GL2.GL_PROJECTION);  
        gl.glLoadIdentity();  

        //glu.gluPerspective(45.0f, h, 1.5, 1000.0);  
        gl.glOrtho( -width/2.f, width/2.f, -height/2.f, height/2.f, -10000, 10000 );
        gl.glMatrixMode(GL2.GL_MODELVIEW);  
        gl.glLoadIdentity();  
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
    }
    
    public double getScaleFactor(GLAutoDrawable glad, GL2 gl){
        double s=1; 
        int x = glad.getSurfaceWidth()-20;
        int y = glad.getSurfaceHeight()-20;       
//        System.out.println(Arrays.toString(xRange));
//        System.out.println(Arrays.toString(yRange));
//        System.out.println(Arrays.toString(zRange));
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
//        System.out.println("Coordinates at cursor are (" + x + ", " + y+" )");
        glu.gluUnProject((double) 0, (double) 0, 0.0, mvmatrix, 0,projmatrix, 0, viewport, 0, wcoord0, 0);
//        System.out.println("World coords at z=0.0 are ( " + wcoord0[0] + ", " + wcoord0[1] + ", " + wcoord0[2] + ")");
        glu.gluUnProject((double) x, (double) y, 0.0, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord1, 0);
//        System.out.println("World coords at z=1.0 are (" + wcoord1[0] + ", " + wcoord1[1] + ", " + wcoord1[2] + ")");
        double[] overviewSize = new double[3];
        overviewSize[0]= Math.abs(wcoord1[0]-wcoord0[0]);
        overviewSize[1]= Math.abs(wcoord1[1]-wcoord0[1]);
        overviewSize[2]= Math.abs(wcoord1[2]-wcoord0[2]);
        double overviewSizeMax = Collections.max(Arrays.asList(ArrayUtils.toObject(overviewSize)));
        double max =  Math.max(Math.max(xRange[2],yRange[2]),zRange[2]);
        s = overviewSizeMax / max;
        return s;
    }
    
    public double[] getMousecoordinates(GLAutoDrawable glad, GL2 gl, int x, int y){
        double[] result = new double[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
        int realy = viewport[3] - (int) y - 1;
        glu.gluUnProject((double) x, (double) realy, 0.0, mvmatrix, 0, projmatrix, 0, viewport, 0, result, 0);
        
        float a = (float) ((glad.getSurfaceWidth()-0)/2 - x);
        float b = (float) ((glad.getSurfaceHeight()-0)/2 - realy);
        yTrans=a;
        zTrans=b;
        MainWindow.getCr().yTrans = a;
        MainWindow.getCr().zTrans = b;
        MainWindow.getCaveMap().repaint();
        //System.out.println(yTrans + " " + zTrans);
        return result;
    }
    
    public void drawRectangel (GL2 gl) {
        gl.glTranslated(0, -yTrans, -zTrans);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glLineWidth(3.0f);
        gl.glBegin(GL2.GL_LINE_LOOP);
//        wcoord[1]+=10;
//        wcoord[2]+=10;
        gl.glVertex3d( wcoord[0], -wcoord[1], -wcoord[2]);
        gl.glVertex3d( wcoord[0], -wcoord[1], wcoord[2]);
        gl.glVertex3d( wcoord[0], wcoord[1], wcoord[2]);
        gl.glVertex3d( wcoord[0], wcoord[1], -wcoord[2]);
//        gl.glVertex3d( 0, -10, -10);
//        gl.glVertex3d( 0, -10, 10);
//        gl.glVertex3d( 0, 10, 10);
//        gl.glVertex3d( 0, 10, -10);
        gl.glEnd();
        gl.glLineWidth(1.0f);
        glut.glutWireSphere(3/scale, 10, 10);
        gl.glTranslated(0, yTrans, zTrans);
    }
    
}
