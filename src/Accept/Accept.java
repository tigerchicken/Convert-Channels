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
	 * ������ת�ַ���
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
	 * �ж��Ƿ����
	 */
	public static boolean isEnd() {
		flag = true;
		sql = "DELETE FROM LIULEI.COMMON WHERE ID=2;COMMIT"; 
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			return false;  
		} 
		
		System.out.println("����");
		return true;  // ������յ� ���򷵻� true 
	}
	/*
	 * �ж��Ƿ� �߼��û��Ƿ������
	 */
	public static boolean isNext() { 
		flag = false;
		
		sql = "DELETE FROM LIULEI.COMMON WHERE ID=1002;COMMIT"; 
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			flag = true;   //���ɾ��ʧ�ܣ���˵���Ѿ�������һλ��ɾ���ɹ�����˵��û�з��ͣ�Ӧ���ڲ�����С�
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
		} catch (SQLException e) { //���ɾ��ʧ�ܣ���˵�����ͷ����ڷ����С�
			//e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	/*
	 *  ͬ������
	 */
	public static void sync() {
		sql = "INSERT INTO LIULEI.COMMON VALUES(1001);COMMIT";
		try {
			stmt.executeUpdate(sql);  //ֻ����һ�� 1001 
		} catch (SQLException e) {
		} 
		flag = true;
		while(flag) {           //�뷢�ͷ�����ͬ�� 
			flag = false;
			sql = "DELETE FROM LIULEI.COMMON WHERE ID=1002;COMMIT";
			try {
				stmt.executeUpdate(sql);  //ֻ����һ�� 1001 
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
	 *   ׼������ һλ
	 */
	public static void prepareAccept() {  
		sql = "INSERT INTO LIULEI.COMMON VALUES(1000);INSERT INTO LIULEI.COMMON VALUES(1001);INSERT INTO LIULEI.COMMON VALUES(1002);COMMIT"; 
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("prepareAccept ʧ��");
//			e.printStackTrace();
		} 
	}

	
	/*
	 * ������Ϣ
	 */
	public static String acceptMessage() {
		String binarys = "";
		int rs;
		prepareAccept();
		int i=1;
		while(true){
			if (isNext()) {  //��� Ϊ true ˵�����ͷ��������

				try {
					sql = "INSERT INTO LIULEI.LIULEI VALUES(44,44);COMMIT";
					rs = stmt.executeUpdate(sql);
				} catch (SQLException e) {
					rs = 0;
				}
				
				if (rs == 1) { // �������ɹ�����˵���õ���Ϣ1��
					sql = "DELETE FROM LIULEI.LIULEI WHERE ID = 44;COMMIT";
					try {
						stmt.executeUpdate(sql);
					} catch (SQLException e) {
//						e.printStackTrace();
					}
				}
				System.out.println("���յ���"+i+"λ" + rs);
				binarys = binarys + String.valueOf(rs); // ����ɹ�Ϊ 1 ������ʧ��Ϊ0
				sync(); 
				i++;
			}
			if(isEnd()) {
				break;
			}
			
		};  // ���û�н������������

		return binaryToStirng(binarys);
	}
	public static void init() {
		flag = false;
		while(!flag) {
			flag = true;
			sql = "DELETE FROM LIULEI.COMMON WHERE ID>=1;COMMIT"; // �ָ����ݿ�
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
