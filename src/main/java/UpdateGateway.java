import java.io.IOException;
//import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import com.rabbitmq.client.Channel;

import com.google.gson.Gson;

public class UpdateGateway {

	Gson gson = new Gson();
	Channel channel;

	public UpdateGateway(Channel channel, String gateway) throws IOException, java.lang.ClassNotFoundException, Exception {
		this.channel = channel;
		ApiCommand cmd = new ApiCommand("delete", gateway, null);
		String correlation = UUID.randomUUID().toString();
		cmd.setCorrelation(correlation);

		String json = gson.toJson(cmd);
		channel.basicPublish("control", "", null, json.getBytes());

		cmd = new ApiCommand("add", gateway);
		
		
		Map<String,String> devices = Database.getDevices(gateway);
		
	//	for (int i = 0; i < devices.size(); i++) {
	//		System.out.println("FIX MISSING HERE");
//			cmd.addDevice(devices.get(i).getTopic(), devices.get(i).getDevice());
	//	}
		
		for (Map.Entry<String, String> entry : devices.entrySet()) {
			//System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			cmd.addDevice(entry.getValue(), entry.getKey());
			//cmd.addDevice(devices.get(i).getTopic(), devices.get(i).getDevice());
		}
		
		sendCommand(cmd);
		
		//App.Disconnect();
	}

	private void sendCommand(ApiCommand cmd) throws IOException {
		String correlation = UUID.randomUUID().toString();
		cmd.setCorrelation(correlation);
		String json = gson.toJson(cmd);
		channel.basicPublish("control", "", null, json.getBytes());
	}

}
