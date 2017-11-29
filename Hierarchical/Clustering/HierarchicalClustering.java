package Hierarchical.Clustering;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import Clustering.InterPointDistances;
import Clustering.Point;

/**
 * Runs Hierarchical clustering 
 *
 */
public class HierarchicalClustering {
	
	public static final String SINGLE_LINK = "S";
	public static final String COMPLETE_LINK = "C";
	public static final String MEAN_LINK = "M";
	
	private int desiredNumberOfClusters;
	private InterPointDistances interPointDistances;
	private String distanceMeasure;
	
	/**
	 * Constructor
	 * @param desiredNumberOfClusters
	 * @param pointsFileName
	 * @param distanceMeasure
	 */
	public HierarchicalClustering(int desiredNumberOfClusters, String pointsFileName, String distanceMeasure) {
		this.desiredNumberOfClusters = desiredNumberOfClusters;
		this.interPointDistances = InterPointDistances.getInstance(pointsFileName);
		if (!SINGLE_LINK.equals(distanceMeasure) && !COMPLETE_LINK.equals(distanceMeasure) && !MEAN_LINK.equals(distanceMeasure)) {
			throw new InvalidParameterException(distanceMeasure != null && distanceMeasure.trim().length() > 0 ? distanceMeasure.trim() : "null" + 
						" is not a valid distance measure paramater. Valid values are " + SINGLE_LINK + " for Single Link, " + COMPLETE_LINK + " for Complete Link and " + MEAN_LINK + " for Mean Link.");
		} else {
			this.distanceMeasure = distanceMeasure;
		}
	}
	
	/**
	 * @return a list of hierarchical clusters
	 */
	public List<HierarchicalCluster> getClusters() {
		
		
		List<HierarchicalCluster> clusters = getInitialClusters();
		
		PriorityQueue<HierarchicalClusterPair> priorityQueue = getHierarchicalClusterPairsOrderedByInterClusterDistance(clusters);
		
		while (clusters.size() > this.desiredNumberOfClusters) {
			
			//Get next cluster pair to merge
			HierarchicalClusterPair hierarchicalClusterPair = priorityQueue.remove();
		
			//Merge the clusters
			HierarchicalCluster mergedCluster = hierarchicalClusterPair.cluster1.mergeWithCluster(hierarchicalClusterPair.cluster2);
			
			//Remove distance measures for the merged clusters
			removeMergedClustersFromDistanceMeasures(priorityQueue, hierarchicalClusterPair);
			
			//Remove merged clusters from current list of clusters
			clusters.remove(hierarchicalClusterPair.cluster1);
			clusters.remove(hierarchicalClusterPair.cluster2);
			
			//Flag merged clusters for garbage collection
			hierarchicalClusterPair.cluster1 = null;
			hierarchicalClusterPair.cluster2 = null;
			hierarchicalClusterPair = null;
			
			//Add distances between new merged cluster and other cluster to the distance measures
			addDistancesToMergedCluster(clusters, mergedCluster, priorityQueue);	
			
			//Add merged cluster to list of clusters
			clusters.add(mergedCluster);
			
		}
		
		return clusters;
		
	}
	
	/**
	 * @return initial list of clusters where each cluster is a point
	 */
	private List<HierarchicalCluster> getInitialClusters() {
		
		List<Point> allPoints = this.interPointDistances.getAllPoints();
		List<HierarchicalCluster> initialClusters = new ArrayList<HierarchicalCluster>(allPoints.size());
		
		for (Point point : allPoints) {
			initialClusters.add(new HierarchicalCluster(new ArrayList<Point>(Arrays.asList(point))));
		}
		
		
		return initialClusters;
		
	}
	
	/**
	 * @param numberOfInitialPoints
	 * @return number of all possible pairs that can be formed from the initial points
	 */
	private int getNumberOfAllPossiblePairs(int numberOfInitialPoints) {
		return numberOfInitialPoints * (numberOfInitialPoints - 1) / 2 * (numberOfInitialPoints - 2);
	}
	
