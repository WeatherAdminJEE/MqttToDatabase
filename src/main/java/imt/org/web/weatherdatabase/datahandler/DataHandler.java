package imt.org.web.weatherdatabase.datahandler;

import imt.org.web.commonmodel.entities.SensorAlertParamEntity;
import imt.org.web.commonmodel.entities.SensorDataEntity;
import imt.org.web.commonmodel.entities.SensorEntity;
import imt.org.web.commonmodel.model.SensorData;
import imt.org.web.weatherdatabase.main.Main;

import java.io.*;
import java.sql.Timestamp;

/**
 * DataHandler class
 */
public class DataHandler implements Runnable {

    private byte[] message;

    /**
     * Constructor
     */
    public DataHandler(byte[] message) {
        this.message = message;
    }

    /**
     * Handle received message
     * @param message Message
     */
    private void handleMessage(byte[] message) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(message);
            ObjectInputStream ois = new ObjectInputStream(bais);
            SensorData sensorData = (SensorData)ois.readObject();
            saveSensorData(sensorData);
        } catch (ClassNotFoundException e) {
            Main.log.error("handleMessage() - Cannot found requested class - " + e.getMessage());
        } catch (IOException e) {
            Main.log.error("handleMessage() - Unable to deserialize object - " + e.getMessage());
        }
    }

    /**
     * Run thread
     */
    @Override
    public void run() {
        Main.log.debug("run() - Starting thread...");
        handleMessage(message);
    }

    /**
     * Insert received SensorData into DB
     * @param sensorData Received SensorData
     */
    private void saveSensorData(SensorData sensorData) {
        // Check if the sensor exists
        SensorEntity sensorEntity = sensorAlreadyExists(sensorData.getIdSensor());
        if(sensorEntity == null) {
            // Default sensor alert
            SensorAlertParamEntity sensorAlertParamEntity = saveDefaultSensorAlertParam();
            sensorEntity = saveSensor(sensorData, sensorAlertParamEntity);
            Main.log.debug("saveSensorData() - Sensor " + sensorData.getIdSensor() + " added !");
        } else {
            Main.log.debug("saveSensorData() - Sensor " + sensorData.getIdSensor() + " already exists");
        }

        SensorDataEntity sensorDataEntity = new SensorDataEntity(
                sensorEntity,
                sensorData.getMeasureValue(),
                sensorData.getDate()
        );
        Main.crudEntityFacade.create(sensorDataEntity);
    }

    /**
     * Check if a sensor is already registered in DB
     * @param idSensor Sensor ID
     * @return The sensor if it exists
     */
    private SensorEntity sensorAlreadyExists(int idSensor) {
        return (SensorEntity)Main.crudEntityFacade.read(SensorEntity.class, idSensor);
    }

    /**
     * Insert new sensor default alert parameters
     * @return New SensorAlertParamEntity
     */
    private SensorAlertParamEntity saveDefaultSensorAlertParam() {
        SensorAlertParamEntity sensorAlertParamEntity = new SensorAlertParamEntity(10000.0, new Timestamp(0));
        Main.crudEntityFacade.create(sensorAlertParamEntity);
        return sensorAlertParamEntity;
    }

    /**
     * Insert new sensor
     * @param sensorData SensorData
     * @param sensorAlertParamEntity SensorAlertParamEntity
     * @return New SensorEntity
     */
    private SensorEntity saveSensor(SensorData sensorData, SensorAlertParamEntity sensorAlertParamEntity) {
        SensorEntity sensorEntity = new SensorEntity(
                sensorData.getIdSensor(),
                "Sensor" + sensorData.getIdSensor(),
                sensorData.getIdCountry(),
                sensorData.getIdCity(),
                sensorData.getGpsCoordinates(),
                sensorData.getMeasureType(),
                sensorAlertParamEntity
        );
        Main.crudEntityFacade.create(sensorEntity);
        return sensorEntity;
    }
}
