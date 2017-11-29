package AssignmentBased.Clustering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Clustering.Point;

public class KMeansLLoydsClustering extends AssignmentBasedClustering {

	public static final int NUMBER_OF_ITERATIONS = 10;
	private List<Point> seedClusters;
	private List<Double> _3MeansCostPerIteration;
	private List<Map<Point, Set<Point>>> clusterContentsPerIteration;
	private double _3MedianCost, _3MedianEuclidianCost;
	
	public KMeansLLoydsClustering(String fileName, int desiredNumberOfClusters, List<Point> seedClusters) {
		super(fileName, desiredNumberOfClusters);
		this.seedClusters = seedClusters;
		this._3MeansCostPerIteration = new ArrayList<Double>(NUMBER_OF_ITERATIONS);
		
		//List to store cluster contents for each iteration
		this.clusterContentsPerIteration = new ArrayList<Map<Point, Set<Point>>>(); 
	}
	
	@Override
	public void runClustering() {
		
		setSeedClusters();

		for (int iterationCounter = 0; iterationCounter < NUMBER_OF_ITERATIONS; ++iterationCounter) {
			
			//Store distance of all points to the centers
			setDistanceOfPointsToCenters();

			//Update centers as average of all associated points
			updateCentersAsAverageOfAssociatedPoints();
	
			updateCostMetrics();
			this._3MeansCostPerIteration.add(Double.valueOf(this._3MeansCost));
			
			clusterContentsPerIteration.add(getClusterContents());

		}

	}
	
	private void setSeedClusters() {
		
		if (this.seedClusters == null || this.seedClusters.size() == 0 || this.seedClusters.size() != this.desiredNumberOfClusters) {
			//Choose the first k centers
			for (int centerCounter = 0; centerCounter < desiredNumberOfClusters; ++centerCounter) {
				this.centers.add(new Point(this.allPoints.get(centerCounter).getCoordinates()));
			}
		} else {
			//Choose the seed centers
			for (int centerCounter = 0; centerCounter < desiredNumberOfClusters; ++centerCounter) {
				this.centers.add(this.seedClusters.get(centerCounter));
			}
		}
	}
	
	@Override
	protected ClosestCenter getNextCluster() {
		return null;
	}
	
	/**
	 * Update the centers as the average of all associated points
	 */
	private void updateCentersAsAverageOfAssociatedPoints() {
		
		//For each center find the average of its associated points and make that the new center
		Map<Point, Set<Point>> clusterContents = getClusterContents();
		Set<Point> currentCenters = clusterContents.keySet();
		
		int pointCoordinatesCounter = 0, pointsCounter = 0;
		List<Double> averageCoordinates = null, currentPointCoordinates = null;
		boolean firstTime = true;

		for (Point center : currentCenters) {
			
			firstTime = true;
			
			//Loop through each point in the cluster and find the average
			Set<Point> clusterPoints = clusterContents.get(center);
			pointsCounter = 0;
			for (Point point : clusterPoints) {
				
				++pointsCounter;
				if (firstTime) {
					averageCoordinates = new ArrayList<Double>(point.getCoordinates());
					firstTime = false;
				} else {
					currentPointCoordinates = point.getCoordinates();
					pointCoordinatesCounter = 0;
					for (Double coordinate : currentPointCoordinates) {
						averageCoordinates.set(pointCoordinatesCounter, Double.valueOf(averageCoordinates.get(pointCoordinatesCounter).doubleValue() + coordinate.doubleValue()));
						++pointCoordinatesCounter;
					}
				}
				
				
			}
			
			//Divide the average coordinates by the number to get the true average
			for (int coordinateCounter = 0; coordinateCounter < averageCoordinates.size(); ++coordinateCounter) {
				averageCoordinates.set(coordinateCounter, Double.valueOf(averageCoordinates.get(coordinateCounter).doubleValue() / pointsCounter));
			}
			
			//Save the new average center
			this.centers.remove(center);
			this.centers.add(new Point(averageCoordinates));
			
		}
		
		
	}

	/**
	 * Store the distance of all points to the centers
	 */
	private void setDistanceOfPointsToCenters() {
		
		this.distanceOfPointsToClosestCenters.clear();
		
		int indexForClosestCenter = 0;
		double distanceToClosestCenter = 0.0, distanceToCurrentCenter = 0.0;
		
		for (Point point : this.allPoints) {
			distanceToClosestCenter = Double.MAX_VALUE;
			for (int centerCounter = 0; centerCounter < desiredNumberOfClusters; ++centerCounter) {
				distanceToCurrentCenter = point.getDistanceFrom(this.centers.get(centerCounter));
				if (distanceToCurrentCenter < distanceToClosestCenter) {
					indexForClosestCenter = centerCounter;
					distanceToClosestCenter = distanceToCurrentCenter;
				}
			}
			this.distanceOfPointsToClosestCenters.add(new ClosestCenter(this.centers.get(indexForClosestCenter), point));
		}
		
	}


	/**
	 * Update the 3-center, 3-means cost 
	 */
	@Override
	protected void updateCostMetrics() {
		
		super.updateCostMetrics();
		
		//Find 3-median cost 
		this._3MedianCost = 0.0;
		this._3MedianEuclidianCost = 0.0;
		
		ClosestCenter closestCenter = null;
		Iterator<ClosestCenter> closestCenterIterator = this.distanceOfPointsToClosestCenters.iterator();
		while (closestCenterIterator.hasNext()) {
			closestCenter = closestCenterIterator.next();
			this._3MedianCost += getManhattanDistanceBetween(closestCenter.getCenterPoint(), closestCenter.getClosestCenterFromPoint());
			this._3MedianEuclidianCost += closestCenter.getDistanceFromPoint();
		}
		
		this._3MedianCost /= this.distanceOfPointsToClosestCenters.size();
		this._3MedianEuclidianCost /= this.distanceOfPointsToClosestCenters.size();
		
	}
	
	private double getManhattanDistanceBetween(Point point1, Point point2) {
		
		double manhattanDistanceBetweenPoints = 0.0;
		int coordinateCounter = 0;
		for (Double coordinate : point1.getCoordinates()) {
			manhattanDistanceBetweenPoints += Math.abs(coordinate.doubleValue() - point2.getCoordinates().get(coordinateCounter++));
		}
		
		return manhattanDistanceBetweenPoints; 
		
	}
	
	public List<Double> get_3MeansCostPerIteration() {
		return _3MeansCostPerIteration;
	}

	public List<Map<Point, Set<Point>>> getClusterContentsPerIteration() {
		return clusterContentsPerIteration;
	}

	public double get_3MedianCost() {
		return _3MedianCost;
	}

	public double get_3MedianEuclidianCost() {
		return _3MedianEuclidianCost;
	}
}
