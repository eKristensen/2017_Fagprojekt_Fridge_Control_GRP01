import com.google.gson.Gson;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.util.UUID;
import java.util.stream.IntStream;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static Gson gson;

    public static void main(String[] args) throws Exception {
        gson = new Gson();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("incap");
        factory.setPassword("ORDZnBMCLH4BRYAAbdi1i3jTVonWozDE");
        factory.setHost("broker.elektro.dtu.dk");
        factory.setPort(5000);
        factory.setVirtualHost("incap");
        factory.setAutomaticRecoveryEnabled(true);
        factory.useSslProtocol();

        Connection connection = factory.newConnection();
        Channel dataChannel = connection.createChannel();
        Channel statusChannel = connection.createChannel();
        Channel controlChannel = connection.createChannel();

        dataChannel.exchangeDeclare("data", "topic", true);
        statusChannel.exchangeDeclare("status", "topic", true);
        controlChannel.exchangeDeclare("control", "topic", true);

        String dataQueue = dataChannel.queueDeclare().getQueue();
        String statusQueue = statusChannel.queueDeclare().getQueue();
        // no queue "name" needed to publish to control

        dataChannel.queueBind(dataQueue, "data", "#");
        statusChannel.queueBind(statusQueue, "status", "#");
        // no binding needed to publish to control

        // add listeners to data and status
        Consumer dataConsumer = new Listener(dataChannel);
        Consumer statusConsumer = new Listener(statusChannel);
        // start listening to data and status
        dataChannel.basicConsume(dataQueue, true, dataConsumer); // comment this line to receive less noise in output while testing commands
        statusChannel.basicConsume(statusQueue, true, statusConsumer);

        
        

        // Initialize database
        // don't use this unless needed to, it won't help just blindly initializing the database, it will probably just mess stuff up
//        new InitializeDatabase(controlChannel);


        // TESTS
        // Configure a set of devices
        String owesome_gateway   = "0015BC001C0011A7";
        testDeleteUnit(controlChannel, owesome_gateway);
        
        //new InitGRP01(controlChannel);
        mySQLtest.getLastTemp();
        //testGetList(controlChannel, owesome_gateway); 

        AlgoritmeTest1.controlFridges(controlChannel);
        
        // Test LED
        //testGreenLed(controlChannel, gateway, indicator);
        //Thread.sleep(5000);
        //testRedLed(controlChannel, gateway, indicator);
        //Thread.sleep(5000);
        //testTurnOffLeds(controlChannel, gateway, indicator);
        //Thread.sleep(5000);
        //testTurnOffLedsDelayed(controlChannel, gateway, indicator, 5000);
        //Thread.sleep(10000);


        // Test RELAY
        //testSwitchRelay(controlChannel, gateway, relay, false);
        //Thread.sleep(5000);
        //testSwitchRelay(controlChannel, gateway, relay, true);


        // Test API
//        testAddUnit(controlChannel, gateway, relay, sensor, indicator); // add unit
//        Thread.sleep(2000);
//        testAddUnit(controlChannel, gateway, relay, sensor, indicator); // try adding it again to force a failure (watch the output from 'status')
//        Thread.sleep(2000);
//        testAddUnit(controlChannel, "0015BC0000000111", relay, sensor, indicator); // try adding new unit with a device that exists in other unit already
//        Thread.sleep(2000);
//        testAddUnit(controlChannel, "0015BC0000000222", "4YRH37FYRTUEJDYE", sensor, indicator); // try adding new unit with device with invalid addresses
//        Thread.sleep(2000);
//        testAddUnit(controlChannel, "0015BC000000XY", relay, sensor, indicator); // try adding new unit invalid gateway
//        Thread.sleep(2000);
//        testDeleteUnit(controlChannel, gateway); // delete unit
//        Thread.sleep(2000);
//        testDeleteUnit(controlChannel, gateway); // try deleting it again
//        Thread.sleep(2000);
//        testUpdateUnit(controlChannel, gateway, relay, sensor, indicator); // try updating a non-existing unit
//        Thread.sleep(2000);
//        testAddUnit(controlChannel, gateway, relay, sensor, indicator); // re-add unit
//        Thread.sleep(2000);
//        testUpdateUnit(controlChannel, gateway, relay, sensor, indicator); // update unit
//        Thread.sleep(2000);
//        testGetList(controlChannel, ApiCommand.EMPTY); // get at list of units registered
//        Thread.sleep(2000);
//        testGetList(controlChannel, gateway); // get a list of devices registered to a unit
//        Thread.sleep(2000);
//        testDeleteUnit(controlChannel, gateway); // clean up


        while (true);
//        dataChannel.close();
//        statusChannel.close();
//        controlChannel.close();
//        connection.close();
    }


    private static void testGreenLed(Channel channel, String gateway, String indicator) throws Exception {
        Command cmd = new Command("led", gateway, indicator);
        cmd.addParameter("green", "on");
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testRedLed(Channel channel, String gateway, String indicator) throws Exception {
        Command cmd = new Command("led", gateway, indicator);
        cmd.addParameter("red", "on");
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testTurnOffLeds(Channel channel, String gateway, String indicator) throws Exception {
        Command cmd = new Command("led", gateway, indicator);
        cmd.addParameter("leds", "off");
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testTurnOffLedsDelayed(Channel channel, String gateway, String indicator, int delay) throws Exception {
        Command cmd = new Command("led", gateway, indicator);
        cmd.addParameter("red", "on");
        //cmd.addItem("green", "on"); // seems only one led can be active at a time...
        cmd.addParameter("delay", ""+delay);
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testSwitchRelay(Channel channel, String gateway, String relay, boolean state) throws Exception {
        Command cmd = new Command("relay", gateway, relay);
        cmd.addParameter("relay", Boolean.toString(state));
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }





    private static void testAddUnit(Channel channel, String gateway, String relay, String sensor, String indicator) throws Exception {
        ApiCommand cmd = new ApiCommand("add", gateway);
        cmd.addDevice("relay", relay);
        cmd.addDevice("sensor", sensor);
        cmd.addDevice("indicator", indicator);
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testDeleteUnit(Channel channel, String gateway) throws Exception {
        ApiCommand cmd = new ApiCommand("delete", gateway, null);
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testUpdateUnit(Channel channel, String gateway, String relay, String sensor, String indicator) throws Exception {
        ApiCommand cmd = new ApiCommand("update", gateway);
        cmd.addDevice("relay", "0015BC0000000AA1");
        cmd.addDevice("sensor", "0015BC0000000AA2");
        cmd.addDevice("indicator", "0015BC0000000AA3");
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testGetList(Channel channel, String gateway) throws Exception {
        ApiCommand cmd = new ApiCommand("list", gateway, null);
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testAddUnitWithRules(Channel channel, String gateway, String relay, String sensor, String indicator, boolean read, boolean write, boolean config) throws Exception {
        ApiCommand cmd = new ApiCommand("add", gateway);
        cmd.setRules(new ApiCommand.Rules(read, write, config));
        cmd.addDevice("relay", relay);
        cmd.addDevice("sensor", sensor);
        cmd.addDevice("indicator", indicator);
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }


}
