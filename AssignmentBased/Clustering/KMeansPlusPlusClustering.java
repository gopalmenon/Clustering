package AssignmentBased.Clustering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class KMeansPlusPlusClustering extends AssignmentBasedClustering {

	private Random randomNumberGenerator;
	
	/**
	 * Constructor
	 * @param fileName
	 * @param desiredNumberOfClusters
	 */
	public KMeansPlusPlusClustering(String fileName, int desiredNumberOfClusters) {
		super(fileName, desiredNumberOfClusters);
		this.randomNumberGenerator = new Random(System.currentTimeMillis());
	}

	
	/* 
	 * Choose the next cluster from the set of points with probability proportional to square of the distance 
	 * of the point from the center closest to it.
	 * (non-Javadoc)
	 * @see AssignmentBased.Clustering.AssignmentBasedClustering#getNextCluster()
	 */
	@Override
	protected ClosestCenter getNextCluster() {
		
		List<ClosestCenter> closestCenters = new ArrayList<ClosestCenter>();
		List<Double> cumulativeProbabilitiesForChoosingPoint = new ArrayList<Double>();
		
		ClosestCenter closestCenter = null;
		double totalDistance = 0.0;
		Iterator<ClosestCenter> closestCenterIterator = this.distanceOfPointsToClosestCenters.iterator();
		while (closestCenterIterator.hasNext()) {
			
			closestCenter = closestCenterIterator.next();
			closestCenters.add(closestCenter);
			
			//Get the distance from its closest center
			totalDistance += closestCenter.getDistanceFromPoint();
			cumulativeProbabilitiesForChoosingPoint.add(Double.valueOf(totalDistance));

		}
		
		//Generate a random number between 0 and 1 signifying a probability
		double probabilityOfChoosingNextCenter = this.randomNumberGenerator.nextDouble();
		
		//Update cumulative probabilities
		int indexForNextCenter = 0;
		double cumulativeProbability = 0.0, currentProbability = 0.0;
		for (int listCounter = 0; listCounter < cumulativeProbabilitiesForChoosingPoint.size(); ++listCounter) {
			
			//Update the slot that contains the distance by the cumulative probability
			currentProbability = cumulativeProbabilitiesForChoosingPoint.get(listCounter) / totalDistance;
			cumulativeProbability += currentProbability;
			cumulativeProbabilitiesForChoosingPoint.set(listCounter, cumulativeProbability);
			
			//Check if the probability generated above has been reached
			if (cumulativeProbability >= probabilityOfChoosingNextCenter && (cumulativeProbability - currentProbability) <= probabilityOfChoosingNextCenter) {
				if ((cumulativeProbability - probabilityOfChoosingNextCenter) < (probabilityOfChoosingNextCenter - (cumulativeProbability - currentProbability))) {
					indexForNextCenter = listCounter;
				} else {
					if (listCounter > 0) {
						indexForNextCenter = listCounter - 1;
					} else {
						indexForNextCenter = listCounter;
					}
				}
				break;
			}
			
		}
		
		return closestCenters.get(indexForNextCenter);
	}
	
}
