package Clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataFileReader {
	
	public static final String WHITESPACE_REGEX = "\\s";

	public static List<Point> getPointsFromTextFile(String fileName) {
		
		//Read text file contents
		List<String> dataFileContents = getDataFileContents(fileName);
		List<Point> allPoints = new ArrayList<Point>(dataFileContents.size());
		Point point = null;
		for (String dataFileLine : dataFileContents) {
			point = getPoint(dataFileLine);
			if (point != null) {
				allPoints.add(point);
			}
		}
		
		return allPoints;
		
	}
	/**
	 * @param coordinatesInTextFormat
	 * @return Point consisting of the coordinates
	 */
	private static Point getPoint(String coordinatesInTextFormat) {
		
		String[] tokens = coordinatesInTextFormat.split(WHITESPACE_REGEX);
		if (tokens.length > 0) {
			try {
				List<Double> coordinates = new ArrayList<Double>(tokens.length - 1);
				int tokenCounter = 0;
				for (String token : tokens) {
					
					//The first token is always an index, so skip it
					if (tokenCounter++ == 0) {
						continue;
					}
					
					//Add coordinate to list of coordinates
					coordinates.add(Double.valueOf(Double.parseDouble(token)));
				}
				
				return new Point(coordinates);
				
			} catch(NumberFormatException e) {
				System.err.println("Invalid coordinate value found.");
				e.printStackTrace();
				System.exit(0);
			}
			
		}
		
		return null;
		
	}
	
	
	/**
	 * @param filePath
	 * @return file contents as a list of strings
	 * @throws IOException
	 */
	private static List<String> getDataFileContents(String filePath) {
		
		List<String> fileContents = new ArrayList<String>();
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
			String fileLine = bufferedReader.readLine();
			
			while (fileLine != null) {
				
				if (fileLine.trim().length() > 0) {
					fileContents.add(fileLine.trim());
				}

				fileLine = bufferedReader.readLine();
			}
			
			bufferedReader.close();
			
		} catch (IOException e) {
			System.err.println("Error while reading from file " + filePath + ".");
			e.printStackTrace();
			System.exit(0);
		}
		
		return fileContents;

	}
	
	/**
	 * @param contents
	 * @param filename
	 */
	public static void writeToFile(String contents, String filename) {
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {

			bw.write(contents);

		} catch (IOException e) {

			e.printStackTrace();

		}
		
	}

}
