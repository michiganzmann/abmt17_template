package ch.ethz.matsim.courses.abmt17_template;

import org.matsim.core.events.handler.EventHandler;

import ch.ethz.matsim.av.electric.assets.station.events.VehicleArrivalEvent;


public interface VehicleArrivalEventHandler extends EventHandler {

	public void handleEvent (VehicleArrivalEvent event);
}
