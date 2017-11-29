package Hierarchical.Clustering;

import Clustering.InterPointDistances;
import Clustering.Point;

/**
 * Hierarchical cluster pair with distance between them determined by the shortest distance between its points
 *
 */
public final class SingleLinkHierarchicalClusterPair extends HierarchicalClusterPair {

	public SingleLinkHierarchicalClusterPair(HierarchicalCluster cluster1, HierarchicalCluster cluster2) {
		super(cluster1, cluster2);
	}

	@Override
	protected void setDistanceBetween() {

		double candidateDistanceBetween = 0.0;
		this.distanceBetween = Double.MAX_VALUE;
		InterPointDistances interPointDistances = InterPointDistances.getInstance();

		for (Point point1 : this.cluster1.getPoints()) {
			for (Point point2 : this.cluster2.getPoints()) {
				candidateDistanceBetween = interPointDistances.getInterPointDistance(point1, point2);
				if (candidateDistanceBetween < this.distanceBetween) {
					this.distanceBetween = candidateDistanceBetween;
				}
			}
		}
		
	}

}
