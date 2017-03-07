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
		
		try {
			connection = DriverManager.getConnection(connectionString, username, password);
			command = connection.createStatement();
			command.execute("INSERT INTO `data` (`ID`, `MAC`, `timestamp`, `type`, `value`) VALUES (NULL, '1122334455667788', '1488642591', 'power', '123');");
		//	Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
}