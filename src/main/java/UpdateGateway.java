import java.io.IOException;
//import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import com.rabbitmq.client.Channel;

import com.google.gson.Gson;

public class UpdateGateway {

    Gson gson = new Gson();
    Channel channel;

    public UpdateGateway(Channel channel, String gateway, String what)
            throws IOException, java.lang.ClassNotFoundException, Exception {
        this.channel = channel;
        if (what.equals("delete")) {
            ApiCommand cmd = new ApiCommand("delete", gateway, null);
            String correlation = UUID.randomUUID().toString();
            cmd.setCorrelation(correlation);

            String json = gson.toJson(cmd);
            channel.basicPublish("control", "", null, json.getBytes());
        } else if (what.equals("update") || what.equals("add")) {
            ApiCommand cmd = new ApiCommand("add", gateway);

            Map<String, String> devices = Database.getDevices(gateway);

            for (Map.Entry<String, String> entry : devices.entrySet()) {
                cmd.addDevice(entry.getValue(), entry.getKey());
            }

            sendCommand(cmd);
        }
    }

    private void sendCommand(ApiCommand cmd) throws IOException {
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);
        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

}
