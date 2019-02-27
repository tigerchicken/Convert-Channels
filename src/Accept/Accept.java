package Accept;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;

import Send.*;

/**
 *   
 */
public class Accept {
	private static Connection conn;
	private static java.sql.Statement stmt = null;
	private static String sql;
	private static boolean flag;
	static {
		conn = DButil.getconnection("LUOYANG");
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 二进制转字符串
	 */
	public static String binaryToStirng(String s) {
		String[] binaryArr = new String[s.length() / 8];
		for (int i = 0; i < s.length(); i = i + 8) {
			binaryArr[i / 8] = s.substring(i, i + 8);
		}
		byte[] mm3 = new byte[binaryArr.length];
		for (int i = 0; i < binaryArr.length; i++) {
			String m = binaryArr[i];
			int nn4 = Integer.parseInt(m, 2);
			byte mm = (byte) (nn4);
			mm3[i] = mm;
		}
		return new String(mm3, Charset.forName("UTF-8"));
	}

	

	/*
	 * 判断是否结束
	 */
	public static boolean isEnd() {
		flag = true;
		sql = "DELETE FROM LIULEI.COMMON WHERE ID=2;COMMIT"; 
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			return false;  
		} 
		
		System.out.println("结束");
		return true;  // 如果是终点 ，则返回 true 
	}
	/*
	 * 判断是否 高级用户是否发送完毕
	 */
	public static boolean isNext() { 
		flag = false;
		
		sql = "DELETE FROM LIULEI.COMMON WHERE ID=1002;COMMIT"; 
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			flag = true;   //如果删除失败，则说明已经发送了一位。删除成功，则说明没有发送，应该在插入该行。
		} 
		
		if(flag == false) {
			sql = "INSERT INTO LIULEI.COMMON VALUES(1002);COMMIT"; 
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
//				e.printStackTrace();
			}
			return false;
		}
		
		
		sql = "DELETE FROM LIULEI.COMMON WHERE ID=1000;DELETE FROM LIULEI.COMMON WHERE ID=1001;COMMIT"; 
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) { //如果删除失败，则说明发送方正在发送中。
			//e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	/*
	 *  同步操作
	 */
	public static void sync() {
		sql = "INSERT INTO LIULEI.COMMON VALUES(1001);COMMIT";
		try {
			stmt.executeUpdate(sql);  //只插入一行 1001 
		} catch (SQLException e) {
		} 
		flag = true;
		while(flag) {           //与发送方进行同步 
			flag = false;
			sql = "DELETE FROM LIULEI.COMMON WHERE ID=1002;COMMIT";
			try {
				stmt.executeUpdate(sql);  //只插入一行 1001 
			} catch (SQLException e) {
				flag = true;
			} 
		}
		sql= "DELETE FROM LIULEI.COMMON WHERE ID = 1001;";
		try {
			stmt.executeUpdate(sql);
		}catch (Exception e) {
		}
		prepareAccept();
	}
	
	/*
	 *   准备接受 一位
	 */
	public static void prepareAccept() {  
		sql = "INSERT INTO LIULEI.COMMON VALUES(1000);INSERT INTO LIULEI.COMMON VALUES(1001);INSERT INTO LIULEI.COMMON VALUES(1002);COMMIT"; 
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("prepareAccept 失败");
//			e.printStackTrace();
		} 
	}

	
	/*
	 * 接收信息
	 */
	public static String acceptMessage() {
		String binarys = "";
		int rs;
		prepareAccept();
		int i=1;
		while(true){
			if (isNext()) {  //如果 为 true 说明发送方发送完毕

				try {
					sql = "INSERT INTO LIULEI.LIULEI VALUES(44,44);COMMIT";
					rs = stmt.executeUpdate(sql);
				} catch (SQLException e) {
					rs = 0;
				}
				
				if (rs == 1) { // 如果插入成功，则说明得到信息1。
					sql = "DELETE FROM LIULEI.LIULEI WHERE ID = 44;COMMIT";
					try {
						stmt.executeUpdate(sql);
					} catch (SQLException e) {
//						e.printStackTrace();
					}
				}
				System.out.println("接收到第"+i+"位" + rs);
				binarys = binarys + String.valueOf(rs); // 插入成功为 1 ，插入失败为0
				sync(); 
				i++;
			}
			if(isEnd()) {
				break;
			}
			
		};  // 如果没有结束则继续接受

		return binaryToStirng(binarys);
	}
	public static void init() {
		flag = false;
		while(!flag) {
			flag = true;
			sql = "DELETE FROM LIULEI.COMMON WHERE ID>=1;COMMIT"; // 恢复数据库
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				flag= false;
			} 
		}
		sql = "INSERT INTO  LIULEI.COMMON VALUES(2);COMMIT";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
	}

	public static void main(String[] args) throws SQLException {
		init();
		System.out.println(acceptMessage());
		DButil.closeAll(conn, stmt, null);
	}

}
