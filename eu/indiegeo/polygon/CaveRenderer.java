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
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.lang3.ArrayUtils;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;

/**
 *
 * @author lenovo
 */
public class CaveRenderer implements GLEventListener {
    
    private boolean debug = false;
    private GLU glu = new GLU();
    GLUT glut = new GLUT();
    private CaveProject cp;
    float xRot = 0;
    float yRot = -90;
    float yTrans = 0;
    float zTrans = 0;
    float yTransOverview = 0;
    float zTransOverview = 0;
    double scale = 6;
    float[] bckgrdRGB = {0.0f, 0.0f, 0.0f};
    boolean rotable = true;
    boolean entranceView = false;
    int lineWidth = 1;
    MouseEvent mouse;
    int showText = 0;
    int[] viewport = new int[4];
    double[] mvmatrix = new double[16];
    double[] projmatrix = new double[16];
    int realy = 0;// GL y coord pos
    double[] wcoord0 = new double[4];// wx, wy, wz;// returned xyz coords
    double[] wcoord1 = new double[4];
    int selectedPointX, selectedPointY;
    CavePoint selectedPoint;
    boolean showSelectedPoint = false;
    boolean findSelectedPoint = false;
    boolean showLoop = false;
    ArrayList<CaveEdge> loopEdges = new ArrayList<>();
    int scaleLength = 10;
    int coloringMode = 1;
    float[] caveRGB = {1.0f, 1.0f, 1.0f};
    float[] result = new float[2];
    ArrayList<java.awt.Color> surveyColors= new ArrayList<>();
    private CaveOverview co;
    boolean fittToScreen = false;

    public void setCp(CaveProject cp) {
        this.cp = cp;
    }

    public void setOverview(CaveOverview co) {
        this.co=co;
        co.setCp(cp);
    }
    
    public static void renderLine(GLAutoDrawable glautodrawable){
        
    }

