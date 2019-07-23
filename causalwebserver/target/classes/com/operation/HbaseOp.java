package com.operation;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class HbaseOp implements Operation
{
    private static Connection connection;
    private static Connection connection2;
    private static Table t;
    private static HBaseAdmin admin;
    private static HBaseAdmin admin2;
    private static Properties properties = new Properties();

  private static String resource = "hbase_resource" ;
    private static String hostname1 = "hostname1";
    private static String hostname2 = "hostname2";
    private static String hostname3 = "hostname3";


  @Override
    public  String init() throws IOException
    {
properties.load(new FileInputStream("src/main/resources/conf.properties"));
      String quorum1 = properties.getProperty(hostname1);
        String quorum2 = properties.getProperty(hostname2);
        String quorum3 = properties.getProperty(hostname3);
        String quorum = quorum1+","+quorum2+","+quorum3;
      try
       {
           Configuration conf = HBaseConfiguration.create();
           conf.set("hbase.zookeeper.quorum", quorum);
           conf.set("hbase.zookeeper.property.clientPort", "2181");
           conf.set("hbase.master",properties.getProperty(hostname1)+":60000");
           conf.addResource(properties.getProperty(resource));
           connection = ConnectionFactory.createConnection(conf);
           connection2 = ConnectionFactory.createConnection(conf);
           admin = (HBaseAdmin) connection.getAdmin();
           admin2 = (HBaseAdmin) connection.getAdmin();
       }catch (IOException e ){
           e.printStackTrace();
       }
        return "hbase init complete";
    }
    public String createTable(String tableName,String CFName)throws IOException
    {
            if (!admin.isTableAvailable(TableName.valueOf(tableName)))
            {
                String[] columnFamilyArray = CFName.split(",");
                HColumnDescriptor[] hColumnDescriptor = new HColumnDescriptor[columnFamilyArray.length];
                for (int i = 0; i < hColumnDescriptor.length; i++)
                {
                    hColumnDescriptor[i] = new HColumnDescriptor(columnFamilyArray[i]);
                }
                HTableDescriptor familyDesc = new HTableDescriptor(TableName.valueOf(tableName));
                for (HColumnDescriptor columnDescriptor : hColumnDescriptor)
                {
                    familyDesc.addFamily(columnDescriptor);
                }
                admin.createTable(familyDesc);
            }
            return "hbase createtable complete";
    }
    @Override
    public  void cleanup()
    {
        try {
            t.close();
            connection.close();
            connection = null;
        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    @Override
    public String read (String tableName, String key, Set<String> fields,int clientFlag)throws IOException
    {
properties.load(new FileInputStream("src/main/resources/conf.properties"));
      String r=",,";
        try{
          if(clientFlag == 1)
          {
            t = connection.getTable(TableName.valueOf(tableName));
          }
          else {
            t = connection2.getTable(TableName.valueOf(tableName));

          }
            Get g = new Get(key.getBytes());
            Result result = t.get(g);
            String readcol="";
            Iterator it = fields.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                readcol = next.toString();
            }

            byte[] row = result.getRow();
            Cell[] listCells = result.rawCells();

            for (Cell cell : listCells)
            {
                if(new String(CellUtil.cloneFamily(cell)).equals(readcol))
                {
                    r = new String(CellUtil.cloneValue(cell));
                }
            }
        }catch (Exception e)
        {
				e.printStackTrace();
            return "readerror";
        }
        return r;
    }
    public static String read2(String tableName, String key, Set<String> fields) throws IOException
    {
        String r="";
        try{
            t = connection2.getTable(TableName.valueOf(tableName));
            Get g = new Get(key.getBytes());
            Result result = t.get(g);
            String readcol="";
            Iterator it = fields.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                readcol = next.toString();
            }
            byte[] row = result.getRow();
            Cell[] listCells = result.rawCells();

            for (Cell cell : listCells) {
                if(new String(CellUtil.cloneFamily(cell)).equals(readcol))
                {
                    r = new String(CellUtil.cloneValue(cell));
                }
            }
        }catch (Exception e)
        {
            return "readerror";
        }

        return r;
    }
    @Override
    public  String insert(String table, String KEYROW,String key, String col, String val,int clientFlag) throws IOException
    {
      if(clientFlag == 1)
      {
        t = connection.getTable(TableName.valueOf(table));
      }else{
        t = connection2.getTable(TableName.valueOf(table));
      }

        Put put = new Put(Bytes.toBytes(KEYROW));
        put.addColumn( Bytes.toBytes(col),null, Bytes.toBytes(val));
        t.put(put);
        t.close();
        return "hbase insert complete";
    }
    public static String insert2(String table, String KEYROW, String key, String colFamily, String col, String val) throws IOException
    {
        t = connection2.getTable(TableName.valueOf(table));
        Put put = new Put(Bytes.toBytes(KEYROW));
        put.addColumn( Bytes.toBytes(col),null, Bytes.toBytes(val));
        t.put(put);
        t.close();
        return "hbase insert complete";
    }
    @Override
    public  String delete(String tableName, String KEYROW,String key) throws IOException
    {
        t = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(key));
        t.delete(delete);
        return "hbase delet complete";
    }
    public  String clearTable(String table) throws IOException
    {

        TableName tableName = TableName.valueOf(table);
        admin.disableTable(tableName);
        admin.truncateTable(tableName, true);
        return "hbase clearTable complete";
    }



}
