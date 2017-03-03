import com.google.gson.Gson;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        dataChannel.basicConsume(dataQueue, true, dataConsumer); // comment this line out to receive less noise in output while testing commands
        statusChannel.basicConsume(statusQueue, true, statusConsumer);


        // TESTS
        // Configure a set of devices
        String gateway   = "0015BC000000000A";
        String relay     = "0015BC000000000B";
        String sensor    = "0015BC000000000C";
        String indicator = "0015BC000000000D";


        // Test LED
        testGreenLed(controlChannel, gateway, indicator);
        Thread.sleep(5000);
        testRedLed(controlChannel, gateway, indicator);
        Thread.sleep(5000);
        testTurnOffLeds(controlChannel, gateway, indicator);
        Thread.sleep(5000);
        testTurnOffLedsDelayed(controlChannel, gateway, indicator, 5000);
        Thread.sleep(10000);


        // Test RELAY
        testSwitchRelay(controlChannel, gateway, relay, false);
        Thread.sleep(5000);
        testSwitchRelay(controlChannel, gateway, relay, true);


        // Test API
        testAddUnit(controlChannel, gateway, relay, sensor, indicator);
        testAddUnit(controlChannel, gateway, relay, sensor, indicator); // try adding it again to force a failure (watch the output from 'status')
        testDeleteUnit(controlChannel, gateway);



        while (true);
//        dataChannel.close();
//        statusChannel.close();
//        controlChannel.close();
//        connection.close();
    }


    private static void testGreenLed(Channel channel, String gateway, String indicator) throws Exception {
        Command cmd = new Command("led", gateway, indicator);
        cmd.addItem("green", "on");

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testRedLed(Channel channel, String gateway, String indicator) throws Exception {
        Command cmd = new Command("led", gateway, indicator);
        cmd.addItem("red", "on");

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testTurnOffLeds(Channel channel, String gateway, String indicator) throws Exception {
        Command cmd = new Command("led", gateway, indicator);
        cmd.addItem("leds", "off");

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testTurnOffLedsDelayed(Channel channel, String gateway, String indicator, int delay) throws Exception {
        Command cmd = new Command("led", gateway, indicator);
        cmd.addItem("red", "on");
        //cmd.addItem("green", "on"); // seems only one led can be active at a time...
        cmd.addItem("delay", ""+delay);

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testSwitchRelay(Channel channel, String gateway, String relay, boolean state) throws Exception {
        Command cmd = new Command("relay", gateway, relay);
        cmd.addItem("relay", Boolean.toString(state));

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }





    private static void testAddUnit(Channel channel, String gateway, String relay, String sensor, String indicator) throws Exception {
        Command cmd = new Command("add", gateway, null);
        cmd.addItem("relay", relay);
        cmd.addItem("sensor", sensor);
        cmd.addItem("indicator", indicator);
        cmd.setCorrelation("relid-1");

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

    private static void testDeleteUnit(Channel channel, String gateway) throws Exception {
        Command cmd = new Command("delete", gateway, null);
        cmd.setCorrelation("relid-2");

        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }


}