	/**
	 * @param initialClusters
	 * @return a priority queue that orders cluster pairs by the distance between them
	 */
	private PriorityQueue<HierarchicalClusterPair> getHierarchicalClusterPairsOrderedByInterClusterDistance(List<HierarchicalCluster> initialClusters) {
		
		PriorityQueue<HierarchicalClusterPair> priorityQueue = new PriorityQueue<HierarchicalClusterPair>(getNumberOfAllPossiblePairs(initialClusters.size()), new HierarchicalClusterPairComparator());
		for (int clusterCounter = 0; clusterCounter < initialClusters.size(); ++clusterCounter) {
			for (int otherClusterCounter = clusterCounter + 1; otherClusterCounter < initialClusters.size(); ++otherClusterCounter) {
				
				if (this.distanceMeasure.equals(SINGLE_LINK)) {
					priorityQueue.add(new SingleLinkHierarchicalClusterPair(initialClusters.get(clusterCounter), initialClusters.get(otherClusterCounter)));
				} else if (this.distanceMeasure.equals(COMPLETE_LINK)) {
					priorityQueue.add(new CompleteLinkHierarchicalClusterPair(initialClusters.get(clusterCounter), initialClusters.get(otherClusterCounter)));
				} else {
					priorityQueue.add(new MeanLinkHierarchicalClusterPair(initialClusters.get(clusterCounter), initialClusters.get(otherClusterCounter)));
				}
				
			}
		}
		
		return priorityQueue;
		
	}
	
	/** Remove distance measures between components of merged cluster and other clusters
	 * @param priorityQueue
	 * @param mergedClusterPair
	 */
	private void removeMergedClustersFromDistanceMeasures(PriorityQueue<HierarchicalClusterPair> priorityQueue, HierarchicalClusterPair mergedClusterPair) {
		
		//Look at every entry in the queue and mark it for removal if it contains either of the merged clusters
		List<HierarchicalClusterPair> clusterPairsToBeRemoved = new ArrayList<HierarchicalClusterPair>();
		Iterator<HierarchicalClusterPair> priorityQueueIterator = priorityQueue.iterator();
		HierarchicalClusterPair clusterPair = null;
		while (priorityQueueIterator.hasNext()) {
		
			clusterPair = priorityQueueIterator.next();
			if (clusterPair.cluster1.equals(mergedClusterPair.cluster1) || clusterPair.cluster1.equals(mergedClusterPair.cluster2) || clusterPair.cluster2.equals(mergedClusterPair.cluster1) || clusterPair.cluster2.equals(mergedClusterPair.cluster2)) {
				clusterPairsToBeRemoved.add(clusterPair);
			}
			
		}
		
		//Remove clusters marked for removal
		for (HierarchicalClusterPair clusterPairToBeRemoved : clusterPairsToBeRemoved) {
			priorityQueue.remove(clusterPairToBeRemoved);
		}
		
	}
	
	/**
	 * Store distances from merged cluster to all other clusters
	 * @param existingClusters
	 * @param mergedCluster
	 * @param priorityQueue
	 */
	private void addDistancesToMergedCluster(List<HierarchicalCluster> existingClusters, HierarchicalCluster mergedCluster, PriorityQueue<HierarchicalClusterPair> priorityQueue) {
		
		for (HierarchicalCluster cluster : existingClusters) {
			
			if (this.distanceMeasure.equals(SINGLE_LINK)) {
				priorityQueue.add(new SingleLinkHierarchicalClusterPair(cluster, mergedCluster));
			} else if (this.distanceMeasure.equals(COMPLETE_LINK)) {
				priorityQueue.add(new CompleteLinkHierarchicalClusterPair(cluster, mergedCluster));
			} else {
				priorityQueue.add(new MeanLinkHierarchicalClusterPair(cluster, mergedCluster));
			}
		
		}
		
	}

}
