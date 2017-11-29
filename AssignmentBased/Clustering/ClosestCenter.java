package AssignmentBased.Clustering;

import Clustering.Point;

/**
 * Closest center from a point
 *
 */
public class ClosestCenter {

	private Point centerPoint, closestCenterFromPoint;
	private double distanceFromPoint;

	/**
	 * @param centerPoint is the center that is closest from a point
	 * @param closestCenterFromPoint is the point
	 */
	public ClosestCenter(Point centerPoint, Point closestCenterFromPoint) {
		this.centerPoint = centerPoint;
		this.closestCenterFromPoint = closestCenterFromPoint;
		this.distanceFromPoint = closestCenterFromPoint.getDistanceFrom(centerPoint);
	}
	
	public void setNewCenter(Point newCenterPoint) {
		this.centerPoint = newCenterPoint;
		this.distanceFromPoint = closestCenterFromPoint.getDistanceFrom(this.centerPoint);
	}
	
	public Point getCenterPoint() {
		return centerPoint;
	}

	public Point getClosestCenterFromPoint() {
		return closestCenterFromPoint;
	}

	public double getDistanceFromPoint() {
		return distanceFromPoint;
	}

	@Override
	public int hashCode() {
		return this.centerPoint.hashCode() + 31 * this.closestCenterFromPoint.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (other instanceof ClosestCenter) {
			if (((ClosestCenter) other).getCenterPoint().equals(this.centerPoint) && ((ClosestCenter) other).getClosestCenterFromPoint().equals(this.closestCenterFromPoint)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
	}
}
