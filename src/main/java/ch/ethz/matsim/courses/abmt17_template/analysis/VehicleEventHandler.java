package ch.ethz.matsim.courses.abmt17_template.analysis;

import java.util.HashMap;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.VehicleEntersTrafficEvent;
import org.matsim.api.core.v01.events.VehicleLeavesTrafficEvent;
import org.matsim.api.core.v01.events.handler.GenericEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleEntersTrafficEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleLeavesTrafficEventHandler;
import org.matsim.api.core.v01.network.Network;

import ch.ethz.matsim.courses.abmt17_template.JavaUtil.MyDoubleEntry;

public class VehicleEventHandler implements GenericEventHandler, PersonEntersVehicleEventHandler,
	PersonLeavesVehicleEventHandler, LinkLeaveEventHandler, VehicleEntersTrafficEventHandler, 
	VehicleLeavesTrafficEventHandler{
	
	private Map<String, Boolean> vehicleIsEmpty = new HashMap<String,Boolean>();
	private Map<String, MyDoubleEntry> vehicleDistances = new HashMap<String, MyDoubleEntry>();
	private Map<String, Double> vehicleCharge = new HashMap<String, Double>();
	private Map<String, MyDoubleEntry> travelTime = new HashMap<String, MyDoubleEntry>();
	
	private Network network;
	
	public VehicleEventHandler (Network network) {
		this.network = network;
	}
	
	@Override
	public void handleEvent(GenericEvent event) {
		
		// put all the av_vehicles into the maps (first lines of eventFile
		if (event.getEventType().equals("AVVehicleAssignment")) {
			String vehicle = event.getAttributes().get("vehicle");
			this.vehicleIsEmpty.put(vehicle, true);
			this.vehicleDistances.put(vehicle, new MyDoubleEntry(0.0, 0.0));
			this.vehicleCharge.put(vehicle, 0.0);
			this.travelTime.put(vehicle, new MyDoubleEntry(0.0, 0.0));
		}
		
		// subtract the remaining chargeState by starting recharging
		if (event.getEventType().equals("RechargeStart")) {
			String vehicle = event.getAttributes().get("vehicle");
			Double chargeLeft = Double.parseDouble(event.getAttributes().get("chargeState"));
			this.vehicleCharge.put(vehicle, this.vehicleCharge.get(vehicle) - chargeLeft);
		}
		
		// add the full chargeState at the end of recharging
		if (event.getEventType().equals("RechargeEnd")) {
			String vehicle = event.getAttributes().get("vehicle");
			Double fullCharge = Double.parseDouble(event.getAttributes().get("chargeState"));
			this.vehicleCharge.put(vehicle, this.vehicleCharge.get(vehicle) + fullCharge);
		}
	}
				
	@Override
	public void handleEvent(PersonEntersVehicleEvent event) {	
		// if a Person (not the driver with the av Name) enters the vehicle, the vehicle is not longer empty
		String vehicle = event.getVehicleId().toString();
		if (vehicle.contains("av")) {
			if (event.getPersonId().toString().equals(vehicle)) {}
			else {
				this.vehicleIsEmpty.put(vehicle, false);
			}
		}
	}
		
	
	@Override
	public void handleEvent(PersonLeavesVehicleEvent event) {
		
		// if a Person (not the driver with the AV-Name) leaves the vehicle, the vehicle is empty again
		String vehicle = event.getVehicleId().toString();
		if (vehicle.contains("av")) {
			if (event.getPersonId().toString().equals(vehicle)) {}
			else {
				this.vehicleIsEmpty.put(vehicle, true);
			}
		}
	}
	
	@Override
	public void handleEvent(LinkLeaveEvent event) {
		
		// add Distance to Vehicle, if empty -> total distance, with a passenger -> both
		String vehicle = event.getVehicleId().toString();
		if (vehicle.contains("av")) {
			String link = event.getLinkId().toString();
			Double distance = network.getLinks().get(Id.createLinkId(link)).getLength();
			MyDoubleEntry oldDist = this.vehicleDistances.get(vehicle);
			
			if (this.vehicleIsEmpty.get(vehicle)) {
				this.vehicleDistances.put(vehicle, oldDist.addTotal(distance));
				}
			else {
				this.vehicleDistances.put(vehicle, oldDist.addWithPassenger(distance));
			}
		}
	}
	
	@Override
	public void handleEvent(VehicleLeavesTrafficEvent event) {
		
		// subtract Time to Vehicle when enter traffic, if empty -> total time, with a passenger -> both
		String vehicle = event.getVehicleId().toString();
		if (vehicle.contains("av")) {
			Double time = event.getTime();
			MyDoubleEntry oldTime = this.travelTime.get(vehicle);
			
			if (this.vehicleIsEmpty.get(vehicle)) {
				this.travelTime.put(vehicle, oldTime.addTotal(time));
			}
			else {
				this.travelTime.put(vehicle, oldTime.addWithPassenger(time));
			}
		}
	}
	
	@Override
	public void handleEvent(VehicleEntersTrafficEvent event) {
	
		// add Time to Vehicle when leave traffic, if empty -> total time, with a passenger -> both
		String vehicle = event.getVehicleId().toString();
		if (vehicle.contains("av")) {
			Double time = event.getTime();
			MyDoubleEntry oldTime = this.travelTime.get(vehicle);
			
			if (this.vehicleIsEmpty.get(vehicle)) {
				this.travelTime.put(vehicle, oldTime.addTotal(-time));
				}
			else {
				this.travelTime.put(vehicle, oldTime.addWithPassenger(-time));
			}
		}
		
	}
	
	// get-Methods for main method
	public Map<String, Boolean> getVehicleIsEmpty(){
		return this.vehicleIsEmpty;
	}
	public Map<String, MyDoubleEntry> getVehicleDistances() {
		return this.vehicleDistances;
	}
	public Map<String, Double> getVehicleCharge() {
		return this.vehicleCharge;
	}
	public Map<String, MyDoubleEntry> getTravelTime() {
		return this.travelTime;
	}
	
	
}
