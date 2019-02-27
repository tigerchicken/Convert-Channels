package Send;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DButil {
	static String className = null;
	static String url = null;
	static String userName = null;
	static String passWord = null;
	
	public static Connection getconnection(String username) {
		className="dm.jdbc.driver.DmDriver";
		url="jdbc:dm://222.20.75.145:5236";
		passWord = "123456789";
		userName = username;
		Connection conn = null;
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.out.println("获取不到drive");
		}
		try {
			conn = DriverManager.getConnection(url,userName,passWord);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	public static void closeAll(Connection conn ,Statement stmt,ResultSet rs) {
		try {
			if (stmt != null) {

				stmt.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (conn != null) {
				conn.close();

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}