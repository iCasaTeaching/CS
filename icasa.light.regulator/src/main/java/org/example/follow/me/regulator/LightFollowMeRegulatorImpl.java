package org.example.follow.me.regulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.example.follow.me.api.FollowMeConfiguration;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;


//Define this class as an implementation of a component :
@Component
//Create an instance of the component
@Instantiate(name = "my.first.follow.me")
public class LightFollowMeRegulatorImpl implements DeviceListener, FollowMeConfiguration {

	/**
	 * The name of the LOCATION property
	 */
	public static final String LOCATION_PROPERTY_NAME = "Location";

	/**
	 * BinaryLight The name of the location for unknown value
	 */
	public static final String LOCATION_UNKNOWN = "unknown";

	/**
	 * The maximum number of lights to turn on when a user enters the room :
	 **/
	private int maxLightsToTurnOnPerRoom = 2;

	@Requires(optional=true)
	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;

	@Requires(optional=true)
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;

	@Requires(optional=true)
	private DimmerLight[] dimmerLights;

	@Bind
	/** Bind Method for binaryLights dependency */
	public void bindBinaryLight(BinaryLight binaryLight, Map properties) {
		System.out.println("bind binary light " + binaryLight.getSerialNumber());
		binaryLight.addListener(this);
	}

	@Unbind
	/** Unbind Method for binaryLights dependency */
	public void unbindBinaryLight(BinaryLight binaryLight, Map properties) {
		System.out.println("unbind dimmer light " + binaryLight.getSerialNumber());
		binaryLight.removeListener(this);
	}

	@Unbind
	/** Unbind Method for dependency */
	public void unbindDimmerLight(DimmerLight dimmerLight, Map properties) {
		System.out.println("unbind dimmerlight " + dimmerLight.getSerialNumber());
		dimmerLight.removeListener(this);
	}

	@Bind
	public void bindDimmerLight(DimmerLight dimmerLight, Map properties) {
		System.out.println("bind dimmer light " + dimmerLight.getSerialNumber());
		dimmerLight.addListener(this);
	}

	@Bind
	/** Bind Method for presenceSensors dependency */
	public synchronized void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		System.out.println("bind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.addListener(this);
	}

	@Unbind
	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		System.out.println("Unbind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.removeListener(this);
	}

	@Invalidate
	/** Component Lifecycle Method */
	public synchronized void stop() {
		System.out.println("Component is stopping...");
		for (PresenceSensor sensor : presenceSensors) {
			sensor.removeListener(this);
		}
	}

	@Validate
	/** Component Lifecycle Mef (changingSensor.getSensedPresence()) {thod */
	public void start() {
		System.out.println("Component is starting...");
	}

	@Override
	public void deviceAdded(GenericDevice arg0) {
		// TODO Auto-generated method stub

	}

