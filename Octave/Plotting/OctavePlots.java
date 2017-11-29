package Octave.Plotting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import AssignmentBased.Clustering.ClosestCenter;
import Clustering.Point;
import Hierarchical.Clustering.HierarchicalCluster;

/**
 * Class with methods to aid in Octave plotting. Will only work with 2D points.
 *
 */
public class OctavePlots {
	
	public static final char POINT_REPRESENTATION = 'x';
	public static final char CENTER_REPRESENTATION = 'o';

	public static String RED_COLOR = "red";
	public static String GREEN_COLOR = "green";
	public static String BLUE_COLOR = "blue";
	public static String MAGENTA_COLOR = "magenta";
	public static String[] PLOT_COLORS = {RED_COLOR, GREEN_COLOR, BLUE_COLOR, MAGENTA_COLOR};
	
	public static String X_LABEL = "xlabel(\"X Coordinate\");\n";
	public static String Y_LABEL = "ylabel(\"Y Coordinate\");\n";
	
	/**
	 * @param points
	 * @param pointColorIndex
	 * @param pointRepresentation
	 * @param plotTitle
	 * @return X and Y coordinates in Octave format 
	 */
	public static String getXAndYCoordinatesScatterPlot(List<Point> points, int pointColorIndex, char pointRepresentation, String plotTitle, boolean showCoordinateLegends) {
		
		if (pointColorIndex >= PLOT_COLORS.length) {
			throw new ArrayIndexOutOfBoundsException("Point color index " + pointColorIndex + " has to be less than or equal to " + (PLOT_COLORS.length - 1));
		}
		
		if (points.get(0).getCoordinates().size() > 2) {
			throw new RuntimeException(points.get(0).getCoordinates().size() + " dimension plotting is not possible.");
		}
		
		StringBuffer xCoordinates = new StringBuffer();
		StringBuffer yCoordinates = new StringBuffer();
		
		boolean firstTime = true;
		for (Point point : points) {
		
			if (firstTime) {
				firstTime = false;
				xCoordinates.append("[");
				yCoordinates.append("[");
			} else {
				xCoordinates.append(", ");
				yCoordinates.append(", ");
			}
			
			xCoordinates.append(point.getCoordinates().get(0));
			yCoordinates.append(point.getCoordinates().get(1));
		
		}

		xCoordinates.append("]");
		yCoordinates.append("]");
		
		StringBuffer octaveScript = new StringBuffer();
		octaveScript.append("scatter(")
		.append(xCoordinates.toString())
		.append(",").append(yCoordinates.toString())
		.append(", '").append(PLOT_COLORS[pointColorIndex])
		.append("', '")
		.append(pointRepresentation)
		.append("'); hold on");
		
		if (plotTitle.trim().length() > 0) {
			octaveScript.append("\ntitle(\"")
			.append(plotTitle)
			.append("\");");
		}
		
		if (showCoordinateLegends) {
			octaveScript.append("\n")
			.append(X_LABEL)
			.append(Y_LABEL);
		}
		
		octaveScript.append("\n");
		
		return octaveScript.toString();
		
	}
	
	/**
	 * @param clusters
	 * @param pointRepresentation
	 * @param plotTitle
	 * @return octave script for plotting hierchical clusters with no defined center
	 */
	public static String getHierarchicalClusterScatterPlot(List<HierarchicalCluster> clusters, char pointRepresentation, String plotTitle) {
	
		StringBuffer octaveScript = new StringBuffer();
		
		int clusterIndex = 0;
		for (HierarchicalCluster cluster : clusters) {
			octaveScript.append(getXAndYCoordinatesScatterPlot(cluster.getPoints(), clusterIndex++, POINT_REPRESENTATION, "", false));
		}
		
		if (plotTitle.trim().length() > 0) {
			octaveScript.append("\ntitle(\"")
			.append(plotTitle)
			.append("\");");
		}
		
		octaveScript.append("\n")
		.append(X_LABEL)
		.append(Y_LABEL);
		
		return octaveScript.toString();
	
	}
	
	/**
	 * @param centers
	 * @param distanceOfPointsToClosestCenters
	 * @return string for octave plot of centers along with points attached to them
	 */
	public static String getAssignmentBasedClusterScatterPlot(List<Point> centers, PriorityQueue<ClosestCenter> distanceOfPointsToClosestCenters, String plotTitle) {
		
		StringBuffer octaveScript = new StringBuffer();
		
		//Get all the centers
		int centerCounter = 0;
		for (Point center : centers) {
		
			//Plot the center
			octaveScript.append(getXAndYCoordinatesScatterPlot(Arrays.asList(center), centerCounter, CENTER_REPRESENTATION, "", false));

			//Plot the points attached to the center
			octaveScript.append(getXAndYCoordinatesScatterPlot(getPointsAttachedToCenter(center, distanceOfPointsToClosestCenters), centerCounter++, POINT_REPRESENTATION, "", false));
		}
		
		if (plotTitle.trim().length() > 0) {
			octaveScript.append("\ntitle(\"")
			.append(plotTitle)
			.append("\");");
		}
		
		octaveScript.append("\n")
		.append(X_LABEL)
		.append(Y_LABEL);
		
		return octaveScript.toString();
		
	}
	
