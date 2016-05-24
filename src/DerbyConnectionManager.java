import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DerbyConnectionManager {

	private static DerbyConnectionManager cm = null;
	private static Connection conn = null;

	public static DerbyConnectionManager getInstance() {
		if (cm == null) {
			cm = new DerbyConnectionManager();
			cm.connect();
		}
		return cm;
	}

	private void connect() {
		try {
			DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());

			conn = DriverManager.getConnection("jdbc:derby:db;create=true");

			if (conn != null) {
				System.out.println("Connected to database #1");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return conn;
	}

}
