package objectaopejrhhoperg;

import imt.org.web.commonmodel.SensorData;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataHandler implements  Runnable{

    BlockingQueue<byte[]> queue;

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
}
