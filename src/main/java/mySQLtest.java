import java.sql.*;
import java.util.ArrayList;

public class mySQLtest
{
	private static String username = "javalogger";
	
	private static String password = "n5D1H0AAxVav0RXi";
	
	private static String connectionString = "jdbc:mysql://172.23.23.124:3306/log";
	private static Connection connection;
	private static Statement cmd;
	private static Statement command;
	private static ResultSet data;
	
	private static int cachewhere = 0;
	private static int cachesize = 20;
	private static boolean sending = false;
	private static Fridgedata[] cache = new Fridgedata[cachesize];
	
	public static void main(String[] args) {

	}
	
	public static void sendTomySQL(Long timestamp, String gateway, String device, String topic, String signal, String value) {
		
		
		
		//add something to catch requets while sending to sql, while loop and a varriable saying its sending?
/*		
		while (sending) {
			System.out.println("Sending: "+sending);
		}
		*/ // broken, must be fixed to get all data
		if (cachewhere == cachesize) {
			CommitCache();
			sending = true;
		}
		
		int i = cachewhere;
		

		cache[i] = new Fridgedata(timestamp, gateway, device, topic, signal, value, false);
		
		/*
		System.out.println(i);
		System.out.println(cache[0].getTopic());
		
		cache[i].setCommited(false);
		cache[i].setTimestamp(timestamp);
		cache[i].setGateway(gateway);
		cache[i].setDevice(device);
		cache[i].setTopic(topic);
		cache[i].setSignal(signal);
		cache[i].setValue(value);
		*/
		cachewhere++;

	}
	
	public static void CommitCache() {
		try {
			connection = getConnection();
			String sql = "INSERT INTO `data` (`ID`, `gateway`, `device`, `timestamp`, `topic`, `value`, `signaldb`) VALUES ";
			
			//System.out.println("ID1: "+cache[0].getTopic());
			
			for(int i = 0; i < cachesize; i++) {
				//System.out.println("i is: " + i);
				sql = sql + "(NULL, '"+cache[i].getGateway()+"'";
				sql = sql + ", '"+cache[i].getDevice()+"'";
				sql = sql + ", '"+cache[i].getTimestamp()/1000+"'";
				sql = sql + ", '"+cache[i].getTopic()+"', '"+cache[i].getValue()+"', '"+cache[i].getSignal()+"')";
				if (i != cachesize -1) sql = sql + ",";
				cache[i].clear();
			}
			sql = sql + ";";
			//System.out.println(sql);
			command = connection.createStatement();
			command.execute(sql);
			cachewhere = 0;
			sending = false;
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
	}
	
	public static Connection getConnection() {
	    try {
	        Class.forName("com.mysql.jdbc.Driver");
	        Connection conn = DriverManager.getConnection(connectionString, username, password);
	        if (!conn.isClosed())
	            return conn;
	    } catch (ClassNotFoundException | SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public static Data[] getLastTemp() throws SQLException {
		Connection connectionget = getConnection();
		String gettemps = "SELECT t1.* FROM data t1 JOIN (SELECT device, MAX(timestamp) timestamp FROM data WHERE `topic` = 'temp' GROUP BY device) t2 ON t1.device = t2.device AND t1.timestamp = t2.timestamp WHERE `topic` = 'temp' ORDER BY `t1`.`value` DESC";
		try {
			cmd = connectionget.createStatement();
			data = cmd.executeQuery(gettemps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				int n = 0;
				ArrayList<Data> fromsql = new ArrayList<Data>();
				if (data.first()) {
					while(data.next()) {
						System.out.println("device: " + data.getString("device") + " value: " + data.getString("value"));
						String gateway = data.getString("gateway");
						fromsql.add(new Data(gateway,data.getString("device"),data.getInt("value"),true,0,0));
						n++;
					}
				}
				Data[] sqla = fromsql.toArray(new Data[fromsql.size()]);
				for (int i = 0; i < n; i++) {
					String device = sqla[i].getDevice();
					Statement cmd2 = connectionget.createStatement();
					ResultSet data2 = cmd2.executeQuery("SELECT * FROM `data` WHERE `device` LIKE '"+device+"' ORDER BY `timestamp` ASC LIMIT 1");
					boolean state = true;
					if (data2.getString("value").equals("false")) state = false;
					sqla[i].setON(state);
				}
				//SELECT * FROM `data` WHERE `device` LIKE '0015BC001A005664' ORDER BY `timestamp` ASC LIMIT 1
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		connectionget.close();
		return null;
		
	}
	
}