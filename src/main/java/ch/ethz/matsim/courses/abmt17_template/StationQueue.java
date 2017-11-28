package ch.ethz.matsim.courses.abmt17_template;

import ch.ethz.matsim.av.electric.assets.station.Station;

public interface StationQueue extends Station {
	
	public int getWaitingVehicles();
	public int getRechargingVehicles();
	public int getFinishedVehicles();

}
