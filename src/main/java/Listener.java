import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.UUID;
import java.io.IOException;

public class Listener extends DefaultConsumer {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static int gatewaycnt = 0;
	private static String addgate = null;

	public Listener(Channel channel, String addgateinput) {
		super(channel);
		addgate = addgateinput;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
		try {
			String message = new String(body, "UTF-8");

			JSONParser parser = new JSONParser();

			try {
				Object obj = parser.parse(message);

				JSONObject jsonObject = (JSONObject) obj;

				String topic = (String) jsonObject.get("topic");

				String gateway = (String) jsonObject.get("gateway");
				
				if (addgate != null) {
					if (gateway.contains(addgate)) {
						if (gatewaycnt < 1) {
							gatewaycnt++;
						}
						else {
							App.Disconnect();
						}
					}
				}
				else {
				
					//Tjekker om gatewaylist map indeholder gateway.
				if (Database.GatewayList().containsKey(gateway)) {
					JSONObject state = (JSONObject) jsonObject.get("state");

					Long timestamp = (Long) jsonObject.get("timestamp");
					String device = (String) jsonObject.get("device");
					String relay = null, power = null, signal = null, voltage = null, current = null, light = null,
							temperature = null, motion = null;
					if (topic.equals("power")) {
						power = (String) state.get("power");
						signal = (String) state.get("signal");
						Database.sendTomySQL(timestamp, gateway, device, 7, signal, power);
					} else if (topic.equals("curvol")) {
						voltage = (String) state.get("voltage");
						current = (String) state.get("current");
						signal = (String) state.get("signal");
						Database.sendTomySQL(timestamp, gateway, device, 1, signal, voltage);
						Database.sendTomySQL(timestamp, gateway, device, 2, signal, current);
					} else if (topic.equals("light")) {
						signal = (String) state.get("signal");
						light = (String) state.get("light");
						Database.sendTomySQL(timestamp, gateway, device, 4, signal, light);
					} else if (topic.equals("temp")) {
						signal = (String) state.get("signal");
						temperature = (String) state.get("temperature");
						Database.sendTomySQL(timestamp, gateway, device, 6, signal, temperature);
					} else if (topic.equals("relay")) {
						relay = (String) state.get("relay");
						signal = "0";
						String value = "0";
						if (relay.equals("true"))
							value = "1";
						else
							value = "0";
						Database.sendTomySQL(timestamp, gateway, device, 5, signal, value);
					} else if (topic.equals("motion")) {
						motion = (String) state.get("motion");
						signal = (String) state.get("signal");
						String value = null;
						if (motion.equals("true")) {
							value = "1";
							
							Gson gson = new Gson();
						    Command cmd = new Command("relay", gateway, Database.getRelay(device));
						    System.out.println("Tænd relæet: "+Database.getRelay(device));
						    cmd.addParameter("relay", Boolean.toString(true));
						    String correlation = UUID.randomUUID().toString();
						    cmd.setCorrelation(correlation);
						    String json = gson.toJson(cmd);
						    App.GetChannel("Control").basicPublish("control", "", null, json.getBytes());
						}
						else {
							value = "0";
						}
						Database.sendTomySQL(timestamp, gateway, device, 3, signal, value);
					} else if (topic.equals("buttin")) {
						// To be added
					}
				}
				
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}

			log.info(envelope.getRoutingKey() + ": " + message);
		} catch (java.lang.ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
