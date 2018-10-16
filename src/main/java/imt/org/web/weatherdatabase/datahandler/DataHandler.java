package imt.org.web.weatherdatabase.datahandler;

import imt.org.web.commonmodel.entities.SensorDataEntity;
import imt.org.web.commonmodel.entities.SensorEntity;
import imt.org.web.commonmodel.model.SensorData;
import imt.org.web.weatherdatabase.main.Main;

import javax.persistence.*;
import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataHandler implements Runnable {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("WeatherDatabase");
    private BlockingQueue<byte[]> queue;

    /**
     * Constructor
     */
    public DataHandler() {
        queue = new ArrayBlockingQueue<byte[]>(100);
    }

    /**
     * Add a received message in the blocking queue
     * @param message Received message
     */
    public void addMessageInQueue(byte[] message) {
        queue.add(message);
    }

    /**
     * Handle received message
     * @param message Message
     */
    public void handleMessage(byte[] message) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(message);
            ObjectInputStream ois = new ObjectInputStream(bais);
            SensorData sd = (SensorData)ois.readObject();
            saveSensorData(sd);
        } catch (ClassNotFoundException e) {
            Main.log.info("handleMessage() - Cannot found requested class - " + e.getMessage());
            return;
        } catch (IOException e) {
            Main.log.error("handleMessage() - Unable to deserialize object - " + e.getMessage());
        }
    }

    /**
     *
     */
    @Override
    public void run() {
        Main.log.debug("run() - Starting thread...");
        while (true){
            try {
                byte[] message = queue.take();
                handleMessage(message);
            } catch (InterruptedException e) {
                Main.log.error("run() - Thread interrupted - " + e.getMessage());
            }
        }
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
            if(!sensorAlreadyExists(sensorData.getIdSensor())) {
                // If not, register the new sensor
                SensorEntity sensorEntity = new SensorEntity(
                    sensorData.getIdSensor(),
                    "Sensor" + sensorData.getIdSensor()
                );
                manager.merge(sensorEntity);
                Main.log.debug("saveSensorData() - Sensor " + sensorData.getIdSensor() + " added !");
            }
            Main.log.debug("saveSensorData() - Sensor " + sensorData.getIdSensor() + " already exists");

            // Create entity from MQTT received data
            SensorDataEntity sensorDataEntity = new SensorDataEntity(
                sensorData.getIdSensor(),
                sensorData.getIdCountry(),
                sensorData.getIdCity(),
                sensorData.getGpsCoordinates(),
                Integer.parseInt(sensorData.getMeasureType().getValue()),
                sensorData.getMeasureValue(),
                sensorData.getDate()
            );


            manager.merge(sensorDataEntity);
            transaction.commit();
            Main.log.debug("saveSensorData() - SensorEntity added !\n" + sensorDataEntity);
        } catch (PersistenceException hibernateEx) {
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
     * @return Sensor already exists ? True : False
     */
    public boolean sensorAlreadyExists(int idSensor) {
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        boolean sensorExists = !manager.createQuery("from SensorEntity where idSensor=:idSensor")
                .setParameter("idSensor", idSensor).getResultList().isEmpty();
        manager.close();
        return sensorExists;
    }
}
