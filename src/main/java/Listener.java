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
			//JSONObject jsonObject = new JSONObject(message);
		    //JSONObject newJSON = jsonObject.getJSONObject("stat");
		    //System.out.println(newJSON);
		    
		    Object obj = parser.parse(message);

            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);
            
            String name = (String) jsonObject.get("topic");
            System.out.println(name);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        
        
        log.info(envelope.getRoutingKey() + ": " + message);
    }
}
