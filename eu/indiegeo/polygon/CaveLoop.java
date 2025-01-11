/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author lenovo
 */
public class CaveLoop {
    ArrayList<CaveEdge> loopEdges = new ArrayList<>();
    double loopLength=0;
    double loopProjectedLength=0;
    double xError = 0;
    double yError = 0;
    double zError = 0;
    double error = 0;
    CavePoint[] pointLine;
    CavePoint start;
    CavePoint end;

    public CavePoint getStart() {
        return start;
    }

    public CavePoint getEnd() {
        return end;
    }

    public double getxError() {
        return xError;
    }

    public double getyError() {
        return yError;
    }

    public double getzError() {
        return zError;
    }

    public double getError() {
        return error;
    }
    
    
    
    public CaveLoop(ArrayList<CaveEdge> loopEdges) {
        this.loopEdges = loopEdges;
        this.calculateLoopLength();
        this.calculateProjectedLoopLength();
        this.findStartEnd();
        this.calculateErrors();
    }    

    public ArrayList<CaveEdge> getLoopEdges() {
        return this.loopEdges;
    }
       
    private void calculateLoopLength() {
        for(int i=0; i<loopEdges.size(); i++) {
            CaveEdge edge = loopEdges.get(i);
            loopLength += edge.getLength();
        }
    }
    
    private void calculateProjectedLoopLength() {
        this.loopEdges.forEach((edge) -> {
            loopProjectedLength += Math.abs(edge.projectedLength);
        });
    }
    
    private void findStartEnd() {
        HashMap<String, Integer> pointsMap = new HashMap<String, Integer>();
        ArrayList<CaveEdge> loopEdgesPart = new ArrayList<>();
        loopEdgesPart.add(loopEdges.get(0));
        loopEdgesPart.add(loopEdges.get(loopEdges.size()-1));
        for(CaveEdge edge : loopEdgesPart){
            if (pointsMap.containsKey(edge.getFrom().getName())) {
                pointsMap.put(edge.getFrom().getName(), pointsMap.get(edge.getFrom().getName())+1);
            } else {
                pointsMap.put(edge.getFrom().getName(), 1);
            }
            if (pointsMap.containsKey(edge.getTo().getName())) {
                pointsMap.put(edge.getTo().getName(), pointsMap.get(edge.getTo().getName())+1);
            } else {
                pointsMap.put(edge.getTo().getName(), 1);
            }
        }
        String mainPointName = "";
        int mainPointFreq = Integer.MIN_VALUE;
        for (String point : pointsMap.keySet()) {
            if (pointsMap.get(point) > mainPointFreq) {
                mainPointFreq = pointsMap.get(point);
                mainPointName = point;
            }            
        }
        pointLine = new CavePoint[loopEdges.size()+1];
        if (loopEdges.get(0).getFrom().getName().trim().equals(mainPointName)){
            pointLine[0] = loopEdges.get(0).getFrom();
        } else {
            pointLine[0] = loopEdges.get(0).getTo();
        }        
        for (int i=1; i<loopEdges.size(); i++) {
            CaveEdge beforeEdge = loopEdges.get(i-1);
            CaveEdge actualEdge = loopEdges.get(i);
            if ( !actualEdge.getFrom().getName().trim().equals(beforeEdge.getTo().getName().trim()) ){
//                CavePoint temp = loopEdges.get(i).from;
//                loopEdges.get(i).from = loopEdges.get(i).to;
//                loopEdges.get(i).to = temp;
                loopEdges.get(i).edgeSwap();
                
            }
        }
        start = loopEdges.get(0).getFrom();
        end = loopEdges.get(loopEdges.size()-1).getTo();
        
    }
    
    private void calculateErrors() {
        for (CaveEdge edge : loopEdges) {
            for (CaveEdge edge2 : loopEdges) {
                if (edge.from.getName().equals(edge2.from.getName())){
                    double[] errors = getErrorBetweenTwoPoint(edge.from, edge2.from);
                    if (errors[3] > this.error){
                        this.xError = errors[0];
                        this.yError = errors[1];
                        this.zError = errors[2];
                        this.error = errors[3];
                    }
                }
                if (edge.to.getName().equals(edge2.from.getName())){
                    double[] errors = getErrorBetweenTwoPoint(edge.to, edge2.from);
                    if (errors[3] > this.error){
                        this.xError = errors[0];
                        this.yError = errors[1];
                        this.zError = errors[2];
                        this.error = errors[3];
                    }
                }
                if (edge.from.getName().equals(edge2.to.getName())){
                    double[] errors = getErrorBetweenTwoPoint(edge.from, edge2.to);
                    if (errors[3] > this.error){
                        this.xError = errors[0];
                        this.yError = errors[1];
                        this.zError = errors[2];
                        this.error = errors[3];
                    }
                }
                if (edge.to.getName().equals(edge2.to.getName())){
                    double[] errors = getErrorBetweenTwoPoint(edge.to, edge2.to);
                    if (errors[3] > this.error){
                        this.xError = errors[0];
                        this.yError = errors[1];
                        this.zError = errors[2];
                        this.error = errors[3];
                    }
                }
            }
        }       
    }
    
