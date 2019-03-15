package fiduciagad.de.sft.main;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import fiduciagad.de.sft.mqtt.MqttSystem;

public class Detector {

	private boolean detectionIsAlive = false;
	private OpenCVHandler gameDetection = new OpenCVHandler();
	private OpenCVHandler colorGrabber = new OpenCVHandler();

	public void startTheDetection() throws MqttSecurityException, MqttException {

		start();

		colorGrabber.startPythonModule();
		colorGrabber.startTheAdjustment();

		String pythonArgument = ConfiguratorValues.getColorHSVMinH() + "," + ConfiguratorValues.getColorHSVMinS() + ","
				+ ConfiguratorValues.getColorHSVMinV() + "," + ConfiguratorValues.getColorHSVMaxH() + ","
				+ ConfiguratorValues.getColorHSVMaxS() + "," + ConfiguratorValues.getColorHSVMaxV();

		MqttSystem mqtt = new MqttSystem("localhost", 1883);

		while (detectionIsAlive) {

			mqtt.sendIdle("false");
			mqtt.sendScore("0-0");

			gameDetection.setPythonArguments(pythonArgument);
			gameDetection.startPythonModule();
			gameDetection.handleWithOpenCVOutput(this);

			mqtt.sendIdle("true");

			stop();
		}

	}

	public void setGameDetection(OpenCVHandler gameDetection) {
		this.gameDetection = gameDetection;
	}

	public void setColorGrabber(OpenCVHandler colorGrabber) {
		this.colorGrabber = colorGrabber;
	}

	public Boolean isOngoing() {
		return detectionIsAlive;
	}

	public void stop() {
		detectionIsAlive = false;
	}

	public void start() {
		detectionIsAlive = true;
	}

}
