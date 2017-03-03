import com.google.gson.annotations.SerializedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Command {
    private transient final Logger log = LoggerFactory.getLogger(this.getClass());

    private String command;
    private String gateway;
    private String unit;
    private String correlation;

    @SerializedName("params")
    private Map<String, String> parameters;


    public Command() {
        parameters = new HashMap<String, String>();
    }

    public Command(String command) {
        this();
        this.command = command;
    }

    public Command(String command, String gateway, String unit) {
        this(command);
        this.gateway = gateway;
        this.unit = unit;
    }

    public void addItem(String key, String value) {
        parameters.put(key, value);
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

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCorrelation() {
        return correlation;
    }

    public void setCorrelation(String correlation) {
        this.correlation = correlation;
    }
}