	private static List<Point> getPointsAttachedToCenter(Point center, PriorityQueue<ClosestCenter> distanceOfPointsToClosestCenters) {
		
		List<Point> pointsAttachedToCenter = new ArrayList<Point>();
		
		Iterator<ClosestCenter> closestCenterIterator = distanceOfPointsToClosestCenters.iterator();
		ClosestCenter closestCenter = null;
		while (closestCenterIterator.hasNext()) {
			closestCenter = closestCenterIterator.next();
			if (closestCenter.getCenterPoint().equals(center)) {
				pointsAttachedToCenter.add(closestCenter.getClosestCenterFromPoint());
			}
		}
		
		return pointsAttachedToCenter;
		
	}
	
	/**
	 * @param _3MeansCostKMeansPlusPlus
	 * @param plotTitle
	 * @return octave script for 3-Means cost trend and cumulative cost trend
	 */
	public static String get3MeansCostTrend(List<Double> threeMeansCostTrend, String plotTitle) {
		
		StringBuffer octaveScript = new StringBuffer();
		
		StringBuffer xCoordinates = new StringBuffer();
		StringBuffer yCoordinates = new StringBuffer();
		StringBuffer cumulativeYCoordinates = new StringBuffer();
		
		double cumulativeCost = 0.0;
		
		boolean firstTime = true;
		for (int threeMeansCostIndex = 0; threeMeansCostIndex < threeMeansCostTrend.size(); ++threeMeansCostIndex) {
			
			if (firstTime) {
				firstTime = false;
				xCoordinates.append("[");
				yCoordinates.append("[");
				cumulativeYCoordinates.append("[");
			} else {
				xCoordinates.append(", ");
				yCoordinates.append(", ");
				cumulativeYCoordinates.append(", ");
			}
			
			xCoordinates.append(threeMeansCostIndex + 1);
			yCoordinates.append(threeMeansCostTrend.get(threeMeansCostIndex).doubleValue());
			cumulativeCost += threeMeansCostTrend.get(threeMeansCostIndex).doubleValue();
			cumulativeYCoordinates.append(cumulativeCost);
		
		}

		xCoordinates.append("]");
		yCoordinates.append("]");
		cumulativeYCoordinates.append("]");

		octaveScript.append("plot(")
		.append(xCoordinates.toString())
		.append(",")
		.append(yCoordinates.toString())
		.append(",'marker', 'x',")
		.append(xCoordinates.toString())
		.append(",")
		.append(cumulativeYCoordinates.toString())
		.append(",'marker', 'x'); hold on");
		
		if (plotTitle.trim().length() > 0) {
			octaveScript.append("\ntitle(\"")
			.append(plotTitle)
			.append("\");");
		}
		
		octaveScript.append("\nxlabel(\"Iteration Number\");")
					.append("\nylabel(\"3-Means Cost\");")
					.append("\nlegend('3-Means Cost', 'Cumulative 3-Means Cost');");
		octaveScript.append("\n");

		return octaveScript.toString();
		
	}
	