	@Override // Count number of lights on
	public void deviceEvent(GenericDevice arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
		// we assume that we listen only to presence sensor events (otherwise
		// there is a bug)
		// assert device instanceof PresenceSensor : "device must be a presence
		// sensors only";

		// based on that assumption we can cast the generic device without
		// checking via instanceof
		if (device instanceof PresenceSensor) {
			PresenceSensor changingSensor = (PresenceSensor) device;
			// check the change is related to presence sensing
			if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
				// get the location where the sensor is:
				String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
				// if the location is known :
				if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
					controlLightsPerRoom(detectorLocation);
				}
			}
			if (propertyName.equals(PresenceSensor.LOCATION_PROPERTY_NAME)) {
				// get the location where the sensor is:
				String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
				// if the location is known :
				if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
					controlLightsPerRoom(detectorLocation);
					controlLightsPerRoom((String) oldValue);
				}
			}
		} else if (device instanceof DimmerLight) {
			System.out.println("light changes\n\n\n");
			DimmerLight changingLight = (DimmerLight) device;
			if (propertyName.equals(DimmerLight.LOCATION_PROPERTY_NAME)) {
				// get the location where the light is:
				String detectorLocation = (String) changingLight.getPropertyValue(LOCATION_PROPERTY_NAME);
				// if the location is known :
				if (!detectorLocation.equals(LOCATION_UNKNOWN)) {

					controlLightsPerRoom(detectorLocation);
					controlLightsPerRoom((String) oldValue);
				}
			}
		}else if (device instanceof BinaryLight) {
			System.out.println("light changes\n\n\n");
			BinaryLight changingLight = (BinaryLight) device;
			if (propertyName.equals(BinaryLight.LOCATION_PROPERTY_NAME)) {
				// get the location where the light is:
				String detectorLocation = (String) changingLight.getPropertyValue(LOCATION_PROPERTY_NAME);
				// if the location is known :
				if (!detectorLocation.equals(LOCATION_UNKNOWN)) {

					controlLightsPerRoom(detectorLocation);
					controlLightsPerRoom((String) oldValue);
				}
			}
		}
	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stubchangingLight.getSerialNumber()

	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub

	}

	private synchronized List<BinaryLight> getBinaryLightFromLocation(String location) {

		List<BinaryLight> binaryLightsLocation = new ArrayList<BinaryLight>();
		for (BinaryLight binLight : binaryLights) {
			if (binLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				binaryLightsLocation.add(binLight);
			}
		}
		return binaryLightsLocation;
	}

	private synchronized List<DimmerLight> getDimmerLightFromLocation(String location) {

		List<DimmerLight> dimmerLightLocation = new ArrayList<DimmerLight>();
		for (DimmerLight dimLight : dimmerLights) {
			System.out.println("\n dimmerlight=" + dimmerLightLocation.size()
					+ "\n\n\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			if (dimLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				dimmerLightLocation.add(dimLight);
			}
		}
		return dimmerLightLocation;
	}

	/**
	 * Return nb of Lights on per location
	 * 
	 * @param location
	 * @return
	 */
	private List<BinaryLight> getBinaryLightONFromLocation(String location) {

		List<BinaryLight> binList = getBinaryLightFromLocation(location);
		List<BinaryLight> binLightsON = new ArrayList<>();
		for (BinaryLight lights : binList) {
			System.out.println(lights.getPowerStatus() + "\n\n\n\n");
			if (lights.getPowerStatus()) {
				binLightsON.add(lights);
			}
		}
		return binLightsON;
	}

	private synchronized List<PresenceSensor> getPresenceSensorFromLocation(String location) {

		List<PresenceSensor> presenceSensorsLocation = new ArrayList<PresenceSensor>();
		for (PresenceSensor sensor : presenceSensors) {
			if (sensor.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				presenceSensorsLocation.add(sensor);
			}
		}
		return presenceSensorsLocation;
	}

	private void controlBinaryLightsPerRoom(String location) {

		System.out.println("\n_\n on est dans " + location + "\n\n");
		// get the related binary lightsBinaryLightFollowMeImple
		List<BinaryLight> lights = getBinaryLightFromLocation(location);
		List<PresenceSensor> sensors = getPresenceSensorFromLocation(location);
		for (BinaryLight binaryLight : lights) {
			binaryLight.turnOff();
		}
		boolean presence = false;
		for (PresenceSensor sensor : sensors) {
			if (sensor.getSensedPresence()) {
				presence = true;
			}
		}
		if (presence) {
			int sizeList = 0;
			while (sizeList < lights.size() && sizeList < maxLightsToTurnOnPerRoom) {
				lights.get(sizeList).turnOn();
				sizeList++;
			}
		}
	}

	private void controlDimmerLightsPerRoom(String location) {

		System.out.println("\n_\n on est dans " + location + "\n\n");
		// get the related binary lights
		List<DimmerLight> lights = getDimmerLightFromLocation(location);
		List<PresenceSensor> sensors = getPresenceSensorFromLocation(location);
		for (DimmerLight dimmerLight : lights) {
			dimmerLight.setPowerLevel(0.0);
		}
		boolean presence = false;
		for (PresenceSensor sensor : sensors) {
			if (sensor.getSensedPresence()) {
				presence = true;
			}
		}
		if (presence) {
			int sizeList = 0;
			while (sizeList < lights.size() && sizeList < maxLightsToTurnOnPerRoom) {
				lights.get(sizeList).setPowerLevel(1.0);
				sizeList++;
			}
		}
	}

	private void controlLightsPerRoom(String location) {

		System.out.println("\n_\n on est dans " + location + "\n\n");
		// get the related binary lights
		List<DimmerLight> lights = getDimmerLightFromLocation(location);
		List<BinaryLight> binLights = getBinaryLightFromLocation(location);
		List<PresenceSensor> sensors = getPresenceSensorFromLocation(location);
		for (DimmerLight dimmerLight : lights) {
			dimmerLight.setPowerLevel(0.0);
		}
		for (BinaryLight blight : binLights) {
			blight.turnOff();
		}

		boolean presence = false;
		for (PresenceSensor sensor : sensors) {
			if (sensor.getSensedPresence()) {
				presence = true;
			}
		}
		if (presence) {
			int sizebin = 0;
			int sizedim = 0;
			while ((sizedim < lights.size() || sizebin < binLights.size())
					&& sizebin + sizedim < maxLightsToTurnOnPerRoom) {
				if (sizedim < lights.size() && sizebin + sizedim < maxLightsToTurnOnPerRoom) {
					lights.get(sizedim).setPowerLevel(1.0);
					sizedim++;
				}
				if (sizebin < binLights.size() && sizebin + sizedim < maxLightsToTurnOnPerRoom) {
					binLights.get(sizebin).turnOn();
					;
					sizebin++;
				}
			}

		}
	}
	@Override
	public int getMaximumNumberOfLightsToTurnOn() {
		return maxLightsToTurnOnPerRoom;
	}
	@Override
	public void setMaximumNumberOfLightsToTurnOn(int maximumNumberOfLightsToTurnOn) {
		this.maxLightsToTurnOnPerRoom=maximumNumberOfLightsToTurnOn;
		
	}

}
