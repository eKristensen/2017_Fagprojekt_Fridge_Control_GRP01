import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.UUID;

@SuppressWarnings("Duplicates")
public class InitializeDatabase {


    Gson gson = new Gson();
    Channel channel;
    public InitializeDatabase(Channel channel) throws IOException {
        this.channel = channel;
        addUnits();
    }


    public void addUnits() throws IOException {
        // 0015BC001C00119E - fryser FH1
        ApiCommand cmd = new ApiCommand("add", "0015BC001C00119E");
        cmd.addDevice("relay", "0015BC001D02019D");
        cmd.addDevice("sensor", "0015BC001A00530F");
        cmd.addDevice("indicator", "0015BC0028000062");
        sendCommand(cmd);

        // 0015BC001C0011AF - køleskab 117
        cmd = new ApiCommand("add", "0015BC001C0011AF");
        cmd.addDevice("relay", "0015BC001D02005C");
        cmd.addDevice("sensor", "0015BC001A0052F6");
        cmd.addDevice("indicator", "0015BC00280001DE");
        sendCommand(cmd);

        // 0015BC001C0011AD - test setup 1
        cmd = new ApiCommand("add", "0015BC001C0011AD");
        cmd.addDevice("relay", "0015BC001D02589F");
        cmd.addDevice("relay", "0015BC001D02008C");
        cmd.addDevice("indicator", "0015BC002800005F");
        sendCommand(cmd);



        // studenter gruppe 12 / 2017 RF
        cmd = new ApiCommand("add", "0015BC001C0011A9");
        cmd.addDevice("relay", "0015BC001D021FD2");
        cmd.addDevice("sensor", "0015BC001A0053F1");
//        cmd.addDevice("indicator", "0015C0028000613?");
        sendCommand(cmd);
        cmd = new ApiCommand("add", "0015BC001C0011B0");
        cmd.addDevice("relay", "0015BC001D021FBD");
        cmd.addDevice("sensor", "0015BC001A005647");
//        cmd.addDevice("indicator", "0015C002800044F?");
        sendCommand(cmd);
        cmd = new ApiCommand("add", "0015BC001C0011B6");
        cmd.addDevice("relay", "0015BC001D02241C");
        cmd.addDevice("sensor", "0015BC001A00535A");
//        cmd.addDevice("indicator", "0015C002800045D?");
        sendCommand(cmd);
        cmd = new ApiCommand("add", "0015BC001C00121C");
        cmd.addDevice("relay", "0015BC001D0259CC");
        cmd.addDevice("sensor", "0015BC001A00572E");
        cmd.addDevice("indicator", "0015BC0028000172");
        sendCommand(cmd);
        cmd = new ApiCommand("add", "0015BC001C00076A");
        cmd.addDevice("relay", "0015BC001D0200F6");
        cmd.addDevice("sensor", "0015BC001A001A66");
        cmd.addDevice("indicator", "0015BC002800003B");
        sendCommand(cmd);
        cmd = new ApiCommand("add", "0015BC001C000773");
        cmd.addDevice("relay", "0015BC001D020079");
        cmd.addDevice("sensor", "0015BC001A001B34");
        cmd.addDevice("indicator", "0015BC0028000237");
        sendCommand(cmd);

        // studenter gruppe ?? / 2017 RF
        cmd = new ApiCommand("add", "0015BC001C00078D");
        cmd.addDevice("relay", "0015BC001D0201C1");
        cmd.addDevice("sensor", "0015BC001A001A42");
        cmd.addDevice("indicator", "0015BC00280001D8");
        sendCommand(cmd);
        cmd = new ApiCommand("add", "0015BC001C00119D");
        cmd.addDevice("relay", "0015BC001D02238F");
        cmd.addDevice("sensor", "0015BC001A005565");
        cmd.addDevice("indicator", "0015BC0028000034");
        sendCommand(cmd);
        cmd = new ApiCommand("add", "0015BC001C0011A7");
        cmd.addDevice("relay", "0015BC001D0200A5");
        cmd.addDevice("sensor", "0015BC001A001AED");
        cmd.addDevice("indicator", "0015BC0028000100");
        sendCommand(cmd);
    }



    private void sendCommand(ApiCommand cmd) throws IOException {
        String correlation = UUID.randomUUID().toString();
        cmd.setCorrelation(correlation);
        String json = gson.toJson(cmd);
        channel.basicPublish("control", "", null, json.getBytes());
    }

}
