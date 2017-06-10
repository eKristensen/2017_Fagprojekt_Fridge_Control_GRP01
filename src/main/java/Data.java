import java.io.IOException;
import java.util.UUID;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

public class Data {
private String gateway;
private String relay;
private int Temp;
private Gson gson;

public Data(String gateway, String relay, int Temp) {
	this.gateway = gateway;
	this.relay = relay;
	this.Temp = Temp;
}

public int getTemp() {
	return Temp;
}



public void changeState(Channel channel, boolean state) throws IOException {
	gson = new Gson();
    Command cmd = new Command("relay", gateway, relay);
    cmd.addParameter("relay", Boolean.toString(state));
    String correlation = UUID.randomUUID().toString();
    cmd.setCorrelation(correlation);
    System.out.println("Change state to " + state + " on relay: " + relay + " with gateway: " + gateway);
    String json = gson.toJson(cmd);
    channel.basicPublish("control", "", null, json.getBytes());
}

}
