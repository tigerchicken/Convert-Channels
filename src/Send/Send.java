package Send;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * ���ͷ�����
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
	 * �ַ���ת������
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
		System.out.println("���ͽ���");
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
				flag = false;  // ��� 1001 ����ɹ����� 1000 ����ʧ���� �˳�ѭ��
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
			System.out.println("ɾ��1001 1002ʧ��");
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
		System.out.println("��ʼ");
	}
	/*
	 * �ж��Ƿ��ܼ������� ,����ܹ��������У���˵�� ���û��Ѿ� ��ȡ��һλ�����Լ������͡�
	 */
	public static boolean isNext() {
		sql = "INSERT INTO LIULEI.ADVANCED VALUES(1000);INSERT INTO LIULEI.ADVANCED VALUES(1001);INSERT INTO LIULEI.ADVANCED VALUES(1002);COMMIT";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			return false; // �������ʧ�ܣ��򷵻�false
		} 
		return true; // ����ɹ��򷵻� true..
	}

	public static void end() {
		sql="DELETE FROM LIULEI.ADVANCED WHERE ID=1000 ;DELETE FROM LIULEI.ADVANCED WHERE ID=1001;COMMIT;";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) { 
			System.out.println("û�гɹ�ɾ��");
		}
	}
	/*
	 * ������Ϣ
	 */
	public static void sendMessage(String s) {
		System.out.println("��Ҫ���͵�λ��" + s);
		char[] arr = s.toCharArray();
		System.out.println("����׼�����");
		int i = 1;
		start();
		for (char b : arr) {
			while (!isNext()); // �жϽ��շ��Ƿ���Խ������ݣ�������ȴ�����
			switch (b) { // ����һλ����
			case '0':
				sql = "INSERT INTO LIULEI.LIULEI VALUES(44,3);COMMIT";
				try {
					stmt.executeUpdate(sql);
				} catch (SQLException e) {
				}
				System.out.println("���͵�" + i + "λ" + b);
				i++;
				break;

			default:
				sql = "DELETE FROM  LIULEI.LIULEI WHERE ID = 44;COMMIT";
				try {
					stmt.executeUpdate(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				} 
				System.out.println("���͵�" + i + "λ" + b);
				i++;
				break;
			} // switch ����������һλ���ݣ�������ɾ���߼��û��Ĳ��������..
			sync();   //�� ���շ� ͬ��  
		} // ������ �Ѿ�������ϡ�
		while (!isNext());
		endall();
		System.out.println("�������");
	}

	public static void init() {
		sql = "DELETE FROM LIULEI.ADVANCED WHERE ID>=1;COMMIT"; // �ָ����ݿ�
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		sql = "DELETE FROM LIULEI.LIULEI WHERE ID=44;COMMIT"; // �ָ����ݿ�
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public static void main(String[] args) {
		init();
        sendMessage(stringToBinary("�û���LIULEI ���룺123456789 !"));
		DButil.closeAll(conn, stmt, null);
	}

}
