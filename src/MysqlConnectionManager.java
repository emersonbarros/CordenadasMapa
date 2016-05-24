import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConnectionManager {

	private static MysqlConnectionManager cm = null;
	private static Connection conn = null;

	public static MysqlConnectionManager getInstance() {
		if (cm == null) {
			cm = new MysqlConnectionManager();
			cm.connect();
		}
		return cm;
	}

	private void connect() {
		try {
			DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());

			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/coordenadas","root","");

			if (conn != null) {
				System.out.println("Connected to database #2");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return conn;
	}

}
