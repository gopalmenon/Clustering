package AssignmentBased.Clustering;

public class GonzalezKCenterClustering extends AssignmentBasedClustering {

	/**
	 * Constructor
	 * @param fileName
	 * @param desiredNumberOfClusters
	 */
	public GonzalezKCenterClustering(String fileName, int desiredNumberOfClusters) {
		super(fileName, desiredNumberOfClusters);
	}
	
	@Override
	protected ClosestCenter getNextCluster() {
		return this.distanceOfPointsToClosestCenters.remove();
	}
	
}
