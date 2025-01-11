/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.indiegeo.polygon;

/**
 *
 * @author Csépe Zoltán
 */
public class Vektor {
        double x;
        double y;
        double z;
        
        

        public Vektor(){
            x=0;
            y=0;
            z=0;
        }
        public  static Vektor vektorialisSzorzat(Vektor a, Vektor b){
            Vektor c= new Vektor();
            c.x=a.y*b.z-b.y*a.z;
            c.y=-(a.x*b.z-b.x*a.z);
            c.z=a.x*b.y-b.x*a.y;
            return c;
        }

        public static double Norma(Vektor a){
            double s;
            s=Math.sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
            return s;
        }

        public static double skalarisSzorzat(Vektor a, Vektor b){
            double h;
            h=a.x*b.x+a.y*b.y+a.z*b.z;
            return h;
       }
}
