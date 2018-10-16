package imt.org.web.weatherdatabase.subscriber.mqtt;

import imt.org.web.weatherdatabase.datahandler.DataHandler;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.ResourceBundle;

/**
 * MQTT Publisher class
 */
public class MQTTSubscriber implements MqttCallback{

    private MqttClient subscriber;
    private MqttConnectOptions connectOptions;
    private DataHandler dataHandler;
    private String brokerUrl;
    private String topic;
    private boolean cleanSession;

    // Config file
    public static final ResourceBundle CONFIG = ResourceBundle.getBundle("config");

    public MQTTSubscriber(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        initMQTTSubscriber();

        // Temp directory
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

        try {
            connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(cleanSession);

            // Construct an MQTT blocking mode client
            subscriber = new MqttClient(this.brokerUrl, "MqttToDatabase", dataStore);
            subscriber.connect(connectOptions);
            subscriber.setCallback(this);
            subscriber.subscribe(topic);
        } catch (MqttException e) {
            System.out.println("MQTTSubscriber() - Unable to set up client : " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Init MQTTSubscriber properties
     */
    private void initMQTTSubscriber() {
        String url = CONFIG.getString("MQTTBroker");
        String port = CONFIG.getString("MQTTPort");
        String protocol = "tcp://";
        brokerUrl = protocol + url + ":" + port;
        cleanSession = false;
        topic = CONFIG.getString("MQTTTopic");
    }

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        // Called when the connection to the server has been lost.
        System.out.println("connectionLost() - Connection lost : " + cause);
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Unused - Called when a message has been delivered to the server
    }

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    public void messageArrived(String topic, MqttMessage message) {
        System.out.println("messageArrived() - Message arrive on topic " + topic +" : " + message);
        dataHandler.addMessageInQueue(message.getPayload());
    }
}
