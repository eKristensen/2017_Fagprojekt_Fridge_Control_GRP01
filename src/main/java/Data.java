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
private int TempHigh;
private int TempLow;
private Gson gson;

public Data(String gateway, String sensor, String relay, int Temp, boolean state, int TempHigh, int TempLow) {
	this.gateway = gateway;
	this.sensor = sensor;
	this.relay = relay;
	this.Temp = Temp;
	this.state = state;
	this.TempHigh = TempHigh;
	this.TempLow = TempLow;
}

public int getTemp() {
	return Temp;
}

public void setTemp(int temp) {
	Temp = temp;
}

public int getTempHigh() {
	return TempHigh;
}

public void setTempHigh(int temp) {
	TempHigh = temp;
}

public int getTempLow() {
	return TempLow;
}

public void setTempLow(int temp) {
	TempLow = temp;
}

public boolean getState() {
	return state;
}

public void setState(boolean state) {
	this.state = state;
}

public String getSensor() {
	return this.sensor;
}

public String getRelay() {
	return this.relay;
}

public void changeState(Channel channel, boolean state) throws IOException {
    Command cmd = new Command("relay", gateway, relay);
    cmd.addParameter("relay", Boolean.toString(state));
    String correlation = UUID.randomUUID().toString();
    cmd.setCorrelation(correlation);

    String json = gson.toJson(cmd);
    channel.basicPublish("control", "", null, json.getBytes());
}

public String getGateway() {
	return this.gateway;
}

}
