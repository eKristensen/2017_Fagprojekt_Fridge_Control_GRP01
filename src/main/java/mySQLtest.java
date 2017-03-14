import java.sql.*;

public class mySQLtest
{
	private static String username = "javalogger";
	
	private static String password = "n5D1H0AAxVav0RXi";
	
	private static String connectionString = "jdbc:mysql://172.23.23.124:3306/log";
	private static Connection connection;
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
	
}