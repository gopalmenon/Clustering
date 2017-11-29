package Hierarchical.Clustering;
import java.util.Comparator;

public class HierarchicalClusterPairComparator implements Comparator<HierarchicalClusterPair> {

	@Override
	public int compare(HierarchicalClusterPair hierarchicalClusterPair1, HierarchicalClusterPair hierarchicalClusterPair2) {
		double difference = hierarchicalClusterPair1.getDistanceBetween() - hierarchicalClusterPair2.getDistanceBetween();
		if (difference > 0) {
			return 1;
		} else if (difference < 0) {
			return -1;
		} else {
			return 0;
		}
	}

}
