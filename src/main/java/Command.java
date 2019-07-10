import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Command {

    private String command;
    private String gateway;
    private String device;
    private String correlation;

    @SerializedName("params")
    private Map<String, String> parameters;

    public Command() {
        parameters = new HashMap<String, String>();
    }

    public Command(String command, String gateway, String device) {
        this();
        this.command = command;
        this.gateway = gateway;
        this.device = device;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public boolean hasParameters() {
        return parameters.size() > 0;
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getCorrelation() {
        return correlation;
    }

    public void setCorrelation(String correlation) {
        this.correlation = correlation;
    }

    public boolean isValid() {
        if (command == null || gateway == null || device == null)
            return false;
        // slow regex check, can be optimized with a compiled pattern
        if (!gateway.matches("[0-9A-F]{16}"))
            return false;
        if (!device.matches("[0-9A-F]{16}"))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : parameters.keySet()) {
            sb.append(key + ":" + parameters.get(key) + " ");
        }
        return "Command: " + command + ", Gateway: " + gateway + ", Device: " + device + ", Parameters: "
                + sb.toString();
    }
}
