import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Listener extends DefaultConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Listener(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        
        JSONParser parser = new JSONParser();
        
		try {
		    Object obj = parser.parse(message);

            JSONObject jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject);
            
            String topic = (String) jsonObject.get("topic");

        	//System.out.println("topic: " + topic);
            String gateway = (String) jsonObject.get("gateway");
            if ((topic.equals("power") || topic.equals("curvol") || topic.equals("light") 
            		|| topic.equals("temp") || topic.equals("motion") || topic.equals("buttin"))
            		&& (gateway.equals("0015BC001C0011A7") || gateway.equals("0015BC001C00119D")) ) {
            	JSONObject state = (JSONObject)jsonObject.get("state");
                //System.out.println(state.get("signal"));
                
                Long timestamp = (Long) jsonObject.get("timestamp");
                String device = (String) jsonObject.get("device");
                String relay = null,power = null,signal = null,voltage = null,current = null,light = null,temperature = null,motion = null;
                if (topic == "relay") {
                	relay = (String) state.get("relay");
                	mySQLtest.sendTomySQL(timestamp, gateway, device, topic, signal, relay);
                }
                else if (topic.equals("power")) {
                	power = (String) state.get("power");
                	signal = (String) state.get("signal");
                	mySQLtest.sendTomySQL(timestamp, gateway, device, topic, signal, power);
                }
                else if (topic.equals("curvol")) {
                	voltage = (String) state.get("voltage");
                	current = (String) state.get("current");
                	signal = (String) state.get("signal");
                	mySQLtest.sendTomySQL(timestamp, gateway, device, "voltage", signal, voltage);
                	mySQLtest.sendTomySQL(timestamp, gateway, device, "current", signal, current);
                }
                else if (topic.equals("light")) {
                	signal = (String) state.get("signal");
                	light = (String) state.get("light");
                	mySQLtest.sendTomySQL(timestamp, gateway, device, topic, signal, light);
                }
                else if (topic.equals("temp")) {
                	signal = (String) state.get("signal");
                	temperature = (String) state.get("temperature");
                	mySQLtest.sendTomySQL(timestamp, gateway, device, topic, signal, temperature);
                }
                else if (topic.equals("motion")) {
                	motion = (String) state.get("motion");
                	signal = (String) state.get("signal");    
                	String value = null;
                	if (motion.equals("true")) value = "1";
                	else value = "0";
                	mySQLtest.sendTomySQL(timestamp, gateway, device, topic, signal, value);        	
                }
                else if (topic.equals("buttin")) {
                	//To be added
                }
            }
            
            
            
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        
        
        log.info(envelope.getRoutingKey() + ": " + message);
    }

}
