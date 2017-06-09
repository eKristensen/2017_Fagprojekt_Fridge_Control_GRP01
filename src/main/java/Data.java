import java.io.IOException;
import java.util.UUID;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

public class Data {
private String gateway;
private String sensor;
private String relay;
private int Temp;
private boolean state;
private Gson gson;

public Data(String gateway, String sensor, String relay, int Temp, boolean state, int TempHigh, int TempLow) {
	this.gateway = gateway;
	this.sensor = sensor;
	this.relay = relay;
	this.Temp = Temp;
	this.state = state;
}

public int getTemp() {
	return Temp;
}

public void setTemp(int temp) {
	Temp = temp;
}

public boolean getState() {
	return state;
}

public void setState(boolean state) {
	this.state = state;
}

public void setRelay(String relay) {
	System.out.println("Relay set: " + relay);
	this.relay = relay;
}

public String getSensor() {
	return this.sensor;
}

public String getRelay() {
	return this.relay;
}

public void changeState(Channel channel, boolean state) throws IOException {
	gson = new Gson();
    Command cmd = new Command("relay", gateway, relay);
    cmd.addParameter("relay", Boolean.toString(state));
    String correlation = UUID.randomUUID().toString();
    cmd.setCorrelation(correlation);
    System.out.println("Change state to " + state + " on relay: " + relay + " sensor:  " + sensor + " with gateway: " + gateway);
    String json = gson.toJson(cmd);
    channel.basicPublish("control", "", null, json.getBytes());
}

public String getGateway() {
	return this.gateway;
}

}
