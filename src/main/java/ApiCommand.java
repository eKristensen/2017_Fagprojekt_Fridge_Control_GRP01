import java.util.ArrayList;
import java.util.List;

public class ApiCommand {

    private String command;
    private String gateway;
    private List<DeviceTypePair> devices;
    private String correlation;
    private Rules rules;

    public final static String EMPTY = "0000000000000000";

    public ApiCommand() {
        devices = new ArrayList<DeviceTypePair>();
    }

    public ApiCommand(String command, String gateway) {
        this();
        this.command = command;
        this.gateway = gateway;
    }

    public ApiCommand(String command, String gateway, List<DeviceTypePair> devices) {
        this(command, gateway);
        this.devices = devices;
    }


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public List<DeviceTypePair> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceTypePair> devices) {
        this.devices = devices;
    }

    public void addDevice(String type, String device) {
        devices.add(new DeviceTypePair(type, device));
    }

    public String getCorrelation() {
        return correlation;
    }

    public void setCorrelation(String correlation) {
        this.correlation = correlation;
    }

    public boolean hasDevices() {
        return devices.size() > 0;
    }

    public boolean isValid() {
        if (command == null || gateway == null) return false;
        if (!gateway.matches("[0-9A-F]{16}")) return false;
        return true;
    }

    public boolean isDevicesValid() {
        boolean valid = true;
        for (DeviceTypePair pair : devices) {
            if (!pair.isValid()) {
                valid = false;
            }
        }
        return valid;
    }

    public Rules getRules() {
        return rules;
    }

    public void setRules(Rules rules) {
        this.rules = rules;
    }

    public static class DeviceTypePair {
        private String type;
        private String device;

        public DeviceTypePair() { }

        public DeviceTypePair(String type, String device) {
            this.type = type;
            this.device = device;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public boolean isValid() {
            if (device == null || type == null) return false;
            // slow regex check, can be optimized with a compiled pattern
            if (!device.matches("[0-9A-F]{16}")) return false;
            if (!type.matches("(relay|sensor|indicator)")) return false;
            return true;
        }
    }

    public static class Rules {
        private boolean read;   // allow publishing of data from this unit
        private boolean write;  // allow controlling of devices in this unit
        private boolean config; // allow updating of this unit

        public Rules(boolean read, boolean write, boolean config) {
            this.read = read;
            this.write = write;
            this.config = config;
        }

        public boolean isReadAllowed() {
            return read;
        }

        public void setRead(boolean read) {
            this.read = read;
        }

        public boolean isWriteAllowed() {
            return write;
        }

        public void setWrite(boolean write) {
            this.write = write;
        }

        public boolean isConfigAllowed() {
            return config;
        }

        public void setConfig(boolean config) {
            this.config = config;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (DeviceTypePair pair : devices) {
            sb.append(pair.getType() + ":" + pair.getDevice() + " ");
        }
        return "Command: " + command + ", Gateway: " + gateway + ", Devices: " + sb.toString();
    }
}
