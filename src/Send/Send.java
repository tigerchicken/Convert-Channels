package Send;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 发送方代码
 *
 */
public class Send {
	private static Connection conn;
	private static java.sql.Statement stmt = null;
	private static String sql;
	private static boolean flag;
	static {
		conn = DButil.getconnection("LIULEI");
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 字符串转二进制
	 */
	public static String stringToBinary(String s) {
		byte[] nn = null;
		String string = "";
		try {
			nn = s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (byte b : nn) {
			string += Integer.toBinaryString((Integer) (b & 0xFF) + 256).substring(1);
		}
		return string;
	}
	
	public static void endall() {
		System.out.println("发送结束");
		sql = "DELETE FROM LIULEI.ADVANCED WHERE ID=2;COMMIT;";
		try {
			stmt.executeUpdate(sql);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void sync() {
		end();
		flag = true;
		while(flag) {
			boolean flag1 = true;
			sql = "INSERT INTO LIULEI.ADVANCED VALUES(1000);COMMIT";
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				flag = false; 
			}
			sql = "INSERT INTO LIULEI.ADVANCED VALUES(1001);COMMIT;";
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				flag1 = true;  
//				e.printStackTrace();
			}
			if((flag1 == true) && (flag == false)) {
				flag = false;  // 如果 1001 插入成功并且 1000 插入失败则 退出循环
			}else{
				flag = true;
			}
			sql = "DELETE FROM LIULEI.ADVANCED WHERE ID =1000;DELETE FROM LIULEI.ADVANCED WHERE ID =1001;COMMIT;";
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) { 
//				e.printStackTrace();
			}
			
		}
		sql = "DELETE FROM LIULEI.ADVANCED WHERE ID=1001;DELETE FROM LIULEI.ADVANCED WHERE ID=1002;COMMIT;";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) { 
			System.out.println("删除1001 1002失败");
		}
	}
	
	public static void start() {
		flag = true;
		while(flag) {
			flag = false;
		sql = "INSERT INTO LIULEI.ADVANCED VALUES(2);COMMIT";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			flag =true;
		} 
		}
		System.out.println("开始");
	}
	/*
	 * 判断是否能继续发生 ,如果能够插入三行，则说明 低用户已经 读取了一位，可以继续发送。
	 */
	public static boolean isNext() {
		sql = "INSERT INTO LIULEI.ADVANCED VALUES(1000);INSERT INTO LIULEI.ADVANCED VALUES(1001);INSERT INTO LIULEI.ADVANCED VALUES(1002);COMMIT";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			return false; // 如果插入失败，则返回false
		} 
		return true; // 插入成功则返回 true..
	}

	public static void end() {
		sql="DELETE FROM LIULEI.ADVANCED WHERE ID=1000 ;DELETE FROM LIULEI.ADVANCED WHERE ID=1001;COMMIT;";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) { 
			System.out.println("没有成功删除");
		}
	}
	/*
	 * 发送信息
	 */
	public static void sendMessage(String s) {
		System.out.println("需要发送的位：" + s);
		char[] arr = s.toCharArray();
		System.out.println("数据准备完毕");
		int i = 1;
		start();
		for (char b : arr) {
			while (!isNext()); // 判断接收方是否可以接受数据，不能则等待。。
			switch (b) { // 发送一位数据
			case '0':
				sql = "INSERT INTO LIULEI.LIULEI VALUES(44,3);COMMIT";
				try {
					stmt.executeUpdate(sql);
				} catch (SQLException e) {
				}
				System.out.println("发送第" + i + "位" + b);
				i++;
				break;

			default:
				sql = "DELETE FROM  LIULEI.LIULEI WHERE ID = 44;COMMIT";
				try {
					stmt.executeUpdate(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				} 
				System.out.println("发送第" + i + "位" + b);
				i++;
				break;
			} // switch 结束，发送一位数据，接下来删除高级用户的插入的数据..
			sync();   //与 接收方 同步  
		} // 到这里 已经发送完毕。
		while (!isNext());
		endall();
		System.out.println("发送完毕");
	}

	public static void init() {
		sql = "DELETE FROM LIULEI.ADVANCED WHERE ID>=1;COMMIT"; // 恢复数据库
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		sql = "DELETE FROM LIULEI.LIULEI WHERE ID=44;COMMIT"; // 恢复数据库
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public static void main(String[] args) {
		init();
        sendMessage(stringToBinary("用户：LIULEI 密码：123456789 !"));
		DButil.closeAll(conn, stmt, null);
	}

}
