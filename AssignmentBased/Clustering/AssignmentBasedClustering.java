package AssignmentBased.Clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import Clustering.DataFileReader;
import Clustering.Point;

public abstract class AssignmentBasedClustering {
	
	protected List<Point> allPoints;
	protected List<Point> centers;
	protected PriorityQueue<ClosestCenter> distanceOfPointsToClosestCenters;
	protected int desiredNumberOfClusters;
	protected double _3CenterCost, _3MeansCost;
	protected Point _3CenterCostCenter, _3CenterCostPoint;

	/**
	 * Constructor
	 * @param fileName
	 * @param desiredNumberOfClusters
	 */
	public AssignmentBasedClustering(String fileName, int desiredNumberOfClusters) {
		this.allPoints = DataFileReader.getPointsFromTextFile(fileName);
		this.centers = new ArrayList<Point>(desiredNumberOfClusters);
		this.distanceOfPointsToClosestCenters = new PriorityQueue<ClosestCenter>(new ClosestCenterComparator());
		this.desiredNumberOfClusters = desiredNumberOfClusters;
	}

	/**
	 * Run the Gonzalez algorithm for k-center clustering
	 */
	public void runClustering() {
		
		//Choose the first center arbitrarily
		this.centers.add(this.allPoints.get(0));
		this.allPoints.remove(0);
		
		//Store distance of all points to the initial center
		setDistanceOfPointsToInitialCenter();
		
		ClosestCenter pointFarthestFromAllCenters = null;
		for (int clusterCounter = 1; clusterCounter < this.desiredNumberOfClusters; ++clusterCounter) {
			pointFarthestFromAllCenters = getNextCluster();
			this.centers.add(pointFarthestFromAllCenters.getClosestCenterFromPoint());
			this.allPoints.remove(pointFarthestFromAllCenters.getClosestCenterFromPoint());
			updateDistanceOfPointsToClosestCenter(pointFarthestFromAllCenters.getClosestCenterFromPoint());
		}
		
		updateCostMetrics();

	}
	
	protected abstract ClosestCenter getNextCluster();
	
	/**
	 * Store the distance of all points to the initial point selected as a center
	 */
	protected void setDistanceOfPointsToInitialCenter() {
		
		for (Point point : this.allPoints) {
			this.distanceOfPointsToClosestCenters.add(new ClosestCenter(this.centers.get(0), point));
		}
		
	}
	
	/**
	 * Update the distance of points to closest center. If the next center is closer, update the distance measure.
	 * @param nextCenter
	 */
	protected void updateDistanceOfPointsToClosestCenter(Point nextCenter) {
		
		//Keep a list of closest centers to be updated because a new center has been added
		List<ClosestCenter> closestCentersToBeUpdated = new ArrayList<ClosestCenter>();
		
		ClosestCenter closestCenter = null;
		Iterator<ClosestCenter> closestCenterIterator = this.distanceOfPointsToClosestCenters.iterator();
		while (closestCenterIterator.hasNext()) {
			
			closestCenter = closestCenterIterator.next();
			closestCentersToBeUpdated.add(closestCenter);

		}
		
		//Replace the entry in the cases the new center is closer to points. 
		for (ClosestCenter closestCenterEntry : closestCentersToBeUpdated) {
			if (closestCenterEntry.getDistanceFromPoint() > closestCenterEntry.getClosestCenterFromPoint().getDistanceFrom(nextCenter)) {
				this.distanceOfPointsToClosestCenters.remove(closestCenterEntry);
				closestCenterEntry.setNewCenter(nextCenter);
				this.distanceOfPointsToClosestCenters.add(closestCenterEntry);
			}
		}

	}

	/**
	 * Update the 3-center cost and 3-means cost
	 */
	protected void updateCostMetrics() {
		
		//Find maximum distance to the closest center
		ClosestCenter maximumDistanceToClosestCenter = this.distanceOfPointsToClosestCenters.peek();
		this._3CenterCost = maximumDistanceToClosestCenter.getDistanceFromPoint();
		this._3CenterCostCenter = maximumDistanceToClosestCenter.getCenterPoint();
		this._3CenterCostPoint = maximumDistanceToClosestCenter.getClosestCenterFromPoint();
		
		//Find 3-means cost 
		double threeMeansCost = 0.0;
		Iterator<ClosestCenter> closestCenterIterator = this.distanceOfPointsToClosestCenters.iterator();
		while (closestCenterIterator.hasNext()) {
			threeMeansCost += Math.pow(closestCenterIterator.next().getDistanceFromPoint(), 2.0);
		}
		threeMeansCost /= this.distanceOfPointsToClosestCenters.size();
		this._3MeansCost = Math.sqrt(threeMeansCost);
		
	}
	
	
	/**
	 * @return a map of sets where each set has the points in a cluster
	 */
	public Map<Point, Set<Point>> getClusterContents() {
		
		Map<Point, Set<Point>> clusterContents = new HashMap<Point, Set<Point>>();
		
		ClosestCenter closestCenter = null;
		Set<Point> pointsInCluster = null;
		Iterator<ClosestCenter> closestCenterIterator = this.distanceOfPointsToClosestCenters.iterator();
		while (closestCenterIterator.hasNext()) {
			
			//Add cluster points to the associated set and put it in the map
			closestCenter = closestCenterIterator.next();
			if (clusterContents.containsKey(closestCenter.getCenterPoint())) {
				pointsInCluster = clusterContents.get(closestCenter.getCenterPoint());
			} else {
				pointsInCluster = new HashSet<Point>();
			}
			pointsInCluster.add(closestCenter.getClosestCenterFromPoint());
			clusterContents.put(closestCenter.getCenterPoint(), pointsInCluster);
			
		}
		
		return clusterContents;
		
	}
	
	public List<Point> getCenters() {
		return this.centers;
	}

	public PriorityQueue<ClosestCenter> getDistanceOfPointsToClosestCenters() {
		return this.distanceOfPointsToClosestCenters;
	}

	public double get_3CenterCost() {
		return _3CenterCost;
	}

	public double get_3MeansCost() {
		return _3MeansCost;
	}

	public Point get_3CenterCostCenter() {
		return _3CenterCostCenter;
	}

	public Point get_3CenterCostPoint() {
		return _3CenterCostPoint;
	}

}
