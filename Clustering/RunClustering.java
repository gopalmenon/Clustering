package Clustering;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import AssignmentBased.Clustering.GonzalezKCenterClustering;
import AssignmentBased.Clustering.KMeansLLoydsClustering;
import AssignmentBased.Clustering.KMeansPlusPlusClustering;
import Hierarchical.Clustering.HierarchicalCluster;
import Hierarchical.Clustering.HierarchicalClustering;
import Octave.Plotting.OctavePlots;

public class RunClustering {

	public static final int DESIRED_NUMBER_OF_HIERARCHICAL_CLUSTERS = 4;
	public static final int DESIRED_NUMBER_OF_ASSIGNMENT_BASED_CLUSTERS = 3;
	public static final int DESIRED_NUMBER_OF_K_MEDIAN_CLUSTERS_1 = 4;
	public static final int DESIRED_NUMBER_OF_K_MEDIAN_CLUSTERS_2 = 5;
	public static final int K_MEANS_PLUS_PLUS_ITERATIONS = 25;
	
	public static final String TOY_DATA_FILE = "data/ToyData.txt";
	public static final String C1_2D_DATA_FILE = "data/C1.txt";
	public static final String C2_2D_DATA_FILE = "data/C2.txt";
	public static final String C3_5D_DATA_FILE = "data/C3.txt";
	public static final String K_MEDIAN_OUTFILE = "outfile/4A Centers - Gopal Menon.txt";
	
	public static final String[] ALL_DATA_FILES = {TOY_DATA_FILE, C1_2D_DATA_FILE, C2_2D_DATA_FILE, C3_5D_DATA_FILE};
	public static final String[] ALL_DISTANCE_MEASURES = {HierarchicalClustering.SINGLE_LINK, HierarchicalClustering.COMPLETE_LINK, HierarchicalClustering.MEAN_LINK};	
	public static final String[] ALL_DISTANCE_MEASURES_DESCRIPTIONS = {"Single Link", "Complete Link", "Mean Link"};
	
	private DecimalFormat decimalFormat;
	
	public RunClustering() {
		this.decimalFormat = new DecimalFormat("0.0000");
	}
	
	public static void main(String[] args) {
		RunClustering runClustering = new RunClustering();
		runClustering.runHierarchicalClustering();
		runClustering.runAssignmentBasedClustering();
	}

