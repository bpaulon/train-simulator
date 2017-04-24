package bcp.imc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/** 
 * Reports events from trains and provisioning thread 
 */
public class TrainMonitor implements Observer {

	final static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss - SS");

	protected void printEventWithTimeStamp(String desc) {
		Date date = new Date();
		System.out.println("[" + sdf.format(date) + "] " + desc);
	}

	protected void handleTrainEvent(Train train, Object arg) {
		printEventWithTimeStamp(train.getId() + " - " + arg);
	}

	protected void handleProvisioningEvent(Object arg) {
		printEventWithTimeStamp(arg.toString());
	}

	/**
	 * Handles events from trains and station provisioner
	 * {@inheritDoc}
	 */
	public void update(Observable o, Object arg) {
		if (o instanceof Train) {
			handleTrainEvent((Train) o, arg);
		} else if (o instanceof StationProvisioner) {
			handleProvisioningEvent(arg);
		}
	}
}
