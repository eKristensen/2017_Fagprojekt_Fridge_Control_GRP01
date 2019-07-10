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
    private static Map<String, Integer> gatewaycache = new HashMap<String, Integer>();
    private static long sendtime = System.currentTimeMillis() / 1000L;
    private static ArrayList<Fridgedata> sendcache = new ArrayList<Fridgedata>();
    private static long devicetime = 0;
    private static Map<String, Integer> devicecache = new HashMap<String, Integer>();

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
            GatewayList(); // Update list cache
            DeviceList();
            connection = getConnection();
            String sql = "INSERT INTO `data` (`ID`, `gateway`, `device`, `timestamp`, `topic`, `value`, `signaldb`) VALUES ";

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
            cmd = connection.createStatement();
            cmd.executeUpdate(sql);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Map<String, Integer> GatewayList() throws java.lang.ClassNotFoundException {
        if ((gatewaytime + 5 * 60) <= (System.currentTimeMillis() / 1000L)) {
            try {
                connection = getConnection();
                String sql = "SELECT ID,device FROM `gateways`";
                cmd = connection.createStatement();
                data = cmd.executeQuery(sql);
                gatewaycache = new HashMap<String, Integer>();
                while (data.next()) {
                    gatewaycache.put(data.getString("device"), data.getInt("ID"));
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

    public static Map<String, Integer> DeviceList() throws java.lang.ClassNotFoundException {
        if ((devicetime + 5 * 60) <= (System.currentTimeMillis() / 1000L)) {
            try {
                connection = getConnection();
                String sql = "SELECT ID,device FROM `devices`";
                cmd = connection.createStatement();
                data = cmd.executeQuery(sql);
                devicecache = new HashMap<String, Integer>();
                while (data.next()) {
                    devicecache.put(data.getString("device"), data.getInt("ID"));
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
            connection = getConnection();
            String sql = "SELECT MAX(t1.device) as relay FROM `devices` INNER JOIN devices as t1 ON t1.groupID=devices.groupID WHERE devices.device='0015BC001A005359' AND t1.type='relay' GROUP BY devices.groupID";
            cmd = connection.createStatement();
            data = cmd.executeQuery(sql);
            String device = "";
            while (data.next()) {
                device = data.getString("relay");
            }
            connection.close();
            return device;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, String> getDevices(String gateway) throws java.lang.ClassNotFoundException {
        try {
            GatewayList();
            connection = getConnection();
            String sql = "SELECT devices.type,devices.device FROM groups INNER JOIN devices ON groups.ID=devices.groupID WHERE gateway="
                    + gatewaycache.get(gateway) + ";";
            cmd = connection.createStatement();
            data = cmd.executeQuery(sql);
            Map<String, String> fromsql = new HashMap<String, String>();
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
        System.out.println("Started getLastTemp");
        long startTime = System.currentTimeMillis();
        try {
            DeviceList();
            connection = getConnection();
            // Hent liste over relæer i hver gruppe. Kun et relæ vælges såfremt der flere.
            String sql = "SELECT MAX(device) as relay,groupID FROM `devices` WHERE `type` LIKE 'relay' GROUP BY groupID";
            cmd = connection.createStatement();
            data = cmd.executeQuery(sql);
            Map<Integer, String> relaysfromID = new HashMap<Integer, String>();
            ;
            while (data.next()) {
                relaysfromID.put(data.getInt("groupID"), data.getString("relay"));
            }

            long time = System.currentTimeMillis() / 1000L - 5 * 60; // unix for 5 min siden
            // Hent grupper med motion inden for de sidste 3 sæt.
            /*
             * sql =
             * "SELECT devices.groupID FROM data t1 INNER JOIN (SELECT device, MAX(timestamp) timestamp FROM data WHERE `topic` = 3 AND value=1 AND timestamp > "
             * +time+" GROUP BY device) t2 ON t1.device = t2.device AND t1.timestamp = t2.timestamp INNER JOIN devices ON devices.ID=t1.device INNER JOIN groups ON devices.groupID=groups.ID WHERE groups.aktiv=1 AND topic=3 AND value=1 AND t1.timestamp > "
             * +time; cmd = connection.createStatement(); data = cmd.executeQuery(sql);
             */
            ArrayList<Integer> fromsql = new ArrayList<Integer>();
            /*
             * while (data.next()) { fromsql.add(data.getInt("groupID")); }
             */

            // Hent seneste temperatur. Er der flere temperature i et sæt tages gennemsnit af det. Enheder med motion
            // udelukkes. Enheder med aktiv=0 udelukkes.
            sql = "SELECT AVG(t1.value) as val,gateways.device AS gate,devices.groupID FROM data t1 ";
            sql += "INNER JOIN (SELECT device, MAX(timestamp) timestamp FROM data WHERE `topic` = 6 AND timestamp > "
                    + time + " GROUP BY device) t2 ";
            sql += "ON t1.device = t2.device AND t1.timestamp = t2.timestamp ";
            sql += "INNER JOIN devices ON devices.ID=t1.device INNER JOIN groups ";
            sql += "ON devices.groupID=groups.ID ";
            sql += "INNER JOIN gateways ";
            sql += "ON groups.gateway=gateways.ID ";
            sql += "WHERE groups.aktiv=1 AND topic=6 AND t1.timestamp > " + time;
            for (int i = 0; i < fromsql.size(); i++) {
                sql += " AND groups.ID != " + fromsql.get(i);
            }
            sql += " GROUP BY devices.groupID;";
            // System.out.println(sql);
            cmd = connection.createStatement();
            data = cmd.executeQuery(sql);
            ArrayList<Data> todata = new ArrayList<Data>();
            while (data.next()) {
                // System.out.println("gate: "+data.getString("gate")+" groupID: "+data.getString("groupID")+ " value:
                // "+data.getInt("val") + " getrelay til gruppe: "+ relaysfromID.get(data.getInt("groupID")) + " status
                // mangler");
                todata.add(
                        new Data(data.getString("gate"), relaysfromID.get(data.getInt("groupID")), data.getInt("val")));
            }
            connection.close();
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            Data[] todataarr = new Data[todata.size()];
            todataarr = todata.toArray(todataarr);
            System.out.println("Runtime getLastTemp: " + totalTime + " ms");
            return todataarr;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Runtime getLastTemp: " + totalTime + " ms");
        return null;

    }

}