package imt.org.web.weatherdatabase.main;

import java.io.*;

import imt.org.web.weatherdatabase.subscriber.mqtt.MQTTSubscriber;
import imt.org.web.weatherdatabase.datahandler.DataHandler;

public class Main {

    public static void main(String[] args) {

        DataHandler dataHandler = new DataHandler();
        Thread t = new Thread(dataHandler);
        t.start();

        MQTTSubscriber test = new MQTTSubscriber(dataHandler);
    }
}
