import java.io.IOException;
import com.rabbitmq.client.*;
import java.util.Map;

public class App {

private static Channel dataChannel = null;
private static Channel statusChannel = null;
private static Channel controlChannel = null;
private static Connection connection = null;

	public static void main(String[] args) throws Exception {
		
		//Database.getLastTemp();
		//System.exit(0);

		String addgate = null;

		if (args.length >= 1) {
			System.out.println("Input registred. The gateway " + args[0] + " will be updated.");
			addgate = args[0];
		} else {
			System.out.println("No argument, datacollection starting...");
		}
		
		Map<String,Integer> gatews = Database.GatewayList();
		
		/*
		for (Map.Entry< String,Integer> entry : gatews.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}
		
		if (gatews.containsKey("0015BC001C0011B1")) System.out.println("YES");
		
		System.out.println(gatews.get("0015BC001C0011B1")); */

		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("incap");
		factory.setPassword("ORDZnBMCLH4BRYAAbdi1i3jTVonWozDE");
		factory.setHost("broker.elektro.dtu.dk");
		factory.setPort(5000);
		factory.setVirtualHost("incap");
		factory.setAutomaticRecoveryEnabled(true);
		factory.useSslProtocol();

		connection = factory.newConnection();
		dataChannel = connection.createChannel();
		statusChannel = connection.createChannel();
		controlChannel = connection.createChannel();

		dataChannel.exchangeDeclare("data", "topic", true);
		statusChannel.exchangeDeclare("status", "topic", true);
		controlChannel.exchangeDeclare("control", "topic", true);

		String dataQueue = dataChannel.queueDeclare().getQueue();
		String statusQueue = statusChannel.queueDeclare().getQueue();

		dataChannel.queueBind(dataQueue, "data", "#");
		statusChannel.queueBind(statusQueue, "status", "#");

		Consumer dataConsumer = new Listener(dataChannel, null);
		Consumer statusConsumer = new Listener(statusChannel, addgate);

		if (addgate == null) dataChannel.basicConsume(dataQueue, true, dataConsumer);
		statusChannel.basicConsume(statusQueue, true, statusConsumer);

		
		if (addgate != null) new UpdateGateway(controlChannel,addgate);

		Database.getLastTemp();
		
		// connection.close();
	}
	
	public static Channel GetChannel(String chan) {
		if (chan.equals("Control")) {
			return controlChannel;
		}
		else return null;
	}
	
	public static void Disconnect() {
		try {
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
