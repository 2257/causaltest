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

public class PostgresxlOp implements Operation
{

    private static Properties properties = new Properties();
    private static String publicIP1 = "publicIP1";
    private static String publicIP2 = "publicIP2";
    private static String publicIP3 = "publicIP3";
    private static String privateIP1 = "privateIP1";
    private static String privateIP2 = "privateIP2";
    private static String privateIP3 = "privateIP3";
    private static  String user = "postgres_user";
    private static  String password = "postgres_password";
    private static  String DB_URL =
            "jdbc:postgresql://39.104.96.96:5432,39.104.153.41:5432/zl";

    private static Connection conn1 = null;
    private static Connection conn2 = null;

    @Override
    public String init()throws IOException
    {
	properties.load(new FileInputStream("src/main/resources/conf.properties"));
      String url = "jdbc:postgresql://"+properties.getProperty(privateIP2)+":5432,"
                                          +properties.getProperty(privateIP3)+":5432/"
                                          +properties.getProperty(user);

      try{
            Class.forName("org.postgresql.Driver");
            conn1 = DriverManager.getConnection(url,properties.getProperty(user),properties.getProperty(password));
            conn2 = DriverManager.getConnection(url,properties.getProperty(user),properties.getProperty(password));
        }catch(SQLException se)
        {
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return "init postgres";
    }
    public  String createTable(String tableName)
    {

        String sql = "";
        if (tableName.equals("twitterusertable")) {
            sql = "CREATE TABLE IF NOT EXISTS twitterusertable (" +
                    "id VARCHAR(30),tweetid VARCHAR(30),liketweet VARCHAR(30)," +
                    "retweet VARCHAR(30),commenttweet VARCHAR(30),time VARCHAR(40)," +
                    "PRIMARY KEY(id)) DISTRIBUTE BY REPLICATION";

            //System.out.println("SQL="+sql);
        } else if (tableName.equals("amazoncommenttable"))
        {
            sql = "CREATE TABLE IF NOT EXISTS amazoncommenttable (" +
                    "id VARCHAR(30),descri VARCHAR(30),user VARCHAR(30)," +
                    "ifuseful VARCHAR(30),belongsto VARCHAR(30),time VARCHAR(40)," +
                    "PRIMARY KEY(id)) DISTRIBUTE BY REPLICATION";
        } else if (tableName.equals("flickrpicturetable"))
        {
            sql = "CREATE TABLE IF NOT EXISTS flickrpicturetable (" +
                    "id VARCHAR(30),content VARCHAR(30),permit VARCHAR(30)," +
                    "likeduser VARCHAR(30),album VARCHAR(30),location VARCHAR(30)," +
                    "comment VARCHAR(30),tag VARCHAR(30),time VARCHAR(40)," +
                    "PRIMARY KEY(id)) DISTRIBUTE BY REPLICATION";
        } else if(tableName.equals("amazoncarttable"))
        {
            sql = "CREATE TABLE IF NOT EXISTS amazoncarttable (" +
                    "id VARCHAR(30),commodity VARCHAR(30)," +
                    "PRIMARY KEY(id)) DISTRIBUTE BY REPLICATION";
        } else if(tableName.equals("amazoncommoditytable"))
        {
            sql = "CREATE TABLE IF NOT EXISTS amazoncommoditytable (" +
                    "id VARCHAR(30),price VARCHAR(30),descri VARCHAR(30)," +
                    "comment VARCHAR(30),question VARCHAR(30),time VARCHAR(40)," +
                    "PRIMARY KEY(id)) DISTRIBUTE BY REPLICATION";
        }

        try
        {

            Statement stmt = conn1.createStatement();
            stmt.executeUpdate(sql);

        }catch (SQLException e){
            e.printStackTrace();
        }
        return "pgsqlcreatetable";
    }

    @Override
    public void cleanup()
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
        }catch (SQLException e)
        {
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
            sql = "insert into "+tableName+" (id,"+col+") values ('"+id+"','"+val+"') on conflict (id) do update set "+col+" = '"+val+"'";
           // System.out.println("insert="+sql);
            if(clientFlag == 1)
            {
              stmt = conn1.createStatement();
            }else if (clientFlag == 2)
              {
              stmt = conn2.createStatement();
            }

            stmt.executeUpdate(sql);
            stmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("exception in Postgres insert:"+e.getMessage());

        }
        return "insert-pgsql\n";
    }
    public static String insert2(String tableName,String id,String col,String val)
    {

        Statement stmt = null;
        String sql ="";
        String r = "";
        try
        {
            sql = "insert into "+tableName+" (id,"+col+") values ('"+id+"','"+val+"') ";
            // System.out.println("insert="+sql);
            stmt = conn2.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("exception in Postgres insert:"+e.getMessage());

        }
        return "insert-pgsql\n";
    }

    @Override
    public  String read(String tableName, String key, Set<String> field, int clientFlag)
    {
        Statement stmt = null;
        String sql ="";
        String r = "";
        String col = "";
        for (String c : field)
        {
          col = c;
        }
        try
        {
            sql = "SELECT "+col+" FROM "+tableName+" where id = '"+key+"'";
            if(clientFlag == 1)
            {
              stmt = conn1.createStatement();
            }else if(clientFlag ==2)
            {
              stmt = conn2.createStatement();
            }

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                r = rs.getString(col);
            }
            stmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("exception in Postgres read:"+e.getMessage());

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
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("exception in Postgres read:"+e.getMessage());

            return "readerror";

        }
        return r;


    }

    @Override
    public String delete(String tableName,String id,String nul)
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


        return "pgsqldelete\n";
    }
}