	/**
	 * Run clustering once for each type of distance measure on all the data files
	 */
	private void runHierarchicalClustering() {
		
		int distanceMeasureIndex = 0;
		for (String dataFile : Arrays.asList(C1_2D_DATA_FILE)) {
			distanceMeasureIndex = 0;
			for (String distanceMeasure : ALL_DISTANCE_MEASURES) {
				List<HierarchicalCluster> clusters = new HierarchicalClustering(DESIRED_NUMBER_OF_HIERARCHICAL_CLUSTERS, dataFile, distanceMeasure).getClusters();
				System.out.println("Hierarchical Clusters for file " + dataFile + " and distance measure " + distanceMeasure + ":\n");
				System.out.println(OctavePlots.getHierarchicalClusterScatterPlot(clusters, OctavePlots.POINT_REPRESENTATION, "Hierarchical Clusters for file " + dataFile + " and distance measure " + ALL_DISTANCE_MEASURES_DESCRIPTIONS[distanceMeasureIndex++]));
			}
			InterPointDistances.destroyInstance();
		}

	}

	
	private void runAssignmentBasedClustering() {
		
		String fileNameForClustering = C2_2D_DATA_FILE;
		
		GonzalezKCenterClustering gonzalezKCenterClustering = new GonzalezKCenterClustering(fileNameForClustering, DESIRED_NUMBER_OF_ASSIGNMENT_BASED_CLUSTERS);
		gonzalezKCenterClustering.runClustering();
		System.out.println("Assignment Based Gonzalez Clustering for file " + fileNameForClustering + ":\n");
		System.out.println("3-Center cost: " + this.decimalFormat.format(gonzalezKCenterClustering.get_3CenterCost()) + " for center " + gonzalezKCenterClustering.get_3CenterCostCenter() + " and point " + gonzalezKCenterClustering.get_3CenterCostPoint());
		System.out.println("3-Means cost: " + this.decimalFormat.format(gonzalezKCenterClustering.get_3MeansCost()));
		System.out.println(OctavePlots.getAssignmentBasedClusterScatterPlot(gonzalezKCenterClustering.getCenters(), gonzalezKCenterClustering.getDistanceOfPointsToClosestCenters(), "Assignment Based Gonzalez Clusters for file " + fileNameForClustering));
		
		//List to store cluster contents for each iteration
		List<Map<Point, Set<Point>>> clusterContentsPerIteration = new ArrayList<Map<Point, Set<Point>>>(); 
		
		//Lists to store 3 means cost
		List<Double> _3MeansCostKMeansPlusPlus = new ArrayList<Double>(K_MEANS_PLUS_PLUS_ITERATIONS);
	
		//Run multiple iterations for K-Means++
		KMeansPlusPlusClustering kMeansPlusPlusClustering = null;
		for (int iterationCounter = 0; iterationCounter < K_MEANS_PLUS_PLUS_ITERATIONS; ++iterationCounter) {
			
			kMeansPlusPlusClustering = new KMeansPlusPlusClustering(fileNameForClustering, DESIRED_NUMBER_OF_ASSIGNMENT_BASED_CLUSTERS);
			kMeansPlusPlusClustering.runClustering();
			clusterContentsPerIteration.add(kMeansPlusPlusClustering.getClusterContents());
			_3MeansCostKMeansPlusPlus.add(Double.valueOf(kMeansPlusPlusClustering.get_3MeansCost()));
			
			if (iterationCounter == 0) {
				System.out.println("Assignment Based K-Means Plus Plus Clustering for file " + fileNameForClustering + ":\n");
				System.out.println("3-Center cost: " + this.decimalFormat.format(kMeansPlusPlusClustering.get_3CenterCost()) + " for center " + kMeansPlusPlusClustering.get_3CenterCostCenter() + " and point " + kMeansPlusPlusClustering.get_3CenterCostPoint());
				System.out.println("3-Means cost: " + this.decimalFormat.format(kMeansPlusPlusClustering.get_3MeansCost()));
				System.out.println(OctavePlots.getAssignmentBasedClusterScatterPlot(kMeansPlusPlusClustering.getCenters(), kMeansPlusPlusClustering.getDistanceOfPointsToClosestCenters(), "Assignment Based K-Means Plus Plus Clusters for file " + fileNameForClustering));
			}
			
		}
		
		System.out.println("\nK-Means Plus Plus Clustering for file " + fileNameForClustering + ", 3-Means cumulative cost trend and cluster comparison with Gonzalez clustering :\n");
		System.out.println(OctavePlots.get3MeansCostTrend(_3MeansCostKMeansPlusPlus, "K-Means Plus Plus 3-Means Cumulative Cost Trend for file " + fileNameForClustering));
		System.out.println("\nK-Means Plus Plus Cluster Contents Intersection Size as Percentage comparison with Gonzalez clustering :\n");
		//System.out.println(OctavePlots.getClusterIntersection(gonzalezKCenterClustering.getClusterContents(), clusterContentsPerIteration, "K-Means Plus Plus Cluster Contents Intersection Size as Percentage for file " + fileNameForClustering));
		
		KMeansLLoydsClustering kMeansLLoydsClustering = new KMeansLLoydsClustering(fileNameForClustering, DESIRED_NUMBER_OF_ASSIGNMENT_BASED_CLUSTERS, null);
		kMeansLLoydsClustering.runClustering();
		System.out.println("Assignment Based LLoyd's Clustering for file " + fileNameForClustering + " and seeds as first three points:\n");
		System.out.println("3-Center cost: " + this.decimalFormat.format(kMeansLLoydsClustering.get_3CenterCost()) + " for center " + kMeansLLoydsClustering.get_3CenterCostCenter() + " and point " + kMeansLLoydsClustering.get_3CenterCostPoint());
		System.out.println("3-Means cost: " + this.decimalFormat.format(kMeansLLoydsClustering.get_3MeansCost()));
		System.out.println(OctavePlots.getAssignmentBasedClusterScatterPlot(kMeansLLoydsClustering.getCenters(), kMeansLLoydsClustering.getDistanceOfPointsToClosestCenters(), "Assignment Based LLoyd's Clusters using first 3 points as seeds for file " + fileNameForClustering));
		
		kMeansLLoydsClustering = new KMeansLLoydsClustering(fileNameForClustering, DESIRED_NUMBER_OF_ASSIGNMENT_BASED_CLUSTERS, gonzalezKCenterClustering.getCenters());
		kMeansLLoydsClustering.runClustering();
		System.out.println("Assignment Based LLoyd's Clustering for file " + fileNameForClustering + " and seeds as output of Gonzalez clustering:\n");
		System.out.println("3-Center cost: " + this.decimalFormat.format(kMeansLLoydsClustering.get_3CenterCost()) + " for center " + kMeansLLoydsClustering.get_3CenterCostCenter() + " and point " + kMeansLLoydsClustering.get_3CenterCostPoint());
		System.out.println("3-Means cost: " + this.decimalFormat.format(kMeansLLoydsClustering.get_3MeansCost()));
		System.out.println(OctavePlots.getAssignmentBasedClusterScatterPlot(kMeansLLoydsClustering.getCenters(), kMeansLLoydsClustering.getDistanceOfPointsToClosestCenters(), "Assignment Based LLoyd's Clusters using Gonzalez seeds for file " + fileNameForClustering));
		
		kMeansLLoydsClustering = new KMeansLLoydsClustering(fileNameForClustering, DESIRED_NUMBER_OF_ASSIGNMENT_BASED_CLUSTERS, kMeansPlusPlusClustering.getCenters());
		kMeansLLoydsClustering.runClustering();
		System.out.println("Assignment Based LLoyd's Clustering for file " + fileNameForClustering + " and seeds as output of k-means++ clustering:\n");
		System.out.println("3-Center cost: " + this.decimalFormat.format(kMeansLLoydsClustering.get_3CenterCost()) + " for center " + kMeansLLoydsClustering.get_3CenterCostCenter() + " and point " + kMeansLLoydsClustering.get_3CenterCostPoint());
		System.out.println("3-Means cost: " + this.decimalFormat.format(kMeansLLoydsClustering.get_3MeansCost()));
		System.out.println("3-Median cost: " + this.decimalFormat.format(kMeansLLoydsClustering.get_3MedianCost()));
		System.out.println(OctavePlots.getAssignmentBasedClusterScatterPlot(kMeansLLoydsClustering.getCenters(), kMeansLLoydsClustering.getDistanceOfPointsToClosestCenters(), "Assignment Based LLoyd's using K-Means++ seeds Clusters for file " + fileNameForClustering));
		System.out.println("\nLloyds Clustering for file " + fileNameForClustering + ", 3-Means cumulative cost trend:\n");
		System.out.println(OctavePlots.get3MeansCostTrend(kMeansLLoydsClustering.get_3MeansCostPerIteration(), "Lloyds Clustering 3-Means Cumulative Cost Trend using K-Means++ seeds for file " + fileNameForClustering));
		//System.out.println(OctavePlots.getClusterIntersection(kMeansPlusPlusClustering.getClusterContents(), kMeansLLoydsClustering.getClusterContentsPerIteration(), "Assignment Based LLoyd's using K-Means++ seeds Cluster Contents Intersection Size as Percentage for file " + fileNameForClustering));
		
		fileNameForClustering = C3_5D_DATA_FILE;
		gonzalezKCenterClustering = new GonzalezKCenterClustering(fileNameForClustering, DESIRED_NUMBER_OF_K_MEDIAN_CLUSTERS_1);
		gonzalezKCenterClustering.runClustering();
		kMeansLLoydsClustering = new KMeansLLoydsClustering(fileNameForClustering, DESIRED_NUMBER_OF_K_MEDIAN_CLUSTERS_1, gonzalezKCenterClustering.getCenters());
		kMeansLLoydsClustering.runClustering();
		System.out.println("K-Median Clustering with " + DESIRED_NUMBER_OF_K_MEDIAN_CLUSTERS_1 + " clusters for file " + fileNameForClustering + ". Output sent to file " + K_MEDIAN_OUTFILE + ":\n");
		System.out.println("3-Median Manhattan cost: " + this.decimalFormat.format(kMeansLLoydsClustering.get_3MedianCost()));
		System.out.println("3-Median Euclidian cost: " + this.decimalFormat.format(kMeansLLoydsClustering.get_3MedianEuclidianCost()));
		saveCentersToOutfile(kMeansLLoydsClustering.getCenters());

		gonzalezKCenterClustering = new GonzalezKCenterClustering(fileNameForClustering, DESIRED_NUMBER_OF_K_MEDIAN_CLUSTERS_2);
		gonzalezKCenterClustering.runClustering();
		kMeansLLoydsClustering = new KMeansLLoydsClustering(fileNameForClustering, DESIRED_NUMBER_OF_K_MEDIAN_CLUSTERS_2, gonzalezKCenterClustering.getCenters());
		kMeansLLoydsClustering.runClustering();
		System.out.println("K-Median Clustering with " + DESIRED_NUMBER_OF_K_MEDIAN_CLUSTERS_2 + " clusters for file " + fileNameForClustering + ":\n");
		System.out.println("3-Median Manhattan cost: " + this.decimalFormat.format(kMeansLLoydsClustering.get_3MedianCost()));
		System.out.println("3-Median Euclidian cost: " + this.decimalFormat.format(kMeansLLoydsClustering.get_3MedianEuclidianCost()));
		System.out.println("Centers: " + kMeansLLoydsClustering.getCenters());

	}
	
	/**
	 * Save centers to outfile whereeach line has 1 center with 6 tab separated numbers. The first being the index (e.g., 1, 2, 3 or 4), 
	 * and the next 5 being the 5-dimensional coordinates of that center.
	 * @param centers
	 */
	private void saveCentersToOutfile(List<Point> centers) {
		
		StringBuffer contentsToWriteToFile = new StringBuffer();
		
		int centerCounter = 1;
		for (Point center : centers) {
			contentsToWriteToFile.append(centerCounter++);
			for (Double coordinate : center.getCoordinates()) {
				contentsToWriteToFile.append("\t")
									 .append(coordinate.toString());
			}
			contentsToWriteToFile.append("\n");
		}

		DataFileReader.writeToFile(contentsToWriteToFile.toString(), K_MEDIAN_OUTFILE);
		
	}

}

