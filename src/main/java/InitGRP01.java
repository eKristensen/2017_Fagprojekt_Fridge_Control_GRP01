import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.UUID;

@SuppressWarnings("Duplicates")
public class InitGRP01 {


    Gson gson = new Gson();
    Channel channel;
    public InitGRP01(Channel channel) throws IOException {
        this.channel = channel;
        addUnits();
    }


    public void addUnits() throws IOException {
        ApiCommand cmd = new ApiCommand("add", "0015BC001C0011A7");
        cmd.addDevice("relay", "0015BC001D0201C1");
        cmd.addDevice("relay", "0015BC001D02238F");
        cmd.addDevice("relay", "0015BC001D0200A5");
        cmd.addDevice("sensor", "0015BC001A001AED");
        cmd.addDevice("sensor", "0015BC001A005565");
        cmd.addDevice("sensor", "0015BC001A001A42");
        cmd.addDevice("indicator", "0015BC0028000100");
        cmd.addDevice("indicator", "0015BC00280001D8");
        cmd.addDevice("indicator", "0015BC0028000034");
        sendCommand(cmd);
    }



    private void sendCommand(ApiCommand cmd) throws IOException {
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);
        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

}
