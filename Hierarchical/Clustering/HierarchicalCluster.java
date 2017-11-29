package Hierarchical.Clustering;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Clustering.Point;

public class HierarchicalCluster {

	private List<Point> points;
	
	/**
	 * Constructor
	 * @param points
	 */
	public HierarchicalCluster(List<Point> points) {
		this.points = new ArrayList<Point>(points);
	}
	
	/**
	 * @param other cluster
	 * @return merge with other cluster and return merged cluster
	 */
	public HierarchicalCluster mergeWithCluster(HierarchicalCluster other) {
		
		List<Point> pointsInMergedCluster = new ArrayList<Point>(this.points.size() + other.points.size());
		pointsInMergedCluster.addAll(this.points);
		pointsInMergedCluster.addAll(other.points);
		return new HierarchicalCluster(pointsInMergedCluster);
		
	}
	
	public List<Point> getPoints() {
		return Collections.unmodifiableList(this.points);
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (other instanceof HierarchicalCluster) {
			if (this.points.size() == ((HierarchicalCluster) other).points.size()) {
				for (Point point : this.points) {
					if (!((HierarchicalCluster) other).points.contains(point)) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
	}
	
	@Override
	public int hashCode() {
		
		int hashCode = 0;
		for (Point point : this.points) {
			hashCode += point.hashCode();
		}
		return hashCode;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuffer clusterString = new StringBuffer();
		boolean firstTime = true;
		for (Point point : this.points) {
			if (firstTime) {
				firstTime = false;
				System.out.print("[");
			} else {
				System.out.print(", ");
			}
			System.out.print(point);
		}
		System.out.print("]");
		return clusterString.toString();
	}
}
