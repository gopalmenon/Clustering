package AssignmentBased.Clustering;

import java.util.Comparator;

public class ClosestCenterComparator implements Comparator<ClosestCenter> {

	@Override
	public int compare(ClosestCenter closestCenter1, ClosestCenter closestCenter2) {

		double difference = closestCenter1.getDistanceFromPoint() - closestCenter2.getDistanceFromPoint();
		if (difference < 0.0) {
			return 1;
		} else if (difference > 0.0) {
			return -1;
		} else {
			return 0;
		}
	
	}

}
