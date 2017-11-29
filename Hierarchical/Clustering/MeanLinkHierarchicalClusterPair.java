package Hierarchical.Clustering;
import java.util.ArrayList;
import java.util.List;

import Clustering.Point;

/**
 * Hierarchical cluster pair with distance between them determined by the distance between the mean of its points
 *
 */
public class MeanLinkHierarchicalClusterPair extends HierarchicalClusterPair {

	public MeanLinkHierarchicalClusterPair(HierarchicalCluster cluster1, HierarchicalCluster cluster2) {
		super(cluster1, cluster2);
	}

	@Override
	protected void setDistanceBetween() {
		
		//Distance between mean points is the distance between the clusters
		this.distanceBetween = getClusterMeanPoint(cluster1).getDistanceFrom(getClusterMeanPoint(cluster2));

	}
	
	/**
	 * @param cluster
	 * @return a point corresponding to the mean of the cluster
	 */
	private Point getClusterMeanPoint(HierarchicalCluster cluster) {
		
		List<Double> clusterMeanCoordinates = new ArrayList<Double>(), pointCoordinates = null;
		int coordinateCounter = 0;
		
		//Loop through all points in the cluster
		for (Point point : cluster.getPoints()) {
			coordinateCounter = 0;
			pointCoordinates = point.getCoordinates();
			//Loop through all coordinates in each point within the cluster
			for (Double coordinate : pointCoordinates) {
				if (clusterMeanCoordinates.size() >= coordinateCounter + 1) {
					clusterMeanCoordinates.set(coordinateCounter, Double.valueOf(clusterMeanCoordinates.get(coordinateCounter).doubleValue() + coordinate.doubleValue()));
				} else {
					clusterMeanCoordinates.add( coordinate);
				}
				++coordinateCounter;
			}
		}
		
		//Divide all point coordinates by number of coordinates to get the cluster mean
		int numberOfCoordinates = cluster.getPoints().get(0).getNumberOfDimensions();
		for (coordinateCounter = 0; coordinateCounter < numberOfCoordinates; ++coordinateCounter) {
			clusterMeanCoordinates.set(coordinateCounter, Double.valueOf(clusterMeanCoordinates.get(coordinateCounter).doubleValue() / numberOfCoordinates));
		}

		return new Point(clusterMeanCoordinates);
		
	}

}