    @Override
    public void init(GLAutoDrawable glad) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void display(GLAutoDrawable glad) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (debug) { 
            System.out.println(0.0254/90.0*glad.getSurfaceWidth());
            System.out.println(0.0254/90.0*glad.getSurfaceHeight());
            System.out.println("Gldisplay");
        }
        final GL2 gl = glad.getGL().getGL2();
        gl.glClearColor(bckgrdRGB[0], bckgrdRGB[1], bckgrdRGB[2], 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        glu.gluLookAt(1, 0, 0, 0, 0, 0, 0, 0, 1);
        gl.glEnable(GL.GL_LINE_SMOOTH);
        
//        if( scale < 1 ){
//            scale=1;
//        }
        if (cp != null && fittToScreen) {
            scale= getScaleFactor(glad, gl);
            fittToScreen = false;
        }
        gl.glScaled(scale, scale, scale);

        getOverviewRectangle(glad, gl);
        gl.glTranslated(0, yTrans, zTrans);
        if ( co != null ) {
            co.yTrans = yTrans;
            co.zTrans = zTrans;
            co.xRot = xRot;
            co.yRot = yRot;
            MainWindow.getCaveOverview().repaint();
        }
        scaleLength=getScaleLength(gl);

                
        //gl.glRotatef(xRot, 0.0f, 0.0f, 1.0f);
         if(MainWindow.getBasePlanRadioButton().isSelected()){
            gl.glRotatef(xRot, 1.0f, 0.0f, 0.0f);
        } else{
            gl.glRotatef(xRot, 0.0f, 0.0f, 1.0f);
        }
        gl.glRotatef(yRot, 0.0f, 1.0f, 0.0f);
        MainWindow.getxRotField().setText(Float.toString(xRot));
        MainWindow.getyRotField().setText(Float.toString(yRot));
        //gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        if (cp != null){
            double[] zRange = cp.getZRange();
            double[] yRange = cp.getYRange();
            double[] xRange = cp.getXRange();
            ColorMapper color = new ColorMapper(new ColorMapRainbow(),zRange[0],zRange[1]);
            gl.glTranslated(-(xRange[1]-(xRange[2]/2)),  -(yRange[1]-(yRange[2]/2)), -(zRange[1]-(zRange[2]/2)));
            if (entranceView) {
                CavePoint entrance = cp.findCavePointByID("0");
                gl.glColor3f(1.0f-bckgrdRGB[0], 1.0f-bckgrdRGB[1], 1.0f-bckgrdRGB[2]);
                //glu.gluSphere(quad, yTrans, showText, realy);
                glut.glutSolidSphere(0.5, 10, 10);
            }
            if (coloringMode == 2 ){
                gl.glColor3f(caveRGB[0], caveRGB[1], caveRGB[2]);
            }
            int ci = 0; 
            for(CaveSurvey survey : cp.caveSurveys){
                if (coloringMode == 3){
                    java.awt.Color c = this.surveyColors.get(ci);
                    gl.glColor3f(c.getRed()/255, c.getGreen()/255, c.getBlue()/255);
                    ci++;
                }
                for (CaveEdge edge : survey.getSurveyData()){
                    gl.glLineWidth(lineWidth);
                    if (coloringMode == 1) {
                        Color c = color.getColor(edge.getFrom().getCoordinates(2));
                        gl.glColor3f(c.r, c.g, c.b);
                    }
                    gl.glBegin(GL2.GL_LINES);                    
                    gl.glVertex3f((float)edge.getFrom().getCoordinates(0), (float)edge.getFrom().getCoordinates(1), (float)edge.getFrom().getCoordinates(2));  
                    gl.glVertex3f((float)edge.getTo().getCoordinates(0), (float)edge.getTo().getCoordinates(1), (float)edge.getTo().getCoordinates(2));
                    gl.glEnd();
                    gl.glLineWidth(1);
                    switch (showText){
                        case 1 : 
                            drawText(gl, edge.getTo().getName(),(float)edge.getTo().getCoordinates(0), (float)edge.getTo().getCoordinates(1), (float)edge.getTo().getCoordinates(2));
                            break;
                        case 2:
                            drawText(gl, String.format("%.1f",edge.getTo().getCoordinates(2)),(float)edge.getTo().getCoordinates(0), (float)edge.getTo().getCoordinates(1), (float)edge.getTo().getCoordinates(2));
                            break;
                        case 3:
                            drawText(gl, String.format("%.1f",edge.getTo().getEovCoordinates(2)),(float)edge.getTo().getCoordinates(0), (float)edge.getTo().getCoordinates(1), (float)edge.getTo().getCoordinates(2));
                            break;
                        default:
                            break;
                    }
                }
            }
            findCavePoint(gl, mouse);
            mouse = null;
            if (showSelectedPoint){
                showSelectedPoint(gl, selectedPointX, selectedPointY);
            }
            
            if (showLoop) {
                gl.glLineWidth(5);
                gl.glColor3f(1.0f, 0, 0);
                gl.glBegin(GL.GL_LINE_LOOP);
                gl.glVertex3d(loopEdges.get(0).getFrom().getCoordinates(0), loopEdges.get(0).getFrom().getCoordinates(1), loopEdges.get(0).getFrom().getCoordinates(2));
                loopEdges.forEach((edge) -> {
                    gl.glVertex3d(edge.getTo().getCoordinates(0), edge.getTo().getCoordinates(1), edge.getTo().getCoordinates(2));
                });
                gl.glEnd();
            }
            
            gl.glTranslated((xRange[1]-(xRange[2]/2)),  (yRange[1]-(yRange[2]/2)),  (zRange[1]-(zRange[2]/2)));
        }
                      
        if (debug) {
            axes(gl);
        } 
        
        switchTo2DOrtho(glad);
        
