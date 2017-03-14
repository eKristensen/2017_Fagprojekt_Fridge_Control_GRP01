
public class Fridgedata {
	
	private Long timestamp;
	private String gateway;
	private String device;
	private String topic;
	private String signal;
	private String value;
	private boolean commited;


public Fridgedata(Long timestamp, String gateway, String device, String topic, String signal, String value, boolean commited) {
    this.timestamp = timestamp;
    this.gateway = gateway;
    this.device = device;
    this.topic = topic;
    this.signal = signal;
    this.value = value;
    this.commited = commited;
 }

public Long getTimestamp() { return timestamp; }
public String getGateway() { return gateway; }
public String getDevice() { return device; }
public String getTopic() { return topic; }
public String getSignal() { return signal; }
public String getValue() { return value; }
public boolean getCommited() { return commited; }


public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
public void setGateway(String gateway) { this.gateway = gateway; }
public void setDevice(String device) { this.device = device; }
public void setTopic(String topic) { this.topic = topic; }
public void setSignal(String signal) { this.signal = signal; }
public void setValue(String value) { this.value = value; }
public void setCommited(boolean commited) { this.commited = commited; }

public void clear() {
    this.timestamp = null;
    this.gateway = null;
    this.device = null;
    this.topic = null;
    this.signal = null;
    this.value = null;
    this.commited = true;
}

}