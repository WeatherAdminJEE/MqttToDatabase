package imt.org.web.weatherdatabase.main;

import imt.org.web.weatherdatabase.subscriber.mqtt.MQTTSubscriber;
import imt.org.web.weatherdatabase.datahandler.DataHandler;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Main {

    public static Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        DataHandler dataHandler = new DataHandler();
        Thread t = new Thread(dataHandler);
        t.start();
        new MQTTSubscriber(dataHandler);
    }
}
