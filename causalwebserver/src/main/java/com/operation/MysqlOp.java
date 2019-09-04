package com.operation;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Set;
import java.io.File;
import java.io.FileOutputStream;
import java.io.*;
public class MysqlOp implements Operation
{
    private static Properties properties = new Properties();
    private static String publicIP1 = "publicIP1";
    private static String publicIP2 = "publicIP2";
    private static String publicIP3 = "publicIP3";
    private static String privateIP1 = "privateIP1";
    private static String privateIP2 = "privateIP2";
    private static String privateIP3 = "privateIP3";
    private static  String DRIVE = "com.mysql.cj.jdbc.Driver";
    private static  String DB_URL =//
            "jdbc:mysql:replication://39.104.96.96:3306,39.104.153.41:3306/test27?roundRobinLoadBalance=true&serverTimezone=CST";

    private static  String user = "mysql_user";
    private static  String password = "mysql_password";
    private static Connection conn1 = null;
    private static Connection conn2 = null;
    private static String database = "mysql_database";
	FileOutputStream outStream = null;
	 BufferedOutputStream buff = null;
    @Override
    public String init()throws IOException
    {
	properties.load(new FileInputStream("src/main/resources/conf.properties"));
     // String url = "jdbc:mysql:replication://"+properties.getProperty(privateIP2)+":3306,"+properties.getProperty(privateIP3)
      //                +":3306/"+properties.getProperty(database)+"?roundRobinLoadBalance=true&serverTimezone=CST";
     String url = "jdbc:mysql:replication://dcdbt-rybx6lec.sql.tencentcdb.com:32/causaltest?roundRobinLoadBalance=true&serverTimezone=CST";
	 String resp = ""; 
	 try{
            Class.forName(DRIVE);
            conn1 = DriverManager.getConnection(url,properties.getProperty(user),properties.getProperty(password));
            resp = "conn1=="+conn1;
			conn1.setReadOnly(false);
            conn2 = DriverManager.getConnection(url,properties.getProperty(user),properties.getProperty(password));
            conn2.setReadOnly(false);
		/////zhaole////
		//Statement stmt = conn1.createStatement();
		//stmt.executeQuery("show databases");
		//stmt.close();
        }catch(SQLException se)
        {
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return url;
    }
    public String createTable(String tableName)
    {
        String sql = "";
        if (tableName.equals("twitterusertable")) {
            sql = "CREATE TABLE IF NOT EXISTS twitterusertable (" +
                    "id VARCHAR(30),tweetid VARCHAR(30),liketweet VARCHAR(30)," +
                    "retweet VARCHAR(30),commenttweet VARCHAR(30),time VARCHAR(40)," +
                  //  "PRIMARY KEY(id)) ENGINE=NDBCLUSTER DEFAULT CHARSET=utf8";
                    "PRIMARY KEY(id))  CHARSET=utf8";
         //   System.out.println("SQL="+sql);
        } else if (tableName.equals("amazoncommenttable"))
        {
            sql = "CREATE TABLE IF NOT EXISTS amazoncommenttable (" +
                    "id VARCHAR(30),descri VARCHAR(30),user VARCHAR(30)," +
                    "ifuseful VARCHAR(30),belongsto VARCHAR(30),time VARCHAR(40)," +
                    "PRIMARY KEY(id)) ENGINE=NDBCLUSTER DEFAULT CHARSET=utf8";
        } else if (tableName.equals("flickrpicturetable"))
        {
            sql = "CREATE TABLE IF NOT EXISTS flickrpicturetable (" +
                    "id VARCHAR(30),content VARCHAR(30),permit VARCHAR(30)," +
                    "likeduser VARCHAR(30),album VARCHAR(30),location VARCHAR(30)," +
                    "comment VARCHAR(30),tag VARCHAR(30),time VARCHAR(40)," +
                    "PRIMARY KEY(id)) ENGINE=NDBCLUSTER DEFAULT CHARSET=utf8";
        } else if(tableName.equals("amazoncarttable"))
        {
            sql = "CREATE TABLE IF NOT EXISTS amazoncarttable (" +
                    "id VARCHAR(30),commodity VARCHAR(30)," +
                    "PRIMARY KEY(id)) ENGINE=NDBCLUSTER DEFAULT CHARSET=utf8";
        } else if(tableName.equals("amazoncommoditytable"))
        {
            sql = "CREATE TABLE IF NOT EXISTS amazoncarttable (" +
                    "id VARCHAR(30),price VARCHAR(30),descri VARCHAR(30)," +
                    "comment VARCHAR(30),question VARCHAR(30),time VARCHAR(40)," +
                    "PRIMARY KEY(id)) ENGINE=NDBCLUSTER DEFAULT CHARSET=utf8";
        }

        try
        {
            Statement stmt = conn1.createStatement();
            stmt.executeUpdate(sql);

        }catch (SQLException e){
            e.printStackTrace();
        }
        return "mysqlcreatetable";
    }

    @Override
    public  void cleanup()
    {
        try {
            if(conn1!=null)
            {
              conn1.close();
            }
            if(conn2!=null)
            {
              conn2.close();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public  String insert(String tableName,String id,String col,String val,String nul,int clientFlag)
    {

        Statement stmt = null;
        String sql ="";
        String r = "";
        try
        {
            sql = "insert into "+tableName+" (id,"+col+") values ('"+id+"','"+val+"') " +
                    "ON DUPLICATE KEY UPDATE "+col+" = '"+val+"'";
           // System.out.println("insert="+sql);
            if(clientFlag ==1 )
            {
              stmt = conn1.createStatement();
            }else if(clientFlag ==2)
            {
              stmt = conn2.createStatement();
            }
            stmt.executeUpdate(sql);
            stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "insert-mysql\n";
    }
    public static String insert2(String tableName,String id,String col,String val)
    {

        Statement stmt = null;
        String sql ="";
        String r = "";
        try
        {
            sql = "insert into "+tableName+" (id,"+col+") values ('"+id+"','"+val+"') " +
                    "ON DUPLICATE KEY UPDATE "+col+" = '"+val+"'";
           // System.out.println("insert="+sql);
            stmt = conn2.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "insert-mysql\n";
    }
    @Override
    public String read(String tableName, String key, Set<String> field,int clientFlag)
    {
		// FileOutputStream outStream = new FileOutpuStream(new File("mysqlReadException.log"));
        //BufferedOutputStream buff = new BufferedOutputStream(outStream);
		String r_err = "";
		//FileOutputStream outStream = null;
        //BufferedOutputStream buff = null;
        Statement stmt = null;
        String sql ="";
        String r = "";
        try
        {
          String col = "";
          for (String c : field)
          {
            col = c;
          }
            sql = "SELECT "+col+" FROM "+tableName+" where id = '"+key+"'";
          if(clientFlag == 1 )
          {
            stmt = conn1.createStatement();
          }else{
            stmt = conn2.createStatement();

          }

          //  System.out.println("read="+sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                r = rs.getString(col);
            }
            stmt.close();
        }catch (SQLException sqle){
            //e.printStackTrace();
			try{
			outStream = new FileOutputStream(new File("mysqlReadException.log"));
			buff = new BufferedOutputStream(outStream);
			StringWriter sw = new StringWriter();  
			PrintWriter pw = new PrintWriter(sw);  
			sqle.printStackTrace(pw);  
			//String msg=sw.toString();
			buff.write(sw.toString().getBytes());
			}catch(Exception ioe)
				{	System.out.println("mysqlop_read_exceptionLog_error");
					ioe.printStackTrace();}
			finally{
				try {
                buff.flush();
				buff.close();
                outStream.close();
            } catch (Exception closeioe) {
                System.out.println("mysqlop_read_exceptionLog_error");
				closeioe.printStackTrace();
            }
			}
			//if(sqle!=null)
			//r_err = sqle.printStackTrace();
            return "readerror";
        }
        return r;

    }
    public static String read2(String tableName,String key,String col)
    {
        Statement stmt = null;
        String sql ="";
        String r = "";
        try
        {
            sql = "SELECT "+col+" FROM "+tableName+" where id = '"+key+"'";
            stmt = conn2.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                r = rs.getString(col);
            }
            stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
            return "readerror";

        }
        return r;
    }

    @Override
    public  String delete(String tableName,String id,String nul)
    {
        Statement stmt = null;
        String sql ="";
        String r = "";
        try
        {
            sql = "delete  from "+tableName+" where id ="+id;
            stmt = conn1.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }


        return "mysqldelete\n";
    }
}
