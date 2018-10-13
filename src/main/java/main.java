import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

import mqtt.MqttSubscriber;
import objectaopejrhhoperg.DataHandler;

public class main {

    public static void main(String[] args) throws IOException {
        Connection conn = null;

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

        try {
            conn = DriverManager.getConnection(jdbcURL,bddUser, bddPassword);
        } catch (SQLException ex) {

            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        DataHandler dataHandler = new DataHandler();
        Thread t = new Thread(dataHandler);
        t.start();

        MqttSubscriber test = new MqttSubscriber(brokerURL,topic,dataHandler);

        System.out.println(conn);



    }
}
