package mqtt;
import objectaopejrhhoperg.DataHandler;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * this minimalist class is used to listen remote mqtt broker,
 * one data is received it is given to a @{@link DataHandler} in order to be processed
 */
public class MqttSubscriber implements MqttCallback{

IMqttClient subscriber;
DataHandler dataHandler;


public MqttSubscriber(String brokerURL, String topic, DataHandler dataHandler){
    try {
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
        subscriber = new MqttClient(brokerURL, "Sub1", dataStore);
        subscriber.connect();
        subscriber.setCallback(this);
        subscriber.subscribe(topic);
        subscriber.setCallback(this);
        this.dataHandler = dataHandler;
    } catch (MqttException e) {
        e.printStackTrace();
    }

    }


    public void connectionLost(Throwable throwable) {

    }

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println("Message arrive on topic "+s +" : "+mqttMessage);
        dataHandler.addMessageInQueue(mqttMessage.getPayload());
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

}