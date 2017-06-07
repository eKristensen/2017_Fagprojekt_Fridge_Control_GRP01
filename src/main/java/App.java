import com.rabbitmq.client.*;

public class App {

private static Channel dataChannel = null;
private static Channel statusChannel = null;
private static Channel controlChannel = null;

	public static void main(String[] args) throws Exception {

		String addgate = null;

		if (args.length >= 1) {
			System.out.println("Input registred. The gateway " + args[0] + " will be added.");
			addgate = args[0];
		} else {
			System.out.println("No argument, datacollection starting...");
		}
		
		mySQLtest.GatewayList();

		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("incap");
		factory.setPassword("ORDZnBMCLH4BRYAAbdi1i3jTVonWozDE");
		factory.setHost("broker.elektro.dtu.dk");
		factory.setPort(5000);
		factory.setVirtualHost("incap");
		factory.setAutomaticRecoveryEnabled(true);
		factory.useSslProtocol();

		Connection connection = factory.newConnection();
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

		Consumer dataConsumer = new Listener(dataChannel, addgate);
		Consumer statusConsumer = new Listener(statusChannel, null);

		dataChannel.basicConsume(dataQueue, true, dataConsumer);
		statusChannel.basicConsume(statusQueue, true, statusConsumer);

		// connection.close();
	}
	
	public static Channel GetChannel(String chan) {
		if (chan.equals("Control")) {
			return controlChannel;
		}
		else return null;
	}

}