	/**
	 * @param gonzalezClusterContents
	 * @param clusterContentsPerIteration
	 * @param plotTitle
	 * @return octave script for cluster contents intersection size as percentage for each cluster
	 */
	public static String getClusterIntersection(Map<Point, Set<Point>> gonzalezClusterContents, List<Map<Point, Set<Point>>> clusterContentsPerIteration, String plotTitle){
		
		StringBuffer octaveScript = new StringBuffer();
		
		List<Double> cluster0SizeIntersections = new ArrayList<Double>();
		List<Double> cluster1SizeIntersections = new ArrayList<Double>();
		List<Double> cluster2SizeIntersections = new ArrayList<Double>();
		
		List<Point> referenceClusterCenters = new ArrayList<Point>(gonzalezClusterContents.keySet());
		List<Point> referenceCluster0Contents = new ArrayList<Point>(gonzalezClusterContents.get(referenceClusterCenters.get(0)));
		List<Point> referenceCluster1Contents = new ArrayList<Point>(gonzalezClusterContents.get(referenceClusterCenters.get(1)));
		List<Point> referenceCluster2Contents = new ArrayList<Point>(gonzalezClusterContents.get(referenceClusterCenters.get(2)));
		
		List<Point> trendCluster0Contents = null;
		List<Point> trendCluster1Contents = null;
		List<Point> trendCluster2Contents = null;

		
		for (Map<Point, Set<Point>> clusters : clusterContentsPerIteration) {

			
			List<Point> centersInOrderOfReferenceCenters = getCentersInOrderOfReferenceCenters(gonzalezClusterContents.keySet(), clusters.keySet());
			
			trendCluster0Contents = new ArrayList<Point>(clusters.get(centersInOrderOfReferenceCenters.get(0)));
			trendCluster1Contents = new ArrayList<Point>(clusters.get(centersInOrderOfReferenceCenters.get(1)));
			trendCluster2Contents = new ArrayList<Point>(clusters.get(centersInOrderOfReferenceCenters.get(2)));
			
			trendCluster0Contents.retainAll(referenceCluster0Contents);
			trendCluster1Contents.retainAll(referenceCluster1Contents);
			trendCluster2Contents.retainAll(referenceCluster2Contents);
			
			cluster0SizeIntersections.add(100.0 * trendCluster0Contents.size()/referenceCluster0Contents.size());
			cluster1SizeIntersections.add(100.0 * trendCluster1Contents.size()/referenceCluster1Contents.size());
			cluster2SizeIntersections.add(100.0 * trendCluster2Contents.size()/referenceCluster2Contents.size());
			
			
		}

		StringBuffer xCoordinates = new StringBuffer();
		StringBuffer cluster0Coordinates = new StringBuffer();
		StringBuffer cluster1Coordinates = new StringBuffer();
		StringBuffer cluster2Coordinates = new StringBuffer();

		//Create Octave script
		boolean firstTime = true;
		for (int iterationCounter = 0; iterationCounter < clusterContentsPerIteration.size(); ++iterationCounter) {
			
			if (firstTime) {
				firstTime = false;
				xCoordinates.append("[");
				cluster0Coordinates.append("[");
				cluster1Coordinates.append("[");
				cluster2Coordinates.append("[");
			} else {
				xCoordinates.append(", ");
				cluster0Coordinates.append(", ");
				cluster1Coordinates.append(", ");
				cluster2Coordinates.append(", ");
			}
			
			xCoordinates.append(iterationCounter);
			cluster0Coordinates.append(cluster0SizeIntersections.get(iterationCounter).toString());
			cluster1Coordinates.append(cluster1SizeIntersections.get(iterationCounter).toString());
			cluster2Coordinates.append(cluster2SizeIntersections.get(iterationCounter).toString());
		
		}

		xCoordinates.append("]");
		cluster0Coordinates.append("]");
		cluster1Coordinates.append("]");
		cluster2Coordinates.append("]");
		
		octaveScript.append("plot(")
					.append(xCoordinates)
					.append(", ")
					.append(cluster0Coordinates)
					.append(", ")
					.append(xCoordinates)
					.append(", ")
					.append(cluster1Coordinates)
					.append(", ")
					.append(xCoordinates)
					.append(", ")
					.append(cluster2Coordinates)
					.append("); hold on");
		
		octaveScript.append("\nxlabel('Iteration Number');")
					.append("\nylabel('Cluster Overlap %');")
					.append("\ntitle('")
					.append(plotTitle)
					.append("');")
					.append("\nlegend('Cluster 1', 'Cluster 2', 'Cluster 3');");
		
		return octaveScript.toString();
				
	}
	
	private static List<Point> getCentersInOrderOfReferenceCenters(Set<Point> referenceCenters, Set<Point> trendCenters) {
		
		//Create empty list of centers in order of reference centers
		List<Point> centersInOrderOfReferenceCenters = new ArrayList<Point>(trendCenters.size());
		for (@SuppressWarnings("unused") Point point : referenceCenters) {
			centersInOrderOfReferenceCenters.add(null);
		}
		
		double secondCoordinateValue = 0.0;
		for (Point trendPoint : trendCenters) {
			secondCoordinateValue = trendPoint.getCoordinates().get(1).doubleValue();
			if (secondCoordinateValue >= 80 && secondCoordinateValue <= 94) {
				centersInOrderOfReferenceCenters.set(0, trendPoint);
			} else if (secondCoordinateValue >= 16 && secondCoordinateValue <= 22) {
				centersInOrderOfReferenceCenters.set(1, trendPoint);
			} else {
				centersInOrderOfReferenceCenters.set(2, trendPoint);
			}
		}
		
		return centersInOrderOfReferenceCenters;
		
	}
}
