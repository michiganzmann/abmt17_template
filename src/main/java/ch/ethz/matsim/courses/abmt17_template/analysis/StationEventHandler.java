package ch.ethz.matsim.courses.abmt17_template.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.events.handler.GenericEventHandler;

import ch.ethz.matsim.av.electric.assets.station.Station;

public class StationEventHandler implements GenericEventHandler {
	
	private Map<Station,Integer> stationArrivals;
	private Map<Station,Integer> startRecharge;
	private Map<Station,Integer> endRecharge;
	
	private List<String> stationNumber = new ArrayList<String>();
	
	public StationEventHandler(Collection<Station> stations) {
		Station[] stationArray = stations.toArray(new Station[0]);
		this.stationArrivals = new HashMap<Station,Integer>();
		this.startRecharge = new HashMap<Station,Integer>();
		this.endRecharge = new HashMap<Station,Integer>();
		
		for (int i = 0; i < stations.size(); i++) {
			this.stationArrivals.put(stationArray[i], 0);
			this.startRecharge.put(stationArray[i], 0);
			this.endRecharge.put(stationArray[i], 0);
		}
	}
	
	
	@Override
	public void handleEvent(GenericEvent event) {
		if (event.getEventType().equals("VehicleArrival")) {
			
			for (Station station : this.stationArrivals.keySet()) {
				if (station.getId().toString().equals(event.getAttributes().get("station"))) {
					this.stationArrivals.put(station, stationArrivals.get(station) + 1);
				}
			}

			setStationActivity(164,event);
			
		}
		
		if (event.getEventType().equals("RechargeStart")) {
			
			for (Station station : this.startRecharge.keySet()) {
				if (station.getId().toString().equals(event.getAttributes().get("station"))) {
					this.startRecharge.put(station, startRecharge.get(station) + 1);
				}
			}
			setStationActivity(164,event);
		}
		
		if (event.getEventType().equals("RechargeEnd")) {
			
			for (Station station : this.endRecharge.keySet()) {
				if (station.getId().toString().equals(event.getAttributes().get("station"))) {
					this.endRecharge.put(station, endRecharge.get(station) + 1);
				}
			}
			setStationActivity(164,event);
		}
		
	}
	
	
	
	public Map<Station,Integer> getStationArrivals() {
		return this.stationArrivals;
	}
	
	public Map<Station,Integer> getStartRecharge() {
		return this.startRecharge;
	}
	
	public Map<Station,Integer> getEndRecharge() {
		return this.endRecharge;
	}


	private void setStationActivity(int station, GenericEvent event) {

		if (event.getAttributes().get("station").equals("Station_164")) {
			this.stationNumber.add(event.getTime() +","+ event.getAttributes().get("vehicle") 
					+","+ event.getEventType());
		}
	}
	
	public List<String> getStationActivity(){
		return this.stationNumber;
	}

	

}
