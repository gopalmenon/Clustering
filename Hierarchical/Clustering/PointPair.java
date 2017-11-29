package Hierarchical.Clustering;

import Clustering.Point;

public class PointPair {

	private Point point1, point2;
	double distanceBetween;
	
	public PointPair(Point point1, Point point2) {		
		this.point1 = point1;
		this.point2 = point2;
		this.distanceBetween = this.point1.getDistanceFrom(point2);
	}
	
	public double getDistanceBetween() {
		return this.distanceBetween;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof PointPair) {
			if ((this.point1.equals(((PointPair) other).point1) && this.point2.equals(((PointPair) other).point2)) ||
				(this.point1.equals(((PointPair) other).point2) && this.point2.equals(((PointPair) other).point1))) {
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
		return this.point1.hashCode() + this.point2.hashCode();
	}

}
