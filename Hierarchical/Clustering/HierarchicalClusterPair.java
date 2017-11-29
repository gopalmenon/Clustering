package Hierarchical.Clustering;
/**
 * Hierarchical cluster with distance between them defined by the subclass
 *
 */
public abstract class HierarchicalClusterPair {
	
	protected HierarchicalCluster cluster1, cluster2;
	protected double distanceBetween;
	
	public HierarchicalClusterPair(HierarchicalCluster cluster1, HierarchicalCluster cluster2) {
		this.cluster1 = cluster1;
		this.cluster2 = cluster2;
		setDistanceBetween();
	}
	
	protected abstract void setDistanceBetween();
	
	public double getDistanceBetween() {
		return this.distanceBetween;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (other instanceof HierarchicalClusterPair) {
			if ((this.cluster1.equals(((HierarchicalClusterPair) other).cluster1) && this.cluster2.equals(((HierarchicalClusterPair) other).cluster2)) ||
				(this.cluster1.equals(((HierarchicalClusterPair) other).cluster2) && this.cluster2.equals(((HierarchicalClusterPair) other).cluster1))) {
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
		
		return this.cluster1.hashCode() + this.cluster2.hashCode();

	}
}
