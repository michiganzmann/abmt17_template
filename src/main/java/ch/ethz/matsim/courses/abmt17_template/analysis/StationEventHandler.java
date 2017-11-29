package ch.ethz.matsim.courses.abmt17_template.analysis;

import java.util.Map;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.contrib.dvrp.data.Vehicle;

import ch.ethz.matsim.av.electric.assets.station.Station;
import ch.ethz.matsim.av.electric.assets.station.events.RechargeEndEvent;
import ch.ethz.matsim.av.electric.assets.station.events.RechargeStartEvent;
import ch.ethz.matsim.av.electric.assets.station.events.VehicleArrivalEvent;
import ch.ethz.matsim.courses.abmt17_template.RechargeEndEventHandler;
import ch.ethz.matsim.courses.abmt17_template.RechargeStartEventHandler;
import ch.ethz.matsim.courses.abmt17_template.VehicleArrivalEventHandler;

public class StationEventHandler implements RechargeStartEventHandler, RechargeEndEventHandler, VehicleArrivalEventHandler {
	
	//final private Map<Id<Station>, Id<Vehicle>> stationUsage;
	
	/*public StationEventHandler(Map<Id<Station>, Id<Vehicle>> stationUsage) {
		this.stationUsage = stationUsage;
	}
	*/
	
	@Override
	public void handleEvent( RechargeStartEvent event ) {
		
		Id<Station> stationId = event.getStationId();
		Id<Vehicle> vehicleId = event.getVehicleId();
		//this.stationUsage.put(stationId, vehicleId);
		System.out.println("Vehicle: " + vehicleId + " enters Station: " + stationId);
		
	}

	@Override
	public void handleEvent(VehicleArrivalEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEvent(RechargeEndEvent event) {
		// TODO Auto-generated method stub
		
	}

}
