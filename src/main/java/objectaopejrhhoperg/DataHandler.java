package objectaopejrhhoperg;

import imt.org.web.commonmodel.entities.SensorDataEntity;
import imt.org.web.commonmodel.model.SensorData;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataHandler implements Runnable {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("WeatherDatabase");
    private EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
    private BlockingQueue<byte[]> queue;

    public DataHandler(){
        queue = new ArrayBlockingQueue<byte[]>(50);
    }

    public void addMessageInQueue(byte[] message){
        queue.add(message);
    }

    public void handlingMessage (byte[] message) {
        try {
        ByteArrayInputStream bais = new ByteArrayInputStream(message);
        ObjectInputStream ois = new ObjectInputStream(bais);
        SensorData sd = null;

             sd = (SensorData) ois.readObject();
             insertInto(sd);

            System.out.println("date : "+sd.getDate());
            //@TODO store deserialise object into database
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true){
            try {
                byte[] message = queue.take();
                handlingMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertInto(SensorData sensorData) {
        EntityTransaction transaction = null;
        try {
            transaction = manager.getTransaction();
            transaction.begin();

            SensorDataEntity sensorDataEntity = new SensorDataEntity(
                sensorData.getIdSensor()+20+(int)Math.round(Math.random()*10),
                sensorData.getIdCountry(),
                sensorData.getIdCity(),
                sensorData.getGpsCoordinates(),
                Integer.parseInt(sensorData.getMeasureType().getValue()),
                sensorData.getMeasureValue(),
                sensorData.getDate()
            );

            manager.persist(sensorDataEntity);
            transaction.commit();
        } catch (Exception ex) {
            System.out.println("INSERT INTO error - " + ex.getMessage());
        }
    }
}
