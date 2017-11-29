package Clustering;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Hierarchical.Clustering.PointPair;

public class InterPointDistances {
	
	private static InterPointDistances instance = null;
	private List<Point> allPoints = null;
	private List<PointPair> pointPairs;
	
	private InterPointDistances(String fileName) {

		//Store list of all points
		this.allPoints = DataFileReader.getPointsFromTextFile(fileName);
		
		//Store inter point distances
		this.pointPairs = new ArrayList<PointPair>();
		for (int pointCounter = 0; pointCounter < this.allPoints.size(); ++pointCounter) {
			for (int otherPointCounter = pointCounter + 1; otherPointCounter < this.allPoints.size(); ++otherPointCounter) {
				this.pointPairs.add(new PointPair(this.allPoints.get(pointCounter), this.allPoints.get(otherPointCounter)));
			}
		}
	}
	
	public static InterPointDistances getInstance(String fileName) {
		
		if (instance == null) {
			instance = new InterPointDistances(fileName);
		}
		
		return instance;

	}
	
	public static void destroyInstance() {
		
		if (instance != null) {
			instance = null;
		}
		
	}
	
	
	public static InterPointDistances getInstance() {
		
		return instance;

	}

	public double getInterPointDistance(Point point1, Point point2) {
		return this.pointPairs.get(this.pointPairs.indexOf(new PointPair(point1, point2))).getDistanceBetween();
	}
	
	public List<Point> getAllPoints() {
		return Collections.unmodifiableList(this.allPoints);
	}
	
}
