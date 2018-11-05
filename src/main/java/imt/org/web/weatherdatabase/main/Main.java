package imt.org.web.weatherdatabase.main;

import imt.org.web.weatherdatabase.crud.CRUDEntityFacade;
import imt.org.web.weatherdatabase.crud.facade.IEntityFacade;
import imt.org.web.weatherdatabase.subscriber.mqtt.MQTTSubscriber;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Main {

    public static final Logger log = Logger.getLogger(Main.class);
    public static final IEntityFacade crudEntityFacade = new CRUDEntityFacade();

    public static void main(String[] args) {

        PropertyConfigurator.configure("log4j.properties");
        new MQTTSubscriber();
    }
}
