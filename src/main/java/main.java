import java.io.*;

import mqtt.MqttSubscriber;
import objectaopejrhhoperg.DataHandler;

public class main {

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("foo.config"));
        String jdbcURL = br.readLine();
        System.out.println(jdbcURL);
        String bddUser = br.readLine();
        System.out.println(bddUser);
        String bddPassword = br.readLine();
        System.out.println(bddPassword);
        String brokerURL = br.readLine();
        System.out.println(brokerURL);
        String topic = br.readLine();
        System.out.println(topic);
        br.close();

        DataHandler dataHandler = new DataHandler();
        Thread t = new Thread(dataHandler);
        t.start();

        MqttSubscriber test = new MqttSubscriber(brokerURL,topic,dataHandler);
    }
}
