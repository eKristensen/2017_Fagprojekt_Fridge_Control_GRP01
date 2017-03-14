import java.sql.*;

public class mySQLtest
{
	private static String username = "javalogger";
	
	private static String password = "n5D1H0AAxVav0RXi";
	
	private static String connectionString = "jdbc:mysql://172.23.23.124:3306/log";
	private static Connection connection;
	private static Statement command;
	private static ResultSet data;
	
	public static void main(String[] args) {
		/*
		try {
			//connection = DriverManager.getConnection(connectionString, username, password);
			//command = connection.createStatement();
			//command.execute("INSERT INTO `data` (`ID`, `MAC`, `timestamp`, `type`, `value`) VALUES (NULL, '1122334455667788', '1488642591', 'power', '123');");
		//	Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	*/
	}
	
	public static void sendTomySQL(Long timestamp, String gateway, String device, String topic, String signal, String value) {
		try {
			timestamp = timestamp/1000;
			//System.out.println("INSERT INTO `data` (`ID`, `gateway`, `device`, `timestamp`, `topic`, `value`, `signaldb`) VALUES (NULL, '"+gateway+"', '"+device+"', '"+timestamp+"', '"+topic+"', '"+value+"', '"+signal+"');");
			
			connection = DriverManager.getConnection(connectionString, username, password);
			//connection = getConnection();
			command = connection.createStatement();
			command.execute("INSERT INTO `data` (`ID`, `gateway`, `device`, `timestamp`, `topic`, `value`, `signaldb`) VALUES (NULL, '"+gateway+"', '"+device+"', '"+timestamp+"', '"+topic+"', '"+value+"', '"+signal+"');");
			command.close();
		//	Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
}