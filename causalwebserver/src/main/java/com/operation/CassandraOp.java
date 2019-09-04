package com.operation;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class CassandraOp implements Operation
{
  private Properties properties = new Properties();
  static Cluster cluster = null;
  static Session session = null;
  static Session session2 = null;
  private static String readConsistencyLevelValue = "cassandra_readconsistencylevel";
  private static String writeConsistencyLevelValue = "cassandra_writeconsistencylevel";
  private static String publicIP1 = "publicIP1";
  private static String publicIP2 = "publicIP2";
  private static String publicIP3 = "publicIP3";
  private static String privateIP1 = "privateIP1";
  private static String privateIP2 = "privateIP1";
  private static String privateIP3 = "privateIP1";
  private static String port1 = "port1";
  private static String port2 = "port2";
  private static String port3 = "port3";
  private static String keyspace = "cassandra_keyspace";
  private static final AtomicInteger INIT_COUNT = new AtomicInteger(0);

  @Override
  public  String init() throws IOException
  {
    //InputStream in = Properties.class.getClassLoader().getResourceAsStream("src/main/resources/conf.properties");
   // 	properties.load(new FileInputStream("../../resources/conf.properties"));
   properties.load(new FileInputStream("src/main/resources/conf.properties"));
	String ip1 = "";
    String ip2 = "";
    String ip3 = "";
    ip1 = properties.getProperty(privateIP1);
    ip2 = properties.getProperty(privateIP2);
    ip3 = properties.getProperty(privateIP3);
    AddressTranslate at = new AddressTranslate();
    ///at.translate(new InetSocketAddress("39.104.154.79", 9042));
   // System.out.println(ip1);
    String[] hosts = new String[]{ip1,ip2,ip3};
   // System.out.println(hosts);
    /// String[] hosts = new String[]{"172.24.83.25","172.24.83.27","172.24.83.26"};
    cluster = Cluster.builder()
        .addContactPoints(hosts)
        .withAddressTranslator(at)
        .withLoadBalancingPolicy(new RoundRobinPolicy())
        .withPort(9042)
        .build();

    session = cluster.connect(properties.getProperty(keyspace));
    session2 = cluster.connect(properties.getProperty(keyspace));
    return "cassandra init complete";
    //  }

  }
  @Override
  public void cleanup()
  {

    session.close();
    session2.close();
    cluster.close();
    cluster = null;
    session = null;
  }
  public static String createKeyspace(String keyspaceName,String strategy,String replication)
  {
    String cql = "CREATE KEYSPACE if not exists "+keyspaceName+" WITH replication = " +
        "{'class': '"+strategy+"', 'replication_factor': '"+replication+"'}";
    session.execute(cql);
    return "cassandra createKeyspace complete";
  }


  public String createTable(String tableName,Map<String, String> fields,Set<String> primarykey)
  {
    String field = "";
    for (Map.Entry<String, String> entry : fields.entrySet())
    {
      String name = entry.getKey();
      String type = entry.getValue();
      field += name +" "+type+",";
    }
    String primary = "";
    for(String p : primarykey)
    {
      primary += p+",";
    }
    if(primary.endsWith(","))
    {
      primary=primary.substring(0,primary.length()-1);
    }
    String cql = "CREATE TABLE  if not exists "+tableName+" ("+field+" PRIMARY KEY ("+primary+"));";

    // System.out.println("createtable cql="+cql);
    session.execute(cql);
    return "cassandra createTable complete";
  }
  //@WebMethod
  @Override
  public  String read(String table, String key, Set<String> fields,int clientFlag)throws IOException
  {
properties.load(new FileInputStream("src/main/resources/conf.properties"));
    long st = System.currentTimeMillis();
    HashMap<String, String> result = new HashMap<>();
    String re="";
    ConsistencyLevel readConsistencyLevel = ConsistencyLevel.ONE;
	readConsistencyLevel = ConsistencyLevel.valueOf(properties.getProperty(readConsistencyLevelValue));
    try
    {
      Statement stmt ;
      Select.Builder selectBuilder;
      if (fields == null)
      {
        selectBuilder = QueryBuilder.select().all();
      } else
      {
        selectBuilder = QueryBuilder.select();
        for (String col : fields)
        {
          ((Select.Selection) selectBuilder).column(col);
        }
      }
      table.replace("\"","");
      stmt = selectBuilder.from(table).where(QueryBuilder.eq("id", key)).limit(1);
      stmt.setConsistencyLevel(readConsistencyLevel);
      ResultSet rs =  session.execute(stmt);;
      if(clientFlag == 1)
      {
        rs = session.execute(stmt);
      }
      else if(clientFlag == 2)
      {
        rs = session2.execute(stmt);
      }
      Row row = rs.one();

      ColumnDefinitions cd = row.getColumnDefinitions();
      String r1="";
      for (String c : fields)
      {
        r1 = c;
      }
      for (ColumnDefinitions.Definition def : cd)
      {
        re += row.getString(r1);
      }
      long en = System.currentTimeMillis() - st;

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error reading key: " + key+","+e.getMessage());
      // cleanup();
      return "readerror";
    }
    return re;

  }
  public  String read2(String table, String key, Set<String> fields)
  {
    long st = System.currentTimeMillis();
    String re="";
    ConsistencyLevel readConsistencyLevel = ConsistencyLevel.ONE;
    readConsistencyLevel = ConsistencyLevel.valueOf(properties.getProperty(readConsistencyLevelValue));
    try
    {
      Statement stmt ;
      Select.Builder selectBuilder;
      if (fields == null)
      {
        selectBuilder = QueryBuilder.select().all();
      } else
      {
        selectBuilder = QueryBuilder.select();
        for (String col : fields)
        {
          ((Select.Selection) selectBuilder).column(col);
        }
      }
      table.replace("\"","");
      stmt = selectBuilder.from(table).where(QueryBuilder.eq("id", key)).limit(1);
      stmt.setConsistencyLevel(readConsistencyLevel);
      // System.out.println(stmt);
      ResultSet rs = session2.execute(stmt);
      Row row = rs.one();

      ColumnDefinitions cd = row.getColumnDefinitions();
      String r1="";
      for (String c : fields)
      {
        r1 = c;
      }
      for (ColumnDefinitions.Definition def : cd)
      {
        re += row.getString(r1);
      }

    } catch (Exception e) {

      e.printStackTrace();
      System.out.println("Error reading key: " + key+","+e.getMessage());
      //cleanup();
      return "readerror";
    }
    return re;

  }


  //@WebMethod
  @Override
  public String insert(String table, String KEYROW, String key, String col, String val,int clientFlag) throws IOException
  {
	properties.load(new FileInputStream("src/main/resources/conf.properties"));  
	  ConsistencyLevel writeConsistencyLevel = ConsistencyLevel.ONE;
   // System.out.println("zhaoleinsert:"+properties.getProperty(writeConsistencyLevelValue));

	writeConsistencyLevel = ConsistencyLevel.valueOf(properties.getProperty(writeConsistencyLevelValue));
    try{
      Thread.sleep(2);
    }catch (Exception e){
      e.printStackTrace();
    }
    long st = System.currentTimeMillis();
    try{
      Insert insertStmt = QueryBuilder.insertInto(table);
      insertStmt.value(KEYROW, key);
      insertStmt.value(col,val);
     // System.out.println("insert val = "+val);
      insertStmt.setConsistencyLevel(writeConsistencyLevel);
      if(clientFlag == 1)
      {
        session.execute(insertStmt);
      }
      else
      {
        session2.execute(insertStmt);
      }

    }catch (Exception e) {
      e.printStackTrace();
      System.out.println("error insert c*"+e.getMessage());
    }
    long en = System.currentTimeMillis() - st;
    return "cassandra insert complete";
  }
  public  String insert2(String table, String KEYROW, String key, String col, String val) throws IOException
  {
    ConsistencyLevel writeConsistencyLevel = ConsistencyLevel.ONE;
    writeConsistencyLevel = ConsistencyLevel.valueOf(properties.getProperty(writeConsistencyLevelValue));
    try{
      Thread.sleep(2);
    }catch (Exception e){
      e.printStackTrace();
    }
    long st = System.currentTimeMillis();
    try{
      Insert insertStmt = QueryBuilder.insertInto(table);
      // Add key
      insertStmt.value(KEYROW, key);
      // Add fields
      insertStmt.value(col,val);
     // System.out.println("insert val = "+val);
      insertStmt.setConsistencyLevel(writeConsistencyLevel);
      session.execute(insertStmt);
    }catch (Exception e) {
      e.printStackTrace();
      System.out.println("error insert c*"+e.getMessage());
    }
    long en = System.currentTimeMillis() - st;
    return "cassandra insert complete";
  }
  @Override
  public String delete(String tableName, String KEYROW,String key) throws IOException
  {
    long st = System.currentTimeMillis();
    try {
      Statement stmt;
      stmt = QueryBuilder.delete().from(tableName).where(QueryBuilder.eq(KEYROW, key));
     // stmt.setConsistencyLevel(writeConsistencyLevel);
      session.execute(stmt);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error deleting key: " + key);
    }
    long en = System.currentTimeMillis() - st;

    return "cassandra delete complete";
  }

}
