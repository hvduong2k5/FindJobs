package Util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	
	public static Connection MakeConnection ()
	{
		Connection conn = null;
		try {
			//ORCA url ="jdbc:mysql://127.0.0.1:3306/bai1?useSSL=false&serverTimezone=UTC"
			String url = "jdbc:mysql://127.0.0.1:3306/findjob?";
			String username = "root";
			String password = "orca123@Mysql";
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url,username,password);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return conn;
	}
	public static void CloseConnection(Connection conn)
	{
		try {
		if(conn != null)
		{
			conn.close();
		}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
		System.out.println("Test connection : ");
		Connection conn = MakeConnection();
		DatabaseMetaData test= (DatabaseMetaData) conn.getMetaData();
		System.out.print(" ..." +test.getDriverName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
