package ch.ethz.matsim.courses.abmt17_template.analysis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.GenericEventHandler;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.contrib.dvrp.data.Vehicle;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;

import ch.ethz.matsim.av.electric.assets.station.Station;
import ch.ethz.matsim.av.electric.assets.station.events.AbstractStationInteractionEvent;
import ch.ethz.matsim.av.electric.assets.station.events.RechargeEndEvent;
import ch.ethz.matsim.av.electric.assets.station.events.RechargeStartEvent;
import ch.ethz.matsim.av.electric.assets.station.events.VehicleArrivalEvent;
import ch.ethz.matsim.courses.abmt17_template.RechargeEndEventHandler;
import ch.ethz.matsim.courses.abmt17_template.RechargeStartEventHandler;
import ch.ethz.matsim.courses.abmt17_template.VehicleArrivalEventHandler;

public class StationEventHandler implements GenericEventHandler {
	
	private final Collection<Station> stations;
	private Map<Station,Integer> stationArrivals;
	private Map<Station,Integer> startRecharge;
	
	public StationEventHandler(Collection<Station> stations) {
		this.stations = stations;
		Station[] stationArray = stations.toArray(new Station[0]);
		this.stationArrivals = new HashMap<Station,Integer>();
		this.startRecharge = new HashMap<Station,Integer>();
		for (int i = 0; i < stations.size(); i++) {
			this.stationArrivals.put(stationArray[i], 0);
			this.startRecharge.put(stationArray[i], 0);
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
		}
		
		if (event.getEventType().equals("RechargeStart")) {
			
			for (Station station : this.startRecharge.keySet()) {
				if (station.getId().toString().equals(event.getAttributes().get("station"))) {
					this.startRecharge.put(station, startRecharge.get(station) + 1);
				}
			}
		}
		
	}
	
	
	
	public Map<Station,Integer> getStationArrivals() {
		return this.stationArrivals;
	}
	
	public Map<Station,Integer> getStartRecharge() {
		return this.startRecharge;
	}


	

	

}
