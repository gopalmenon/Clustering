package Clustering;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Point {
	
	private List<Double> coordinates;
	private DecimalFormat decimalFormat;
	
	/**
	 * Constructor
	 * @param coordinates
	 */
	public Point(List<Double> coordinates) {
		this.coordinates = new ArrayList<Double>(coordinates);
		this.decimalFormat = new DecimalFormat("0.000000000");
	}
	
	/**
	 * @return number of dimensions in the point object
	 */
	public int getNumberOfDimensions() {
		return this.coordinates.size();
	}
	
	/**
	 * @param other point
	 * @return distance from the other point
	 */
	public double getDistanceFrom(Point other) {
		
		assert this.coordinates.size() == other.getNumberOfDimensions();
		
		double distanceFromOther = 0.0;
		int dimensionCounter = 0;
		for (Double coordinate : this.coordinates) {
			distanceFromOther += Math.pow(coordinate - other.coordinates.get(dimensionCounter), 2.0);
			++dimensionCounter;
		}
		
		return Math.sqrt(distanceFromOther);
		
	}
	
	public List<Double> getCoordinates() {
		return Collections.unmodifiableList(this.coordinates);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		
		if (other instanceof Point) {
			if (this.coordinates.size() == ((Point) other).coordinates.size()) {
				int dimensionCounter = 0;
				//All coordinates must match
				for (Double coordinate : this.coordinates) {
					if (coordinate.doubleValue() != ((Point) other).coordinates.get(dimensionCounter).doubleValue()) {
						return false;
					} else {
						++dimensionCounter;
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
		
		int dimensionCounter = 0, hashCode = 0;
		for (Double coordinate : this.coordinates) {
			hashCode += Math.pow(2.0, dimensionCounter) * coordinate;
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
		for (Double coordinate : this.coordinates) {
			if (firstTime) {
				firstTime = false;
				System.out.print("(");
			} else {
				System.out.print(", ");
			}
			System.out.print(this.decimalFormat.format(coordinate));
		}
		System.out.print(")");
		return clusterString.toString();
	}
}