        gl.glColor3f(0.8f, 0.8f, 0.8f);
        gl.glRasterPos2i((int)(scaleLength+5)/2-12, 20);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "10 m");
        
        gl.glBegin(GL2.GL_QUADS); // Begin drawing quads
        gl.glVertex2f(5.0f, 15.0f); // Top left vertex
        gl.glVertex2f( scaleLength + 5, 15.0f); // Top right vertex
        gl.glVertex2f( scaleLength + 5, 5.0f); // Bottom right vertex
        gl.glVertex2f(5.0f, 5.0f); // Bottom left vertex
        gl.glEnd(); // Finish drawing quads

        switchBackToOriginal(glad);
        gl.glFlush();
        //MainWindow.getCaveOverview().repaint();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (debug) { 
            System.out.println("GLreshape");
        }
        GL2 gl = glad.getGL().getGL2();  
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
        
        gl.glClearColor(bckgrdRGB[0], bckgrdRGB[1], bckgrdRGB[2], 0.5f);
    }
    
    public void drawText(GL2 gl, String text, float x, float y, float z) {
        gl.glRasterPos3f(x, y, z);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, text);
    }
    
    public void findCavePoint(GL2 gl, MouseEvent mouse){
        if (mouse != null && mouse.isShiftDown()){
            int x = mouse.getX(), y = mouse.getY();
            gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
            gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
            gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
            realy = viewport[3] - (int) y - 1;
            //System.out.println("Coordinates at cursor are (" + x + ", " + realy);
            glu.gluUnProject((double) x, (double) realy, 0.0, mvmatrix, 0,projmatrix, 0, viewport, 0, wcoord0, 0);
            //System.out.println("World coords at z=0.0 are ( " + wcoord0[0] + ", " + wcoord0[1] + ", " + wcoord0[2] + ")");
            glu.gluUnProject((double) x, (double) realy, 1.0, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord1, 0);
            //System.out.println("World coords at z=1.0 are (" + wcoord1[0] + ", " + wcoord1[1] + ", " + wcoord1[2] + ")");
            CavePoint closestPoint = cp.findClosestPointToMouse(wcoord0, wcoord1);
            //System.out.println(closestPoint.getName());
            MainWindow.getActualPointName().setText(closestPoint.getName());
            MainWindow.getActualPointData().setText(cp.getClosestPointSurvey());
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glTranslatef((float)closestPoint.getCoordinates(0), (float)closestPoint.getCoordinates(1), (float)closestPoint.getCoordinates(2));
            if (scale > 30) {
                glut.glutWireSphere(0.1, 10, 10);
            } else {
                glut.glutWireSphere(0.3, 10, 10);
            }
            gl.glTranslatef((float)-closestPoint.getCoordinates(0), (float)-closestPoint.getCoordinates(1), (float)-closestPoint.getCoordinates(2));            
            switch (mouse.getButton()) {
                case MouseEvent.BUTTON1:                    
                    break;
                case MouseEvent.BUTTON2:
                    break;
                default:
                    break;
            }
        }
    }
    
    public void showSelectedPoint(GL2 gl, int x, int y){
        if ( findSelectedPoint ) {
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
        realy = viewport[3] - (int) y - 1;
        glu.gluUnProject((double) x, (double) realy, 0.0, mvmatrix, 0,projmatrix, 0, viewport, 0, wcoord0, 0);
        glu.gluUnProject((double) x, (double) realy, 1.0, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord1, 0);
        selectedPoint = cp.findClosestPointToMouse(wcoord0, wcoord1);
        findSelectedPoint = false;
        }
        MainWindow.getActualPointName().setText(selectedPoint.getName());
        StringBuilder str = new StringBuilder();
        str.append(cp.getClosestPointSurvey()+" - EOV: ");
        double[] eov = selectedPoint.getEovCoordinates();
        double[] coordinates = selectedPoint.getCoordinates();
        for (double i: eov) {
            str.append(String.format("%.2f  ", i));
        }
        str.append(String.format("Mélység: %.2f", coordinates[2]));
        CaveEdge ce = cp.findCaveEdge(selectedPoint.getName());
        if (ce != null){
            str.append("  |  From: " + ce.getFrom().getName() + "  Távolság: " +ce.getLength()+ "  Irányszög: " +ce.getAzimuth()+ "  Lejtszög: " +ce.getVertical() + "  Megjegyzés: " + (ce.getNotes().size()>0?ce.getNotes().get(0):""));
        }
        MainWindow.getActualPointData().setText(str.toString());
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glTranslatef((float)selectedPoint.getCoordinates(0), (float)selectedPoint.getCoordinates(1), (float)selectedPoint.getCoordinates(2));
        glut.glutWireSphere(0.3, 10, 10);
        gl.glTranslatef((float)-selectedPoint.getCoordinates(0), (float)-selectedPoint.getCoordinates(1), (float)-selectedPoint.getCoordinates(2));     
    }
    
    public int getScaleLength(GL2 gl) {
        double[] result0 = new double[4];
        double[] result10 = new double[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
        glu.gluProject(0, 0, 0, mvmatrix, 0,projmatrix, 0, viewport, 0, result0, 0);
        glu.gluProject(0, 10, 0, mvmatrix, 0,projmatrix, 0, viewport, 0, result10, 0);
        Vektor a = new Vektor();
        a.x = result10[0] - result0[0];
        a.y = result10[1] - result0[1];
        a.z = result10[2] - result0[2];
        return (int) Vektor.Norma(a);
    }
    
    public double getScaleFactor(GLAutoDrawable glad, GL2 gl){
        double s=1; 
        int x = glad.getSurfaceWidth()/2;
        int y = glad.getSurfaceHeight()/2;
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
        double[] zRange = cp.getZRange();
        double[] yRange = cp.getYRange();
        double[] xRange = cp.getXRange();
        double max =  Math.max(Math.max(xRange[2],yRange[2]),zRange[2]);
        s = overviewSizeMax / max;
        return s;
    }
    
    public void getOverviewRectangle(GLAutoDrawable glad, GL2 gl){
        int x = glad.getSurfaceWidth();
        int y = glad.getSurfaceHeight();
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
        realy = viewport[3] - (int) y - 1;
        realy= y;
//        System.out.println("Coordinates at cursor are (" + x + ", " + realy);
        glu.gluUnProject((double) 0, (double) 0, 0.0, mvmatrix, 0,projmatrix, 0, viewport, 0, wcoord0, 0);
//        System.out.println("World coords at z=0.0 are ( " + wcoord0[0] + ", " + wcoord0[1] + ", " + wcoord0[2] + ")");
        glu.gluUnProject((double) x, (double) realy, 0.0, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord1, 0);
//        System.out.println("World coords at z=1.0 are (" + wcoord1[0] + ", " + wcoord1[1] + ", " + wcoord1[2] + ")");
        if (co!=null){
            co.wcoord = wcoord1;
        }
    }
    
    public void switchTo2DOrtho(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrthof(0, drawable.getSurfaceWidth(), 0, drawable.getSurfaceHeight(), -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void switchBackToOriginal(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }
    
    public void gomb(GL2 gl, double r, int szel, int hossz) {
    int i, j;
       for(i = 0; i <= szel; i++) {
          double szel0 = Math.PI * (-0.5 + (double) (i - 1) / szel);
          double z0  = Math.sin(szel0)*r;
          double zr0 =  Math.cos(szel0)*r;

          double szel1 = Math.PI * (-0.5 + (double) i / szel);
          double z1 = Math.sin(szel1)*r;
         double zr1 = Math.cos(szel1)*r;

         gl.glBegin(gl.GL_QUAD_STRIP);
         gl.glColor3f(0.07f, 0.13f, 0.18f);
         for(j = 0; j <= hossz; j++) {
             double hossz0 = 2 * Math.PI * (double) (j - 1) / hossz;
             double x = Math.cos(hossz0);
             double y = Math.sin(hossz0);

              gl.glNormal3d(x * zr0, y * zr0, z0);
              gl.glVertex3d(x * zr0, y * zr0, z0);
              gl.glNormal3d(x * zr1, y * zr1, z1);
              gl.glVertex3d(x * zr1, y * zr1, z1);
          }
          gl.glEnd();
      }
    }
    
    public void axes (GL2 gl) {
        gl.glBegin(GL2.GL_LINES); 
        gl.glColor3f(1f, 0f, 0f);
        gl.glVertex3f(0f,0f,0f);  
        gl.glVertex3f(10f,0f, 0f);  
        gl.glEnd();  

              //3D  
        gl.glBegin(GL2.GL_LINES); 
        gl.glColor3f(0f, 1f, 0f);
        gl.glVertex3f(0f,0f,0f);  
        gl.glVertex3f(0f,10f,0f);  
        gl.glEnd();  

              //top  
        gl.glBegin(GL2.GL_LINES);
        gl.glColor3f(0f, 0f, 1f);
        gl.glVertex3f(0f,0f,0f);  
        gl.glVertex3f(0f,0f,10f);  
        gl.glEnd();
    }
    
}
