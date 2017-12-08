package ch.ethz.matsim.courses.abmt17_template.analysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BelowZeroCounter {

	private final String warningFilePath;
	private final String logFilePath;
	
	public BelowZeroCounter ( String warningFilePath, String logFilePath ) {
		this.warningFilePath = warningFilePath;
		this.logFilePath = logFilePath;
	}
	
	public Map<Integer, Integer> count() throws NumberFormatException, IOException {
		
		
		Map<Integer, Integer> belowZero = new HashMap<Integer, Integer>();
		try {
			FileReader fileReaderLog = new FileReader(logFilePath);
			FileReader fileReaderWarnings = new FileReader(warningFilePath);
			BufferedReader logReader = new BufferedReader(fileReaderLog);
			BufferedReader warningReader = new BufferedReader(fileReaderWarnings);
			
			String logLine = null;
			String warningLine = null;
			Integer startTime = null;
			Integer endTime = null;
			Integer it = null;
			
			while ( (logLine = logReader.readLine()) != null) {
				if (logLine.contains("### ITERATION") && logLine.contains("BEGINS")) {
					String[] lineSplit = logLine.split(" ");
					it = Integer.parseInt(lineSplit[7]);
					String time = lineSplit[1];
					time = time.split(",")[0];
					startTime = timeToInt(time);
					belowZero.put(it, 0);
					
				}
				else if (logLine.contains("### ITERATION") && logLine.contains("ENDS")) {
					String[] lineSplit = logLine.split(" ");
					String time = lineSplit[1];
					time = time.split(",")[0];
					endTime = timeToInt(time);
					
					warningLine = warningReader.readLine();
					warningLine = warningReader.readLine();
					warningLine = warningReader.readLine();
					String warningTime = warningLine.split(" ")[1].split(",")[0];
					int warTime = timeToInt(warningTime);
					while (warTime < endTime) {
						if (warningLine.contains("dropped below zero!")) {
							belowZero.put(it, belowZero.get(it)+1);
						}
						warningLine = warningReader.readLine();
						warningTime = warningLine.split(" ")[1].split(",")[0];
						warTime = timeToInt(warningTime);
					}
				}
			}
			
			logReader.close();
			warningReader.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return belowZero;
	}
	
	private Integer timeToInt(String time ) {
		String[] timeString = time.split(":");
		Integer sec = Integer.parseInt(timeString[0])*3600 +
				Integer.parseInt(timeString[1])*60 +
				Integer.parseInt(timeString[2]);
		return sec;
	}
	
	
}
