import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Database {
	private static String username = "fagprojekt";

	private static String password = "gf3qAdOPH1l9YtSp";

	private static String connectionString = "jdbc:mysql://172.22.22.104:3306/fagprojekt";
	private static Connection connection;
	private static Statement cmd;
	private static ResultSet data;

	private static long gatewaytime = 0;
	private static Map<String,Integer> gatewaycache = new HashMap<String,Integer>();
	private static long sendtime = System.currentTimeMillis() / 1000L;
	private static ArrayList<Fridgedata> sendcache = new ArrayList<Fridgedata>();
	private static long devicetime = 0;
	private static Map<String,Integer> devicecache = new HashMap<String,Integer>();

	public static void main(String[] args) {

	}

	public static void sendTomySQL(Long timestamp, String gateway, String device, int topic, String signal,
			String value) throws java.lang.ClassNotFoundException {
		if ((sendtime + 1 * 60) <= (System.currentTimeMillis() / 1000L)) {
			CommitCache();
			sendtime = System.currentTimeMillis() / 1000L;
		}
		sendcache.add(new Fridgedata(timestamp, gateway, device, topic, signal, value, false));
	}

	public static void CommitCache() throws java.lang.ClassNotFoundException {
		try {
			System.out.println("Sending");
			GatewayList(); //Update list cache
			DeviceList();
			connection = getConnection();
			String sql = "INSERT INTO `dataV2test` (`ID`, `gateway`, `device`, `timestamp`, `topic`, `value`, `signaldb`) VALUES ";

			for (int i = 0; i < sendcache.size(); i++) {
				if (i != 0)
					sql = sql + ",";
				sql = sql + "(NULL, '" + gatewaycache.get(sendcache.get(i).getGateway()) + "'";
				sql = sql + ", '" + devicecache.get(sendcache.get(i).getDevice()) + "'";
				sql = sql + ", '" + sendcache.get(i).getTimestamp() / 1000 + "'";
				sql = sql + ", '" + sendcache.get(i).getTopic() + "', '" + sendcache.get(i).getValue() + "', '"
						+ sendcache.get(i).getSignal() + "')";
			}
			sendcache.clear();
			sql = sql + ";";
			// System.out.println(sql);
			cmd = connection.createStatement();
			cmd.executeUpdate(sql);
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static Map<String,Integer> GatewayList() throws java.lang.ClassNotFoundException {
		if ((gatewaytime + 5 * 60) <= (System.currentTimeMillis() / 1000L)) {
			try {
				connection = getConnection();
				String sql = "SELECT ID,device FROM `gateways`";
				cmd = connection.createStatement();
				data = cmd.executeQuery(sql);
				gatewaycache = new HashMap<String,Integer>();
				while (data.next()) {
					gatewaycache.put(data.getString("device"),data.getInt("ID"));
				}
				connection.close();

				gatewaytime = System.currentTimeMillis() / 1000L;

				return gatewaycache;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

		} else {
			return gatewaycache;
		}
	}
	
	public static Map<String,Integer> DeviceList() throws java.lang.ClassNotFoundException {
		if ((devicetime + 5 * 60) <= (System.currentTimeMillis() / 1000L)) {
			try {
				connection = getConnection();
				String sql = "SELECT ID,device FROM `devices`";
				cmd = connection.createStatement();
				data = cmd.executeQuery(sql);
				devicecache = new HashMap<String,Integer>();
				while (data.next()) {
					devicecache.put(data.getString("device"),data.getInt("ID"));
				}
				connection.close();

				devicetime = System.currentTimeMillis() / 1000L;

				return devicecache;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

		} else {
			return devicecache;
		}
	}

	public static Connection getConnection() throws java.lang.ClassNotFoundException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(connectionString, username, password);
			if (!conn.isClosed())
				return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getRelay(String motiondevice) throws java.lang.ClassNotFoundException {
		try {
			DeviceList();
			connection = getConnection();
			String sql = "SELECT device FROM `devices` WHERE `groupID`= " + devicecache.get(motiondevice) + " AND type='relay';"; //Der skal være type=relay her
			cmd = connection.createStatement();
			data = cmd.executeQuery(sql);
			String device = "";
			while (data.next()) {
				device = data.getString("device");
			}
			connection.close();
			return device;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String,String> getDevices(String gateway) throws java.lang.ClassNotFoundException {
		try {
			GatewayList();
			connection = getConnection();
			String sql = "SELECT devices.type,devices.device FROM groups INNER JOIN devices ON groups.ID=devices.groupID WHERE gateway=" + gatewaycache.get(gateway) + ";";
			cmd = connection.createStatement();
			data = cmd.executeQuery(sql);
			Map<String,String> fromsql = new HashMap<String,String>();
			while (data.next()) {
				fromsql.put(data.getString("device"), data.getString("type"));
			}
			connection.close();
			return fromsql;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Data[] getLastTemp() throws java.lang.ClassNotFoundException {
		try {
			connection = getConnection();
			connection.close();
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;

	}

}