    private double[] getErrorBetweenTwoPoint(CavePoint start, CavePoint end){
        double[] e = new double[4];
        e[0] = start.getCoordinates(0)-end.getCoordinates(0);
        e[1] = start.getCoordinates(1)-end.getCoordinates(1);
        e[2] = start.getCoordinates(2)-end.getCoordinates(2);
        e[3] = Math.sqrt(e[0] * e[0] + e[1] * e[1] + e[2] * e[2]);
        return e;
    }
    
    public void calculateFixedLoop() {
        double cumulatedLength = 0;
        for (int i=0; i < loopEdges.size(); i++) {
            CaveEdge actualEdge = loopEdges.get(i);
            cumulatedLength += actualEdge.getLength();
            if (i==0) {
                double[] coordinates = actualEdge.from.getCoordinates();
                double[] coordinatesNew = new double[coordinates.length];
                coordinatesNew[0] = coordinates[0] + xError* (cumulatedLength / loopLength);
                coordinatesNew[1] = coordinates[1] + yError* (cumulatedLength / loopLength);
                coordinatesNew[2] = coordinates[2] + zError* (cumulatedLength / loopLength);
                //actualEdge.from.setCoordinates(coordinatesNew);
                coordinates = actualEdge.to.getCoordinates();
                coordinatesNew[0] = coordinates[0] + xError* (cumulatedLength / loopLength);
                coordinatesNew[1] = coordinates[1] + yError* (cumulatedLength / loopLength);
                coordinatesNew[2] = coordinates[2] + zError* (cumulatedLength / loopLength);
                actualEdge.to.setCoordinates(coordinatesNew);
            } else {
                actualEdge.from.setCoordinates(loopEdges.get(i-1).to.getCoordinates());
                double[] coordinates = actualEdge.to.getCoordinates();
                double[] coordinatesNew = new double[coordinates.length];
                coordinatesNew[0] = coordinates[0] + xError* (cumulatedLength / loopLength);
                coordinatesNew[1] = coordinates[1] + yError* (cumulatedLength / loopLength);
                coordinatesNew[2] = coordinates[2] + zError* (cumulatedLength / loopLength);
                actualEdge.to.setCoordinates(coordinatesNew);
            }
        }
    }
    
    public void calculateFixedLoopInEov() {
        double cumulatedLength = 0;
        for (int i=0; i < loopEdges.size(); i++) {
            CaveEdge actualEdge = loopEdges.get(i);
            cumulatedLength += actualEdge.getLength();
            if (i==0) {
                double[] coordinates = actualEdge.from.getEovCoordinates();
                double[] coordinatesNew = new double[coordinates.length];
                coordinatesNew[0] = coordinates[0] + xError* (cumulatedLength / loopLength);
                coordinatesNew[1] = coordinates[1] + yError* (cumulatedLength / loopLength);
                coordinatesNew[2] = coordinates[2] + zError* (cumulatedLength / loopLength);
                //actualEdge.from.setCoordinates(coordinatesNew);
                coordinates = actualEdge.to.getEovCoordinates();
                coordinatesNew[0] = coordinates[0] + xError* (cumulatedLength / loopLength);
                coordinatesNew[1] = coordinates[1] + yError* (cumulatedLength / loopLength);
                coordinatesNew[2] = coordinates[2] + zError* (cumulatedLength / loopLength);
                actualEdge.to.setEovCoordinates(coordinatesNew);
            } else {
                actualEdge.from.setEovCoordinates(loopEdges.get(i-1).to.getEovCoordinates());
                double[] coordinates = actualEdge.to.getEovCoordinates();
                double[] coordinatesNew = new double[coordinates.length];
                coordinatesNew[0] = coordinates[0] + xError* (cumulatedLength / loopLength);
                coordinatesNew[1] = coordinates[1] + yError* (cumulatedLength / loopLength);
                coordinatesNew[2] = coordinates[2] + zError* (cumulatedLength / loopLength);
                actualEdge.to.setEovCoordinates(coordinatesNew);
            }
        }
    }
}
