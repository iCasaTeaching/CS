package org.example.follow.me.manager;


import java.util.Map;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.example.follow.me.api.FollowMeAdministration;
import org.example.follow.me.api.FollowMeConfiguration;
import org.example.follow.me.api.IlluminanceGoal;



//Define this class as an implementation of a component :
@Component
//Create an instance of the component
@Instantiate(name = "FollowMeManager-1")

public class LightFollowMeManagerImpl implements FollowMeAdministration {

	@Requires(optional=true)
	/** Field for followMeConfiguration dependency */
	private FollowMeConfiguration[] followMeConfiguration;

	@Bind
	/** Bind Method for followMeConfiguration dependency */
	public void bindFollowMeConfiguration(FollowMeConfiguration followMeConfiguration, Map properties) {
	}

	@Unbind
	/** Unbind Method for followMeConfiguration dependency */
	public void unbindFollowMeConfiguration(FollowMeConfiguration followMeConfiguration, Map properties) {
		// TODO: Add your implementation code here
	}

	@Invalidate
	/** Component Lifecycle Method */
	public void stop() {
		// TODO: Add your implementation code here
	}

	@Validate
	/** Component Lifecycle Method */
	public void start() {
		// TODO: Add your implementation code here
		System.out.println("start manager....\n");
	}

	@Override
	public void setIlluminancePreference(IlluminanceGoal illuminanceGoal) {
		followMeConfiguration[0].setMaximumNumberOfLightsToTurnOn(illuminanceGoal.getNumberOfLightsToTurnOn());
	}

	@Override
	public IlluminanceGoal getIlluminancePreference() {
		switch (followMeConfiguration[0].getMaximumNumberOfLightsToTurnOn()){
		case 1 :
			return IlluminanceGoal.SOFT;
		case 2 :
			return IlluminanceGoal.MEDIUM;
		case 3 :
			return IlluminanceGoal.FULL;
		default:
			return null;
		}
	}
}
