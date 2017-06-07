import java.sql.*;
import java.util.ArrayList;

public class mySQLtest {
	private static String username = "fagprojekt";

	private static String password = "gf3qAdOPH1l9YtSp";

	private static String connectionString = "jdbc:mysql://172.22.22.104:3306/fagprojekt";
	private static Connection connection;
	private static Statement cmd;
	private static ResultSet data;

	private static long gatewaytime = 0;
	private static String[] gatewaycache = new String[0];
	private static long sendtime = System.currentTimeMillis() / 1000L;
	private static ArrayList<Fridgedata> sendcache = new ArrayList<Fridgedata>();

	public static void main(String[] args) {

	}

	public static void sendTomySQL(Long timestamp, String gateway, String device, String topic, String signal,
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
			connection = getConnection();
			String sql = "INSERT INTO `data` (`ID`, `gateway`, `device`, `timestamp`, `topic`, `value`, `signaldb`) VALUES ";

			for (int i = 0; i < sendcache.size(); i++) {
				if (i != 0)	sql = sql + ",";
				sql = sql + "(NULL, '" + sendcache.get(i).getGateway() + "'";
				sql = sql + ", '" + sendcache.get(i).getDevice() + "'";
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

	public static String[] GatewayList() throws java.lang.ClassNotFoundException {
		// System.out.println("nextupdate: "+(gatewaytime+ 5 * 60)+" current
		// time: "+(System.currentTimeMillis() / 1000L));
		if ((gatewaytime + 5 * 60) <= (System.currentTimeMillis() / 1000L)) {
			try {
				connection = getConnection();
				String sql = "SELECT gate FROM `grupper`";
				cmd = connection.createStatement();
				data = cmd.executeQuery(sql);
				ArrayList<String> fromsql = new ArrayList<String>();
				while (data.next()) {
					fromsql.add(data.getString("gate"));
				}
				connection.close();

				gatewaycache = fromsql.toArray(new String[fromsql.size()]);

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
			connection = getConnection();
			String sql = "SELECT groupID FROM `devices` WHERE `device`= "+motiondevice+"';";
			cmd = connection.createStatement();
			data = cmd.executeQuery(sql);
			String groupid = data.getString("groupID");
			
			sql = "SELECT device FROM `devices` WHERE `groupID`= "+groupid+" AND type=relay';";
			cmd = connection.createStatement();
			data = cmd.executeQuery(sql);
			connection.close();
			return data.getString("device");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Data[] getLastTemp() throws SQLException, java.lang.ClassNotFoundException {
		Connection connectionget = getConnection();
		String gettemps = "SELECT t1.* FROM data t1 JOIN (SELECT device, MAX(timestamp) timestamp FROM data WHERE `topic` = 'temp' GROUP BY device) t2 ON t1.device = t2.device AND t1.timestamp = t2.timestamp WHERE `topic` = 'temp' ORDER BY `t1`.`value` DESC";
		try {
			cmd = connectionget.createStatement();
			data = cmd.executeQuery(gettemps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				int n = 0;
				ArrayList<Data> fromsql = new ArrayList<Data>();
				if (data.first()) {
					while (data.next()) {
						String gateway = data.getString("gateway");
						fromsql.add(new Data(gateway, data.getString("device"), null, data.getInt("value"), true, 500,
								200));
						n++;
					}
				}
				Data[] sqla = fromsql.toArray(new Data[fromsql.size()]);
				n = sqla.length;
				for (int i = 0; i < n; i++) {
					String sensor = sqla[i].getSensor();
					Statement cmd2 = connectionget.createStatement();
					ResultSet data2 = cmd2.executeQuery("SELECT * FROM `data` WHERE `device` LIKE '" + sensor
							+ "' ORDER BY `timestamp` ASC LIMIT 1");
					boolean state = true;
					if (data2.first()) {
						if (data2.getString("value").equals("false"))
							state = false;
						sqla[i].setState(state);
					}
					Statement cmd3 = connectionget.createStatement();
					System.out.println("SELECT * FROM `grupper` WHERE `sensor` LIKE '" + sensor + "' LIMIT 1");
					ResultSet data3 = cmd3
							.executeQuery("SELECT * FROM `grupper` WHERE `sensor` LIKE '" + sensor + "' LIMIT 1");
					if (data3.first()) {
						System.out.println("hej, sat til: " + data3.getString("power"));
						sqla[i].setRelay(data3.getString("power"));
					}
				}
				return sqla;

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		connectionget.close();
		return null;

	}

}