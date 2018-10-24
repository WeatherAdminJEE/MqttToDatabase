package imt.org.web.weatherdatabase.datahandler;

import imt.org.web.commonmodel.entities.SensorAlertParamEntity;
import imt.org.web.commonmodel.entities.SensorDataEntity;
import imt.org.web.commonmodel.entities.SensorEntity;
import imt.org.web.commonmodel.model.SensorData;
import imt.org.web.weatherdatabase.main.Main;

import javax.persistence.*;
import java.io.*;
import java.sql.Timestamp;

/**
 * DataHandler class
 */
public class DataHandler implements Runnable {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("WeatherDatabase");
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
    public void handleMessage(byte[] message) {
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
    public void saveSensorData(SensorData sensorData) {
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            Main.log.debug("saveSensorData() - Begin transaction");

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

            manager.merge(sensorDataEntity);
            transaction.commit();
            Main.log.debug("saveSensorData() - Transaction success");
        } catch (PersistenceException hibernateEx) {
            Main.log.debug("saveSensorData() - Insert error - " + hibernateEx.getMessage());
            if (transaction != null) {
                transaction.rollback();
                Main.log.debug("saveSensorData() - Action rollback !\n" + hibernateEx.getMessage());
            }
        } finally {
            manager.close();
            Main.log.debug("saveSensorData() - EntityManager closed");
        }
    }

    /**
     * Check if a sensor is already registered in DB
     * @param idSensor Sensor ID
     * @return The sensor if it exists
     */
    public SensorEntity sensorAlreadyExists(int idSensor) {
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        SensorEntity sensor = manager.find(SensorEntity.class, idSensor);
        manager.close();
        return sensor;
    }

    /**
     * Insert new sensor default alert parameters
     * @return New SensorAlertParamEntity
     */
    public SensorAlertParamEntity saveDefaultSensorAlertParam() {
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        SensorAlertParamEntity sensorAlertParamEntity = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            Main.log.debug("saveDefaultSensorAlertParam() - Begin transaction");

            sensorAlertParamEntity = new SensorAlertParamEntity(10000.0, new Timestamp(0));
            manager.persist(sensorAlertParamEntity);
            manager.merge(sensorAlertParamEntity);
            transaction.commit();
            Main.log.debug("saveDefaultSensorAlertParam() - Transaction success");
        } catch (PersistenceException hibernateEx) {
            Main.log.debug("saveDefaultSensorAlertParam() - Insert error - " + hibernateEx.getMessage());
            if (transaction != null) {
                transaction.rollback();
                Main.log.debug("saveDefaultSensorAlertParam() - Action rollback !\n" + hibernateEx.getMessage());
            }
        } finally {
            manager.close();
            Main.log.debug("saveDefaultSensorAlertParam() - EntityManager closed");
            return sensorAlertParamEntity;
        }
    }

    /**
     * Insert new sensor
     * @param sensorData SensorData
     * @param sensorAlertParamEntity SensorAlertParamEntity
     * @return New SensorEntity
     */
    public SensorEntity saveSensor(SensorData sensorData, SensorAlertParamEntity sensorAlertParamEntity) {
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        SensorEntity sensorEntity = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            Main.log.debug("saveSensor() - Begin transaction");

            sensorEntity = new SensorEntity(
                    sensorData.getIdSensor(),
                    "Sensor" + sensorData.getIdSensor(),
                    sensorData.getIdCountry(),
                    sensorData.getIdCity(),
                    sensorData.getGpsCoordinates(),
                    sensorData.getMeasureType(),
                    sensorAlertParamEntity
            );
            manager.merge(sensorEntity);
            transaction.commit();
            Main.log.debug("saveSensor() - Transaction success");
        } catch (PersistenceException hibernateEx) {
            Main.log.debug("saveSensor() - Insert error - " + hibernateEx.getMessage());
            if (transaction != null) {
                transaction.rollback();
                Main.log.debug("saveSensor() - Action rollback !\n" + hibernateEx.getMessage());
            }
        } finally {
            manager.close();
            Main.log.debug("saveSensor() - EntityManager closed");
            return sensorEntity;
        }
    }
}
