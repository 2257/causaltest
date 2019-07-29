/**
 * Copyright (c) 2016-2017 YCSB contributors. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */

package com.yahoo.ycsb.workloads;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.RandomByteIterator;
import com.yahoo.ycsb.WorkloadException;
import com.yahoo.ycsb.generator.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.*;
import java.text.SimpleDateFormat;
import com.yahoo.ycsb.generator.UniformLongGenerator;

/**
 * Typical RESTFul services benchmarking scenario. Represents a set of client
 * calling REST operations like HTTP DELETE, GET, POST, PUT on a web service.
 * This scenario is completely different from CoreWorkload which is mainly
 * designed for databases benchmarking. However due to some reusable
 * functionality this class extends {@link CoreWorkload} and overrides necessary
 * methods like init, doTransaction etc.
 */
public class RestWorkload extends CoreWorkload {

  /**
   * The name of the property for the proportion of transactions that are
   * delete.
   */
  public static final String DELETE_PROPORTION_PROPERTY = "deleteproportion";

  /**
   * The default proportion of transactions that are delete.
   */
  public static final String DELETE_PROPORTION_PROPERTY_DEFAULT = "0.00";

  /**
   * The name of the property for the file that holds the field length size for insert operations.
   */
  public static final String FIELD_LENGTH_DISTRIBUTION_FILE_PROPERTY = "fieldlengthdistfile";

  /**
   * The default file name that holds the field length size for insert operations.
   */
  public static final String FIELD_LENGTH_DISTRIBUTION_FILE_PROPERTY_DEFAULT = "fieldLengthDistFile.txt";

  /**
   * In web services even though the CRUD operations follow the same request
   * distribution, they have different traces and distribution parameter
   * values. Hence configuring the parameters of these operations separately
   * makes the benchmark more flexible and capable of generating better
   * realistic workloads.
   */
  // Read related properties.
  private static final String READ_TRACE_FILE = "url.trace.read";
  private static final String READ_TRACE_FILE_DEFAULT = "readtrace.txt";
  private static final String READ_ZIPFIAN_CONSTANT = "readzipfconstant";
  private static final String READ_ZIPFIAN_CONSTANT_DEAFULT = "0.99";
  private static final String READ_RECORD_COUNT_PROPERTY = "readrecordcount";
  // Insert related properties.
  private static final String INSERT_TRACE_FILE = "url.trace.insert";
  private static final String INSERT_TRACE_FILE_DEFAULT = "inserttrace.txt";
  private static final String INSERT_ZIPFIAN_CONSTANT = "insertzipfconstant";
  private static final String INSERT_ZIPFIAN_CONSTANT_DEAFULT = "0.99";
  private static final String INSERT_SIZE_ZIPFIAN_CONSTANT = "insertsizezipfconstant";
  private static final String INSERT_SIZE_ZIPFIAN_CONSTANT_DEAFULT = "0.99";
  private static final String INSERT_RECORD_COUNT_PROPERTY = "insertrecordcount";
  // Delete related properties.
  private static final String DELETE_TRACE_FILE = "url.trace.delete";
  private static final String DELETE_TRACE_FILE_DEFAULT = "deletetrace.txt";
  private static final String DELETE_ZIPFIAN_CONSTANT = "deletezipfconstant";
  private static final String DELETE_ZIPFIAN_CONSTANT_DEAFULT = "0.99";
  private static final String DELETE_RECORD_COUNT_PROPERTY = "deleterecordcount";
  // Delete related properties.
  private static final String UPDATE_TRACE_FILE = "url.trace.update";
  private static final String UPDATE_TRACE_FILE_DEFAULT = "updatetrace.txt";
  private static final String UPDATE_ZIPFIAN_CONSTANT = "updatezipfconstant";
  private static final String UPDATE_ZIPFIAN_CONSTANT_DEAFULT = "0.99";
  private static final String UPDATE_RECORD_COUNT_PROPERTY = "updaterecordcount";

  private Map<Integer, String> readUrlMap;
  private Map<Integer, String> insertUrlMap;
  private Map<Integer, String> deleteUrlMap;
  private Map<Integer, String> updateUrlMap;
  private int readRecordCount;
  private int insertRecordCount;
  private int deleteRecordCount;
  private int updateRecordCount;
  private NumberGenerator readKeyChooser;
  private NumberGenerator insertKeyChooser;
  private NumberGenerator deleteKeyChooser;
  private NumberGenerator updateKeyChooser;
  private NumberGenerator fieldlengthgenerator;
  private DiscreteGenerator operationchooser;

   static int urlCount;
   static int urlIndex;
   static int opc;
   static int opcIndex;
   static int loop;
   static int violation;
   static int violation_sizeerror;
  public static final String DB_PROPERTY = "database";
    public static final String WEBSITE_PROPERTY = "website";
    public static final String CONSISTENCY_TYPE_PROPERTY = "consistency";

    static String db;
    static String website;
    static String consistency;
    static public List<String> request = new ArrayList<>();
    static String user = buildID(1);
    static String user2 = buildID(2);
    static String user3 = buildID(3);
    static String tweet ;//= buildID(1);
    static String tweet2 ;//= buildID(2);
    static String picture = buildID(1);
    static String picture2 = buildID(2);
    static String content;// = buildID(1);
    static String content2;// = buildID(2);
    static String commodity = buildID(1);
    static String commodity2 = buildID(2);
    static String comment = buildID(1);
    static String moment = buildID(1);
    static String moment2 = buildID(2);
    static String question = buildID(1);
    static String question2 = buildID(2);
    static String tag = buildID(1);
    static String tag2 = buildID(2);
    static String descri ;//= buildID(1);
    static String descri2;// = buildID(2);
    static String time;
    static List<String> result = new ArrayList<>();
	static List<Integer> vio = new ArrayList<>();
    int typid;
    static int loopStartIndex = 1;
    Calendar cal;
    Date date;




    public static String buildID(int i)
    {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String tmp = str.substring(0, 8) + str.substring(9, 13) +str.substring(14, 18);
        return "id"+i+"_"+tmp;
    }
  @Override
  public void init(Properties p) throws WorkloadException {
    urlCount = 0;
    urlIndex = 0;
    opcIndex = 0;
    loop = 0;
    violation = 0;
	violation_sizeerror = 0;
    opc = Integer.parseInt(p.getProperty("operationcount"));
    readRecordCount = Integer.parseInt(p.getProperty(READ_RECORD_COUNT_PROPERTY, String.valueOf(Integer.MAX_VALUE)));
    insertRecordCount = Integer
      .parseInt(p.getProperty(INSERT_RECORD_COUNT_PROPERTY, String.valueOf(Integer.MAX_VALUE)));
    deleteRecordCount = Integer
      .parseInt(p.getProperty(DELETE_RECORD_COUNT_PROPERTY, String.valueOf(Integer.MAX_VALUE)));
    updateRecordCount = Integer
      .parseInt(p.getProperty(UPDATE_RECORD_COUNT_PROPERTY, String.valueOf(Integer.MAX_VALUE)));
db = p.getProperty(DB_PROPERTY,"cassandra");
        website = p.getProperty(WEBSITE_PROPERTY,"twitter");
        consistency = p.getProperty(CONSISTENCY_TYPE_PROPERTY,"readyouwrite");

        String instanceName = db+website+consistency;
        try {
            Class instance = Class.forName("com.yahoo.ycsb.workloads.dbinstance."+instanceName);
            Object obj = instance.newInstance();
            Method[] methods = instance.getMethods();
           // System.out.println("getMethods获取的方法：");
            Field fieldRequest = instance.getField("request");
            Field fieldTypeid = instance.getDeclaredField("typid");
            Field fieldloopStartIndex = instance.getDeclaredField("loopStartIndex");
            Field fieldtweet = instance.getField("tweet");
            Field fielddescri = instance.getField("descri");
            Field fielddescri2 = instance.getField("descri2");
            Field fieldcontent = instance.getField("content");

            fieldtweet.setAccessible(true);
            fieldloopStartIndex.setAccessible(true);
            fieldTypeid.setAccessible(true);
            fieldRequest.setAccessible(true);
            typid = (int)fieldTypeid.get(obj);
            loopStartIndex = (int)fieldloopStartIndex.get(obj);
            Method m = instance.getMethod("combine");
            m.invoke(obj);
            request = (ArrayList<String>) fieldRequest.get(obj);
         //   System.out.println(field.get(obj));

            content = (String) fieldcontent.get(obj);
            descri = (String) fielddescri.get(obj);
            descri2 = (String) fielddescri2.get(obj);
            tweet = (String) fieldtweet.get(obj);


        }catch (Exception e){
            e.printStackTrace();
        }

/*

        if(db.equals("cassandra" ) && website.equals("twitter") && consistency.equals("readyourwrite"))
        {
            typid = 1;
            loopStartIndex = 2;
        //    System.out.println("fff");
            url_cas_twitter_readyouwrite();
      //      System.out.println("qaa");
        }else if(db.equals("cassandra" ) && website.equals("twitter") && consistency.equals("writefollowread"))
        {
            typid = 2;
            loopStartIndex = 2;
            url_cas_twitter_writefollowread();
        }
        else if(db.equals("cassandra" ) && website.equals("twitter") && consistency.equals("monotonicread"))
        {
            typid = 3;
            loopStartIndex = 2;
            url_cas_twitter_monotonicread();
        }
        else if(db.equals("cassandra" ) && website.equals("twitter") && consistency.equals("monotonicwrite"))
        {
            typid = 4;
            loopStartIndex = 2;
            url_cas_twitter_monotonicwrite();
        }
        else if(db.equals("cassandra" ) && website.equals("amazon") && consistency.equals("readyourwrite"))
        {
            typid = 5;
            loopStartIndex = 2;
            url_cas_amazon_readyourwrite();
        }
        else if(db.equals("cassandra" ) && website.equals("amazon") && consistency.equals("writefollowread"))
        {
            typid = 6;
            loopStartIndex = 2;
            url_cas_amazon_writefollowread();
        }
        else if(db.equals("cassandra" ) && website.equals("amazon") && consistency.equals("monotonicread"))
        {
            typid = 7;
            loopStartIndex = 2;
            url_cas_amazon_monotonicread();
        }
        else if(db.equals("cassandra" ) && website.equals("amazon") && consistency.equals("monotonicwrite"))
        {
            typid = 8;
            loopStartIndex = 2;
            url_cas_amazon_monotonicwrite();
        }
        else if(db.equals("cassandra" ) && website.equals("flickr") && consistency.equals("readyourwrite"))
        {
            typid = 9;
            loopStartIndex = 2;
            url_cas_flickr_readyourwrite();
        }
        else if(db.equals("cassandra" ) && website.equals("flickr") && consistency.equals("writefollowread"))
        {
            typid = 10;
            loopStartIndex = 2;
            url_cas_flickr_writefollowread();
        }
        else if(db.equals("cassandra" ) && website.equals("flickr") && consistency.equals("monotonicread"))
        {
            typid = 11;
            loopStartIndex = 2;
            url_cas_flickr_monotonicread();
        }
        else if(db.equals("cassandra" ) && website.equals("flickr") && consistency.equals("monotonicwrite"))
        {
            typid = 12;
            loopStartIndex = 2;
            url_cas_flickr_monotonicwrite();
        }
        
        else if(db.equals("hbase" ) && website.equals("twitter") && consistency.equals("readyourwrite"))
        {
            typid = 13;
            loopStartIndex = 2;
            url_hbase_twitter_readyouwrite();
        }
        else if(db.equals("hbase" ) && website.equals("twitter") && consistency.equals("writefollowread"))
        {
            typid = 14;
            loopStartIndex = 2;
            url_hbase_twitter_writefollowread();
        }
        else if(db.equals("hbase" ) && website.equals("twitter") && consistency.equals("monotonicread"))
        {
            typid = 15;
            loopStartIndex = 2;
            url_hbase_twitter_monotonicread();
        }
        else if(db.equals("hbase" ) && website.equals("twitter") && consistency.equals("monotonicwrite"))
        {
            typid = 16;
            loopStartIndex = 2;
            url_hbase_twitter_monotonicwrite();
        }
        else if(db.equals("hbase" ) && website.equals("amazon") && consistency.equals("readyourwrite"))
        {
            typid = 17;
            loopStartIndex = 2;
            url_hbase_amazon_readyourwrite();
        }
        else if(db.equals("hbase" ) && website.equals("amazon") && consistency.equals("writefollowread"))
        {
            typid = 18;
            loopStartIndex = 2;
            url_hbase_amazon_writefollowread();
        }
        else if(db.equals("hbase" ) && website.equals("amazon") && consistency.equals("monotonicread"))
        {
            typid = 19;
            loopStartIndex = 2;
            url_hbase_amazon_monotonicread();
        }
        else if(db.equals("hbase" ) && website.equals("amazon") && consistency.equals("monotonicwrite"))
        {
            typid = 20;
            loopStartIndex = 2;
            url_hbase_amazon_monotonicwrite();
        }
        else if(db.equals("hbase" ) && website.equals("flickr") && consistency.equals("readyourwrite"))
        {
            typid = 21;
            loopStartIndex = 2;
            url_hbase_flickr_readyourwrite();
        }
        else if(db.equals("hbase" ) && website.equals("flickr") && consistency.equals("writefollowread"))
        {
            typid = 22;
            loopStartIndex = 2;
            url_hbase_flickr_writefollowread();
        }
        else if(db.equals("hbase" ) && website.equals("flickr") && consistency.equals("monotonicread"))
        {
            typid = 23;
            loopStartIndex = 2;
            url_hbase_flickr_monotonicread();
        }
        else if(db.equals("hbase" ) && website.equals("flickr") && consistency.equals("monotonicwrite"))
        {
            typid = 24;
            loopStartIndex = 2;
            url_hbase_flickr_monotonicwrite();
        }else if(db.equals("redis" ) && website.equals("twitter") && consistency.equals("readyourwrite"))
        {
            typid = 25;
            loopStartIndex = 1;
            url_redis_twitter_readyouwrite();
        }
        else if(db.equals("redis" ) && website.equals("twitter") && consistency.equals("writefollowread"))
        {
            typid = 26;
            loopStartIndex = 1;
            url_redis_twitter_writefollowread();
        }
        else if(db.equals("redis" ) && website.equals("twitter") && consistency.equals("monotonicread"))
        {
            typid = 27;
            loopStartIndex = 1;
            url_redis_twitter_monotonicread();
        }
        else if(db.equals("redis" ) && website.equals("twitter") && consistency.equals("monotonicwrite"))
        {
            typid = 28;
            loopStartIndex = 1;
            url_redis_twitter_monotonicwrite();
        }
        else if(db.equals("redis" ) && website.equals("amazon") && consistency.equals("readyourwrite"))
        {
            typid = 29;
            loopStartIndex = 1;
            url_redis_amazon_readyourwrite();
        }
        else if(db.equals("redis" ) && website.equals("amazon") && consistency.equals("writefollowread"))
        {
            typid = 30;
            loopStartIndex = 1;
            url_redis_amazon_writefollowread();
        }
        else if(db.equals("redis" ) && website.equals("amazon") && consistency.equals("monotonicread"))
        {
            typid = 31;
            loopStartIndex = 1;
            url_redis_amazon_monotonicread();
        }
        else if(db.equals("redis" ) && website.equals("amazon") && consistency.equals("monotonicwrite"))
        {
            typid = 32;
            loopStartIndex = 1;
            url_redis_amazon_monotonicwrite();
        }
        else if(db.equals("redis" ) && website.equals("flickr") && consistency.equals("readyourwrite"))
        {
            typid = 33;
            loopStartIndex = 1;
            url_redis_flickr_readyourwrite();
        }
        else if(db.equals("redis" ) && website.equals("flickr") && consistency.equals("writefollowread"))
        {
            typid = 34;
            loopStartIndex = 1;
            url_redis_flickr_writefollowread();
        }
        else if(db.equals("redis" ) && website.equals("flickr") && consistency.equals("monotonicread"))
        {
            typid = 35;
            loopStartIndex = 1;
            url_redis_flickr_monotonicread();
        }
        else if(db.equals("redis" ) && website.equals("flickr") && consistency.equals("monotonicwrite"))
        {
            typid = 36;
            loopStartIndex = 1;
            url_redis_flickr_monotonicwrite();
        }
        else if(db.equals("mongodb" ) && website.equals("twitter") && consistency.equals("readyourwrite"))
        {
          typid = 37;
          loopStartIndex = 2;
          url_mongodb_twitter_readyourwrite();
        }
        else if(db.equals("mongodb" ) && website.equals("twitter") && consistency.equals("writefollowread"))
        {
          typid = 38;
          loopStartIndex = 2;
          url_mongodb_twitter_writefollowread();
        }
        else if(db.equals("mongodb" ) && website.equals("twitter") && consistency.equals("monotonicread"))
        {
          typid = 39;
          loopStartIndex = 3;
          url_mongodb_twitter_monotonicread();
        }
        else if(db.equals("mongodb" ) && website.equals("twitter") && consistency.equals("monotonicwrite"))
        {
          typid = 40;
          loopStartIndex = 3;
          url_mongodb_twitter_monotonicwrite();
        }
        else if(db.equals("mongodb" ) && website.equals("flickr") && consistency.equals("readyourwrite"))
        {
          typid = 41;
          loopStartIndex = 2;
          url_mongodb_flickr_readyourwrite();
        }
        else if(db.equals("mongodb" ) && website.equals("flickr") && consistency.equals("writefollowread"))
        {
          typid = 42;
          loopStartIndex = 2;
          url_mongodb_flickr_writefollowread();
        }
        else if(db.equals("mongodb" ) && website.equals("flickr") && consistency.equals("monotonicread"))
        {
          typid = 43;
          loopStartIndex = 3;
          url_mongodb_flickr_monotonicread();
        }
        else if(db.equals("mongodb" ) && website.equals("flickr") && consistency.equals("monotonicwrite"))
        {
          typid = 44;
          loopStartIndex = 3;
          url_mongodb_flickr_monotonicwrite();
        }
        else if(db.equals("mongodb" ) && website.equals("amazon") && consistency.equals("readyourwrite"))
        {
          typid = 45;
          loopStartIndex = 2;
          url_mongodb_amazon_readyourwrite();
        }
        else if(db.equals("mongodb" ) && website.equals("amazon") && consistency.equals("writefollowread"))
        {
          typid = 46;
          loopStartIndex = 2;
          url_mongodb_amazon_writefollowread();
        }
        else if(db.equals("mongodb" ) && website.equals("amazon") && consistency.equals("monotonicread"))
        {
          typid = 47;
          loopStartIndex = 2;
          url_mongodb_amazon_monotonicread();
        }
        else if(db.equals("mongodb" ) && website.equals("amazon") && consistency.equals("monotonicwrite"))
        {
          typid = 48;
          loopStartIndex = 2;
          url_mongodb_amazon_monotonicwrite();
        }
        else if(db.equals("mysql" ) && website.equals("twitter") && consistency.equals("readyourwrite"))
        {
          typid = 49;
          loopStartIndex = 2;
          //    System.out.println("fff");
          url_mysql_twitter_readyouwrite();
          //      System.out.println("qaa");
        }else if(db.equals("mysql" ) && website.equals("twitter") && consistency.equals("writefollowread"))
        {
          typid = 50;
          loopStartIndex = 2;
          url_mysql_twitter_writefollowread();
        }
        else if(db.equals("mysql" ) && website.equals("twitter") && consistency.equals("monotonicread"))
        {
          typid = 51;
          loopStartIndex = 2;
          url_mysql_twitter_monotonicread();
        }
        else if(db.equals("mysql" ) && website.equals("twitter") && consistency.equals("monotonicwrite"))
        {
          typid = 52;
          loopStartIndex = 2;
          url_mysql_twitter_monotonicwrite();
        }
        else if(db.equals("mysql" ) && website.equals("amazon") && consistency.equals("readyourwrite"))
        {
          typid = 53;
          loopStartIndex = 2;
          url_mysql_amazon_readyourwrite();
        }
        else if(db.equals("mysql" ) && website.equals("amazon") && consistency.equals("writefollowread"))
        {
          typid = 54;
          loopStartIndex = 2;
          url_mysql_amazon_writefollowread();
        }
        else if(db.equals("mysql" ) && website.equals("amazon") && consistency.equals("monotonicread"))
        {
          typid = 55;
          loopStartIndex = 2;
          url_mysql_amazon_monotonicread();
        }
        else if(db.equals("mysql" ) && website.equals("amazon") && consistency.equals("monotonicwrite"))
        {
          typid = 56;
          loopStartIndex = 2;
          url_mysql_amazon_monotonicwrite();
        }
        else if(db.equals("mysql" ) && website.equals("flickr") && consistency.equals("readyourwrite"))
        {
          typid = 57;
          loopStartIndex = 2;
          url_mysql_flickr_readyourwrite();
        }
        else if(db.equals("mysql" ) && website.equals("flickr") && consistency.equals("writefollowread"))
        {
          typid = 58;
          loopStartIndex = 2;
          url_mysql_flickr_writefollowread();
        }
        else if(db.equals("mysql" ) && website.equals("flickr") && consistency.equals("monotonicread"))
        {
          typid = 59;
          loopStartIndex = 2;
          url_mysql_flickr_monotonicread();
        }
        else if(db.equals("mysql" ) && website.equals("flickr") && consistency.equals("monotonicwrite"))
        {
          typid = 60;
          loopStartIndex = 2;
          url_mysql_flickr_monotonicwrite();
        }

        else if(db.equals("postgresxl" ) && website.equals("twitter") && consistency.equals("readyourwrite"))
        {
          typid = 61;
          loopStartIndex = 2;
          //    System.out.println("fff");
          url_postgres_twitter_readyouwrite();
          //      System.out.println("qaa");
        }else if(db.equals("postgresxl" ) && website.equals("twitter") && consistency.equals("writefollowread"))
        {
          typid = 62;
          loopStartIndex = 2;
          url_postgres_twitter_writefollowread();
        }
        else if(db.equals("postgresxl" ) && website.equals("twitter") && consistency.equals("monotonicread"))
        {
          typid = 63;
          loopStartIndex = 2;
          url_postgres_twitter_monotonicread();
        }
        else if(db.equals("postgresxl" ) && website.equals("twitter") && consistency.equals("monotonicwrite"))
        {
          typid = 64;
          loopStartIndex = 2;
          url_postgres_twitter_monotonicwrite();
        }
        else if(db.equals("postgresxl" ) && website.equals("amazon") && consistency.equals("readyourwrite"))
        {
          typid = 65;
          loopStartIndex = 2;
          url_postgres_amazon_readyourwrite();
        }
        else if(db.equals("postgresxl" ) && website.equals("amazon") && consistency.equals("writefollowread"))
        {
          typid = 66;
          loopStartIndex = 2;
          url_postgres_amazon_writefollowread();
        }
        else if(db.equals("postgresxl" ) && website.equals("amazon") && consistency.equals("monotonicread"))
        {
          typid = 67;
          loopStartIndex = 2;
          url_postgres_amazon_monotonicread();
        }
        else if(db.equals("postgresxl" ) && website.equals("amazon") && consistency.equals("monotonicwrite"))
        {
          typid = 68;
          loopStartIndex = 2;
          url_postgres_amazon_monotonicwrite();
        }
        else if(db.equals("postgresxl" ) && website.equals("flickr") && consistency.equals("readyourwrite"))
        {
          typid = 69;
          loopStartIndex = 2;
          url_postgres_flickr_readyourwrite();
        }
        else if(db.equals("postgresxl" ) && website.equals("flickr") && consistency.equals("writefollowread"))
        {
          typid = 70;
          loopStartIndex = 2;
          url_postgres_flickr_writefollowread();
        }
        else if(db.equals("postgresxl" ) && website.equals("flickr") && consistency.equals("monotonicread"))
        {
          typid = 71;
          loopStartIndex = 2;
          url_postgres_flickr_monotonicread();
        }
        else if(db.equals("postgresxl" ) && website.equals("flickr") && consistency.equals("monotonicwrite"))
        {
          typid = 72;
          loopStartIndex = 2;
          url_postgres_flickr_monotonicwrite();
        }
        */
    readUrlMap = getTrace(p.getProperty(READ_TRACE_FILE, READ_TRACE_FILE_DEFAULT), readRecordCount);
    insertUrlMap = new LinkedHashMap<Integer, String>();
    //getTrace(p.getProperty(INSERT_TRACE_FILE, INSERT_TRACE_FILE_DEFAULT), insertRecordCount);
    deleteUrlMap = new LinkedHashMap<Integer, String>();
    //getTrace(p.getProperty(DELETE_TRACE_FILE, DELETE_TRACE_FILE_DEFAULT), deleteRecordCount);
    updateUrlMap = new LinkedHashMap<Integer, String>();
    //getTrace(p.getProperty(UPDATE_TRACE_FILE, UPDATE_TRACE_FILE_DEFAULT), updateRecordCount);

    operationchooser = createOperationGenerator(p);

    // Common distribution for all operations.
    String requestDistrib = p.getProperty(REQUEST_DISTRIBUTION_PROPERTY, REQUEST_DISTRIBUTION_PROPERTY_DEFAULT);

    double readZipfconstant = Double.parseDouble(p.getProperty(READ_ZIPFIAN_CONSTANT, READ_ZIPFIAN_CONSTANT_DEAFULT));
    readKeyChooser = getKeyChooser(requestDistrib, readUrlMap.size(), readZipfconstant, p);
    double updateZipfconstant = Double
        .parseDouble(p.getProperty(UPDATE_ZIPFIAN_CONSTANT, UPDATE_ZIPFIAN_CONSTANT_DEAFULT));
    updateKeyChooser = getKeyChooser(requestDistrib, updateUrlMap.size(), updateZipfconstant, p);
    double insertZipfconstant = Double
        .parseDouble(p.getProperty(INSERT_ZIPFIAN_CONSTANT, INSERT_ZIPFIAN_CONSTANT_DEAFULT));
    insertKeyChooser = getKeyChooser(requestDistrib, insertUrlMap.size(), insertZipfconstant, p);
    double deleteZipfconstant = Double
        .parseDouble(p.getProperty(DELETE_ZIPFIAN_CONSTANT, DELETE_ZIPFIAN_CONSTANT_DEAFULT));
    deleteKeyChooser = getKeyChooser(requestDistrib, deleteUrlMap.size(), deleteZipfconstant, p);

    fieldlengthgenerator = getFieldLengthGenerator(p);

  
  

  }

  public static DiscreteGenerator createOperationGenerator(final Properties p) {
    // Re-using CoreWorkload method.
    final DiscreteGenerator operationChooser = CoreWorkload.createOperationGenerator(p);
    // Needs special handling for delete operations not supported in CoreWorkload.
    double deleteproportion = Double
        .parseDouble(p.getProperty(DELETE_PROPORTION_PROPERTY, DELETE_PROPORTION_PROPERTY_DEFAULT));
    if (deleteproportion > 0) {
      operationChooser.addValue(deleteproportion, "DELETE");
    }
    return operationChooser;
  }

  private static NumberGenerator getKeyChooser(String requestDistrib, int recordCount, double zipfContant,
                                               Properties p) throws WorkloadException {
    NumberGenerator keychooser;

    switch (requestDistrib) {
    case "exponential":
      double percentile = Double.parseDouble(p.getProperty(ExponentialGenerator.EXPONENTIAL_PERCENTILE_PROPERTY,
          ExponentialGenerator.EXPONENTIAL_PERCENTILE_DEFAULT));
      double frac = Double.parseDouble(p.getProperty(ExponentialGenerator.EXPONENTIAL_FRAC_PROPERTY,
          ExponentialGenerator.EXPONENTIAL_FRAC_DEFAULT));
      keychooser = new ExponentialGenerator(percentile, recordCount * frac);
      break;
    case "uniform":
      keychooser = new UniformLongGenerator(0, recordCount - 1);
      break;
    case "zipfian":
      keychooser = new ZipfianGenerator(recordCount, zipfContant);
      break;
    case "latest":
      throw new WorkloadException("Latest request distribution is not supported for RestWorkload.");
    case "hotspot":
      double hotsetfraction = Double.parseDouble(p.getProperty(HOTSPOT_DATA_FRACTION, HOTSPOT_DATA_FRACTION_DEFAULT));
      double hotopnfraction = Double.parseDouble(p.getProperty(HOTSPOT_OPN_FRACTION, HOTSPOT_OPN_FRACTION_DEFAULT));
      keychooser = new HotspotIntegerGenerator(0, recordCount - 1, hotsetfraction, hotopnfraction);
      break;
    default:
      throw new WorkloadException("Unknown request distribution \"" + requestDistrib + "\"");
    }
    return keychooser;
  }

  protected static NumberGenerator getFieldLengthGenerator(Properties p) throws WorkloadException {
    // Re-using CoreWorkload method. 
    NumberGenerator fieldLengthGenerator = CoreWorkload.getFieldLengthGenerator(p);
    String fieldlengthdistribution = p.getProperty(FIELD_LENGTH_DISTRIBUTION_PROPERTY,
    FIELD_LENGTH_DISTRIBUTION_PROPERTY_DEFAULT);
    // Needs special handling for Zipfian distribution for variable Zipf Constant.
    if (fieldlengthdistribution.compareTo("zipfian") == 0) {
      int fieldlength = Integer.parseInt(p.getProperty(FIELD_LENGTH_PROPERTY, FIELD_LENGTH_PROPERTY_DEFAULT));
      double insertsizezipfconstant = Double
          .parseDouble(p.getProperty(INSERT_SIZE_ZIPFIAN_CONSTANT, INSERT_SIZE_ZIPFIAN_CONSTANT_DEAFULT));
      fieldLengthGenerator = new ZipfianGenerator(1, fieldlength, insertsizezipfconstant);
    }
    return fieldLengthGenerator;
  }

  /**
   * Reads the trace file and returns a URL map.
   */
  private static Map<Integer, String> getTrace(String filePath, int recordCount)
    throws WorkloadException {
    Map<Integer, String> urlMap = new HashMap<Integer, String>();
    /*int count = 0;
    String line;
    try {
      FileReader inputFile = new FileReader(filePath);
      BufferedReader bufferReader = new BufferedReader(inputFile);
      while ((line = bufferReader.readLine()) != null) {
        urlMap.put(count++, line.trim());
        if (count >= recordCount) {
          break;
        }
      }
      urlCount = count;
      bufferReader.close();
    } catch (IOException e) {
      throw new WorkloadException(
        "Error while reading the trace. Please make sure the trace file path is correct. "
          + e.getLocalizedMessage());
    }*/
    urlCount = request.size();
    //System.out.println("zzz  "+urlCount);
    return urlMap;
  }

  /**
   * Not required for Rest Clients as data population is service specific.
   */
  @Override
  public boolean doInsert(DB d, Object threadstate) {
    return false;
  }

  @Override
  public boolean doTransaction(DB d, Object threadstate) {
    String operation = operationchooser.nextString();
    if (operation == null) {
      return false;
    }

    switch (operation) {
    case "UPDATE":
      doTransactionUpdate(d);
      break;
    case "INSERT":
      doTransactionInsert(d);
      break;
    case "DELETE":
      doTransactionDelete(d);
      break;
    default:
      doTransactionRead(d);
    }
    return true;
  }

  private long exptime()
  {
    double x,z;
    double lambda=5;
    z = Math.random();
    x = -(1 / lambda) * Math.log(z);
    return (long)(x*1000);
  }

  /**
   * Returns next URL to be called.
   */
  private String getNextURL(int opType)
  {

      ViolationChecker checker = new ViolationChecker(descri,descri2,content,tweet);
    //String r = readUrlMap.get(urlIndex++);
        String r="";
    if( request!=null && request.size()>0)
    {
      r = request.get(urlIndex++);

    }
    opcIndex ++;
    if(opcIndex < opc)
    {
      if(urlIndex == urlCount)
      {
        loop++;
        urlIndex = loopStartIndex;
      //System.out.println(urlIndex+"____"+result);
        int tmp=violation;
       // check(result,typid);
          checker.check(result,typid,loop,urlIndex);
        if(tmp!=violation)
          vio.add(loop);
        tmp=0;
        result.clear();
        try{
        Thread.sleep(1);}catch (Exception e){}
        System.out.println("============================================================================"+loop+"\n");
//System.out.println("xxx"+request.size());
        r=request.get(urlIndex++);

      }
      else
      {
        try
        {
          if(r.indexOf("&type=insert&")==1 && request.get(urlIndex).indexOf("&type=read&")==1)
          {
            Thread.sleep(exptime());
          }
          else if(r.indexOf("&type=insert&")==1 && request.get(urlIndex).indexOf("&type=insert&")==1)
          {
            Thread.sleep(exptime());
          }
          else if(r.indexOf("&type=read&")==1 && request.get(urlIndex).indexOf("&type=read&")==1)
          {
            Thread.sleep(exptime());
          }
           else if(r.indexOf("&type=read&")==1 && request.get(urlIndex).indexOf("&type=insert&")==1)
          {
            Thread.sleep(exptime());
          }
         /////////////////////////////////////////////////////////////////////////////////////////
          else if(r.indexOf("&type=insert2&")==1 && request.get(urlIndex).indexOf("&type=read2&")==1)
          {
            Thread.sleep(exptime());
          }
          else if(r.indexOf("&type=read2&")==1 && request.get(urlIndex).indexOf("&type=read2&")==1)
          {
            Thread.sleep(exptime());
          }

          else if(r.indexOf("&type=read2&")==1 && request.get(urlIndex).indexOf("&type=insert2&")==1)
          {
            Thread.sleep(exptime());
          }
           else if(r.indexOf("&type=insert2&")==1 && request.get(urlIndex).indexOf("&type=insert2&")==1)
          {
            Thread.sleep(exptime());
          }
           else if(r.indexOf("&type=insert&")==1 && request.get(urlIndex).indexOf("&type=update&")==1)
          {
            Thread.sleep(exptime());
          }
        }catch (Exception e){}

      }
    }else if(opcIndex == opc)
    {
      //r = readUrlMap.get(urlCount-1);
      
      r = request.get(urlCount-1);
    }
    //System.out.println("urlIndex="+urlIndex+",urlCount="+urlCount);

    return r;
   /* if (opType == 1) {
      return readUrlMap.get(readKeyChooser.nextValue().intValue());
    } else if (opType == 2) {
      return insertUrlMap.get(insertKeyChooser.nextValue().intValue());
    } else if (opType == 3) {
      return deleteUrlMap.get(deleteKeyChooser.nextValue().intValue());
    } else {
      return updateUrlMap.get(updateKeyChooser.nextValue().intValue());
    }*/
  }

  @Override
  public void doTransactionRead(DB d) {
    HashMap<String, ByteIterator> result1 = new HashMap<String, ByteIterator>();
    Set<String> nu = new HashSet();
   // Map<String, ByteIterator> result = new HashMap<String, ByteIterator>();
    //result.addAll(d.read2(getNextURL(1), result));
   // d.read(null,getNextURL(1),null,result1);
  //d.read2(getNextURL(1),result);
  // System.out.println(d.getClass().getName()+"------"+d.getClass().getSuperclass());
    String url = getNextURL(1);
//System.out.println("url=="+url);
    if(url.startsWith("?type=read"))
    {
      System.out.println("[result]="+d.read2(url,result));
    }
    else
   {
//	System.out.println("http");
      d.read2(url,result);
    } 
    //check(result,typeid);
    if(opcIndex == opc)
    {
      System.out.println("============================================================================"+(++loop)+"\n");
      	System.out.println("[violation] num:"+violation+" ,proportion:"+(float)violation/(float)loop);
	System.out.println("[readerror] num:"+violation_sizeerror+" ,proportion:"+(float)violation_sizeerror/(float)loop);
	System.out.println("[total    ] num:"+(violation+violation_sizeerror)+" ,proportion:"+(float)(violation+violation_sizeerror)/(float)loop);
	System.out.println(vio);
      System.out.println("\n\n\n");
    }
  }

	public void printViolationInfo(int i,int num,int loop){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(i==0){
			//violation_size error
			System.out.println("[violate_size] num:"+num+",proportion:"+(float)num/loop+"---"+df.format(System.currentTimeMillis()));
		}else{
			System.out.println("[violate] num:"+num+",proportion:"+(float)num/loop+"---"+df.format(System.currentTimeMillis()));

		}
	}

  @Override
  public void doTransactionInsert(DB db) {
    HashMap<String, ByteIterator> value = new HashMap<String, ByteIterator>();
    // Create random bytes of insert data with a specific size.
    value.put("data", new RandomByteIterator(fieldlengthgenerator.nextValue().longValue()));
    db.insert(null, getNextURL(2), value);
  }

  public void doTransactionDelete(DB db) {
    db.delete(null, getNextURL(3));
  }

  @Override
  public void doTransactionUpdate(DB db) {
    HashMap<String, ByteIterator> value = new HashMap<String, ByteIterator>();
    // Create random bytes of update data with a specific size.
    value.put("data", new RandomByteIterator(fieldlengthgenerator.nextValue().longValue()));
    db.update(null, getNextURL(4), value);
  }
    public void check(List<String> result,int typid)
    {
    switch (typid)
    {
      case 1:case 13:case 25: case 37: case 49:case 61:
      {
	      String s ="";
        try
        {
            if(result.size()<1)
            {
              violation_sizeerror++;
             // System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
			 printViolationInfo(0,violation_sizeerror,loop); 
			  urlIndex = loopStartIndex;
            }
            else
            {
                s = result.get(0);
                if((s.indexOf("default") !=-1)||(s.indexOf("error") !=-1))
                {
                    violation_sizeerror++;
                    //System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
                    printViolationInfo(0,violation_sizeerror,loop);

					urlIndex = loopStartIndex;
                }
                else if (!s.equals(tweet)) {
                  violation++;
                 // System.out.println("[violate] num:" + violation + ", proportion:" + (float) violation / loop);
                 printViolationInfo(1,violation,loop);
				 urlIndex = loopStartIndex;
                }
            }
        }catch(Exception e)
        {
		      System.out.println("arraylist null exception "+ result);
	      }
        break;
      }
      case 33: case 17:
      {
        String s ="";
        if(result.size()<1)
        {
            violation_sizeerror++;
            //System.out.println("[violate_size] num:"+violation_sizeerror+",proportion:"+(float)violation_sizeerror/loop);
            printViolationInfo(0,violation_sizeerror,loop);

			urlIndex=loopStartIndex;
        }
        else
        {
            s=result.get(0);
            if((s.indexOf("default") !=-1)||(s.indexOf("error") !=-1))
            {
                violation_sizeerror++;
               // System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
               printViolationInfo(0,violation_sizeerror,loop);

			   urlIndex = loopStartIndex;
            }
            else if(!s.equals(descri))
            {
              violation++;
              //System.out.println("[violate] num:"+violation+",proportion:"+(float)violation/loop);
              printViolationInfo(1,violation,loop);
			  urlIndex=loopStartIndex;
            }
        }

        break;
      }
      case 2:case 26:/*case 30:*/case 34: case 38: case 50://case 62:
      {
          try
          {
              if(result.size()<2)
              {
                  violation_sizeerror ++;
                 // System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
                 printViolationInfo(0,violation_sizeerror,loop);

				 urlIndex = loopStartIndex;
              }
              else
              {

                  String s1 = result.get(0);
                  String s2 = result.get(1);
                  if((s1.indexOf("default") !=-1)||(s1.indexOf("error") !=-1) ||
                      (s2.indexOf("default") !=-1)||(s2.indexOf("error") !=-1))
                  {
                      violation_sizeerror++;
                     // System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
                      printViolationInfo(0,violation_sizeerror,loop);

					  urlIndex = loopStartIndex;
                  }
                  else if((s2.equals(s1)))
                  {
                    violation ++;
                    //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                    printViolationInfo(1,violation,loop);
					urlIndex = loopStartIndex;
                  }

              }
          }catch (Exception e){e.printStackTrace();}
          break;

      }
     /* case 26:case 30:case 34: case 38: case 50:case 62://read the [write],but did not read the [read] which this [write] dependent
      {
        try
        {
            if(result.size()<2)
            {
                violation_sizeerror ++;
                System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
                urlIndex = loopStartIndex;
            }
            else
            {

              String s1 = result.get(0);
              String s2 = result.get(1);
              if((s1.indexOf("default") !=-1)||(s1.indexOf("error") !=-1) ||
                  (s2.indexOf("default") !=-1)||(s2.indexOf("error") !=-1))
              {
                  violation_sizeerror++;
                  System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
                  urlIndex = loopStartIndex;
              }
              else if((s2.equals(tweet) && !s1.equals(tweet)))
              {
                  violation ++;
                  System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                  urlIndex = loopStartIndex;
              }

            }
        }catch (Exception e){e.printStackTrace();}
        break;
      }*/
      case 3:case 7:case 11:case 15:case 19:case 23:case 27:case 31:case 35: case 39: case 43: case 47: case 51:case 63: case 55:case 67: case 59: case 71:
      {
        try
        {
            if(result.size()<3)
            {
                violation_sizeerror ++;
                //System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
                printViolationInfo(0,violation_sizeerror,loop);

				urlIndex = loopStartIndex;
            }
            else
            {
                String s1 = result.get(0);
                String s2 = result.get(1);
                String s3 = result.get(2);
                if((s1.indexOf("default") !=-1&& s1.indexOf("error")!=-1)
                    ||(s2.indexOf("default") !=-1&& s2.indexOf("error")!=-1)
                    ||(s3.indexOf("default") !=-1&& s3.indexOf("error")!=-1))
                {
                  violation_sizeerror ++;
                 // System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
                  printViolationInfo(0,violation_sizeerror,loop);

				  urlIndex = loopStartIndex;
                }
                else if(s3.equals(s1) && !s3.equals(s2))
                {
                  violation ++;
                 // System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                 printViolationInfo(1,violation,loop);
				 urlIndex = loopStartIndex;
                }
            }
        }catch (Exception e){}
        break;
      }
      case 4:case 8:case 12:case 16:case 20:case 24:case 28:case 32:case 36: case 40: case 44: case 48: case 52: case 64: case 56:case 68: case 60: case 72:
      {
        try{
          if(result.size()<2)
          {
              violation_sizeerror ++;
              //System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
              printViolationInfo(0,violation_sizeerror,loop);

			  urlIndex = loopStartIndex;
          }
          else
          {
              String s1 = result.get(0);
              String s2 = result.get(1);
              if((s1.indexOf("default") !=-1&& s1.indexOf("error")!=-1)
                  ||(s2.indexOf("default") !=-1&& s2.indexOf("error")!=-1))
              {
                violation_sizeerror ++;
                //System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
                printViolationInfo(0,violation_sizeerror,loop);

				urlIndex = loopStartIndex;
              }
              else if(!s1.equals(s2))
              {
                violation ++;
                //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                printViolationInfo(1,violation,loop);
				urlIndex = loopStartIndex;
              }
          }
        }catch (Exception e){}
        break;
      }
      case 5: case 45: case 53: case 65:
      {
          if(result.size()<1)
          {
            violation_sizeerror ++;
           // System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
            printViolationInfo(0,violation_sizeerror,loop);

			urlIndex = loopStartIndex;
          }
          else
          {
              String s = result.get(0);
              if(!s.equals("true"))
              {
                violation ++;
                //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                printViolationInfo(1,violation,loop);
				urlIndex = loopStartIndex;
              }
          }
          break;
      }
      case 54:case 30://2018-10-28
      {
          if(result.size()<3)
          {
              violation_sizeerror ++;
             // System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
             printViolationInfo(0,violation_sizeerror,loop);

			 urlIndex = loopStartIndex;
          }
          else
          {
              String s1 = result.get(0);
              String s2 = result.get(1);
              String s3 = result.get(2);
              if((s1.indexOf("default") !=-1&& s1.indexOf("error")!=-1)
                  ||(s2.indexOf("default") !=-1&& s2.indexOf("error")!=-1)
                  ||(s3.indexOf("default") !=-1&& s3.indexOf("error")!=-1))
              {
                violation_sizeerror ++;
                //System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
                printViolationInfo(0,violation_sizeerror,loop);

				urlIndex = loopStartIndex;
              }
              else if(s2.equals(descri2) && !s3.equals(descri))
              {
                violation ++;
               // System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                printViolationInfo(1,violation,loop);
				urlIndex = loopStartIndex;
              }
          }
          break;
      }   
      case 6:case 10: case 14:case 18:case 22:case 42: case 46: /*case 54:*/case 62:case 66: case 58: case 70://read the commodity in cart but then didnt read the descri
      {
          if(result.size()<2)
          {
              violation_sizeerror ++;
              //System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
              printViolationInfo(0,violation_sizeerror,loop);

			  urlIndex = loopStartIndex;
          }
          else
          {
              String s1 = result.get(0);
              String s2 = result.get(1);
              if((s1.indexOf("default") !=-1&& s1.indexOf("error")!=-1)
                  ||(s2.indexOf("default") !=-1&& s2.indexOf("error")!=-1))
              {
                violation_sizeerror ++;
               // System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
                printViolationInfo(0,violation_sizeerror,loop);

				urlIndex = loopStartIndex;
              }
              else if(s2.equals(s1))
              {
                violation ++;
                //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                printViolationInfo(1,violation,loop);
				//printViolationInfo(0,violation_sizeerror,loop);

				urlIndex = loopStartIndex;
              }
          }
          break;
      }   
      case 9: case 41: case 57:case 69:
      {
          if(result.size()<1)
          {
              violation_sizeerror ++;
              //System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
              printViolationInfo(0,violation_sizeerror,loop);

			  urlIndex = loopStartIndex;
          }
          else
          {
              String s = result.get(0);
              if(!s.equals(content))
              {
                violation ++;
                //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                printViolationInfo(1,violation,loop);
				urlIndex = loopStartIndex;
              }
          }
          break;
      }
     
     /* case 14:case 18:
      {
          if(result.size()<2)
          {
              violation_sizeerror ++;
              System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
              urlIndex = loopStartIndex;
          }
          else
          {
              String s1 = result.get(0);
              String s2 = result.get(1);
              if((s1.indexOf("default") !=-1&& s1.indexOf("error")!=-1)
                  ||(s2.indexOf("default") !=-1&& s2.indexOf("error")!=-1))
              {
                violation_sizeerror ++;
                System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
                urlIndex = loopStartIndex;
              }
              else if((s2.equals(comment) && !s1.equals(content)))
              {
                violation ++;
                System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                urlIndex = loopStartIndex;
              }
          }
          break;
      }*/
      case 21:
      {
          if(result.size()<1)
          {
              violation_sizeerror ++;
              //System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
              printViolationInfo(0,violation_sizeerror,loop);

			  urlIndex = loopStartIndex;
          }
          else
          {
              String s = result.get(0);
              if(!s.equals(content))
              {
                violation ++;
                //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                printViolationInfo(1,violation,loop);

				urlIndex = loopStartIndex;
              }
          }
        break;
      }


      case 29:
      {
        if(result.size()<1)
        {
            violation_sizeerror ++;
            //System.out.println("[violate_size] num:"+violation_sizeerror+", proportion:"+(float)violation_sizeerror/loop);
            printViolationInfo(0,violation_sizeerror,loop);

			urlIndex = loopStartIndex;
        }
        else
        {
            String s = result.get(0);
            if(!s.equals(descri))
            {
              violation ++;
             // System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
              printViolationInfo(1,violation,loop);
			  urlIndex = loopStartIndex;
            }
        }
        break;
      }
    }
    //System.out.println("violate");
    }


  public void createtablestmt(String u)
  {
    request.add(u);
  }

//
//    public void url_cas_twitter_readyouwrite()
//    {
//      //1
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//        //request.add(url);
//        createtablestmt();
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=twitterusertable&keyrow=id&key="+user+"&col=liketweet&val="+tweet+"&time="+time;
//        request.add(url);
//        url = "?type=read&key="+user+"&pk=liketweet&tableName=twitterusertable";
//        request.add(url);
//        url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//        request.add(url);
//        cleanstmt();
//
//    }
//    public void url_cas_twitter_writefollowread()
//    {
//      //2
//      /*  String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//        //request.add(url);
//        createtablestmt();
//        Calendar cal = Calendar.getInstance();
//        Date date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=twitterusertable&keyrow=id&key="+user+"&col=liketweet&val="+tweet+"&time="+time;
//        request.add(url);
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=twitterusertable&keyrow=id&key="+user2+"&col=liketweet&val="+tweet+"&time="+time;
//        request.add(url);
//        url = "?type=read&key="+user2+"&pk=liketweet&tableName=twitterusertable";
//        request.add(url);
//        url = "?type=read&key="+user+"&pk=liketweet&tableName=twitterusertable";
//        request.add(url);
//        url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//        request.add(url);
//        url = "?type=delete&key="+user2+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//        request.add(url);
//        cleanstmt();*/
//        String url = "";
//        initstmt();
//        createtablestmt();
//        Calendar cal = Calendar.getInstance();
//        Date date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//       //client fuzhu
//        url = "?type=insert2&tableName=twitterusertable&keyrow=id&key="+user+"&col=liketweet&val="+tweet+"&time="+time;
//        request.add(url);
//        // client main
//        url = "?type=read&key="+user+"&pk=liketweet&tableName=twitterusertable";
//        request.add(url);
//        //client main
//        url = "?type=insert&tableName=twitterusertable&keyrow=id&key="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//        request.add(url);
//        //client fuzhu
//        url = "?type=read2&key="+user+"&pk=liketweet&tableName=twitterusertable";
//        request.add(url);
//        url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//        request.add(url);
//      cleanstmt();
//    }
//    public void url_cas_twitter_monotonicread()
//    {
//      //3
//        /*?type=createtable&tableName=k123.utable1&fields=id,text,liketweet,text,retweet,text,commenttweet,text&pk=id
//            ?type=insert&tableName=k123.utable1&keyrow=id&key=user1&cf=1&col=liketweet&val=tweet1 #第一次插入
//            ?type=read&key=user1&pk=liketweet&tableName=k123.utable1 #第一次读取
//            ?type=insert&tableName=k123.utable1&keyrow=id&key=user1&cf=1&col=liketweet&val=tweet2 #第二次、更新
//            ?type=read&key=user1&pk=liketweet&tableName=k123.utable1 #第二次读取
//            ?type=read&key=user1&pk=liketweet&tableName=k123.utable1 #第三次读取
//            ?type=delete&key=user1&col=liketweet&keyrow=id&cf=1&tableName=k123.utable1
//        * */
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//        //request.add(url);
//        createtablestmt();
//        Calendar cal = Calendar.getInstance();
//        Date date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=twitterusertable&keyrow=id&key="+user+"&col=liketweet&val="+tweet+"&time="+time;
//        request.add(url);
//        url = "?type=read2&key="+user+"&pk=liketweet&tableName=twitterusertable";
//        request.add(url);
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=twitterusertable&keyrow=id&key="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//        request.add(url);
//        url = "?type=read2&key="+user+"&pk=liketweet&tableName=twitterusertable";
//        request.add(url);
//       /* try{
//          Thread.sleep(50);
//        }catch(Exception e){
//          e.printStackTrace();
//        }*/
//        url = "?type=read2&key="+user+"&pk=liketweet&tableName=twitterusertable";
//        request.add(url);
//        url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//        request.add(url);
//        cleanstmt();
//
//    }
//    public void url_cas_twitter_monotonicwrite()
//    {
//      //4
//      String url ="";
//        //String url = "";initstmt();
//       // request.add(initstmt());
//      initstmt();
//        //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//        //request.add(url);
//        createtablestmt();
//        //-----client b write 1------------------------------------
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=twitterusertable&keyrow=id&key="+user+"&col=liketweet&val="+tweet+"&time="+time;
//        request.add(url);
//        //-----client b write 2------------------------------------
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=twitterusertable&keyrow=id&key="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//        request.add(url);
//        //-----client a read 1------------------------------------
//        url = "?type=read2&key="+user+"&pk=liketweet&tableName=twitterusertable";
//        request.add(url);
//        //-----client a read 2------------------------------------
//        url = "?type=read2&key="+user+"&pk=liketweet&tableName=twitterusertable";
//        request.add(url);
//        url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//        request.add(url);
//        //url = "?type=cleanup";
//        //request.add(cleanstmt());
//        cleanstmt();
//    }
//    public void url_cas_amazon_readyourwrite()
//    {
//      //5
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=k123.amazoncommenttable&fields=id,text,descri,text,user,text,ifuseful,text,belongsto,text,time,text&pk=id";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=k123.amazoncommenttable&fields=id,text,descri,text,user,text,ifuseful,text,belongsto,text,time,text&pk=id");
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=amazoncommenttable&keyrow=id&key="+comment+"&col=ifuseful&val=true&time="+time;
//        request.add(url);
//        url = "?type=read&key="+comment+"&pk=ifuseful&tableName=amazoncommenttable";
//        request.add(url);
//        url = "?type=delete&key="+comment+"&col=ifuseful&keyrow=id&tableName=amazoncommenttable";
//        request.add(url);
//        cleanstmt();
//
//    }
//    public void url_cas_amazon_writefollowread()
//    {
//      //6
//        /* read des ,add into cart,then read the descri of commodity again
//         */
//        String url = "";
//        initstmt();
//      Calendar cal = Calendar.getInstance();
//      Date date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        //client B
//        url = "?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id";
//        request.add(url);
//        url = "?type=insert2&tableName=amazoncommoditytable&keyrow=id&key="+commodity+"&col=descri&val="+descri+"&time="+time;
//        request.add(url);
//      //client main
//        url = "?type=read&key="+commodity+"&pk=descri&tableName=amazoncommoditytable";
//        request.add(url);
//      //client main
//      url = "?type=insert&tableName=amazoncommoditytable&keyrow=id&key="+commodity+"&col=descri&val="+descri2+"&time="+time;
//      request.add(url);
//      //client B
//      url = "?type=read2&key="+commodity+"&pk=descri&tableName=amazoncommoditytable";
//      request.add(url);
//      url = "?type=delete&key="+commodity+"&col=descri&keyrow=id&tableName=amazoncommoditytable";
//      request.add(url);
//      //url = "?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id";
//        //request.add(url);
//        //url = "?type=createtable&tableName=k123.amazoncarttable&fields=id,text,commodity,text&pk=id";
//        //request.add(url);
//       /* createtablestmt("?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id");
//        createtablestmt("?type=createtable&tableName=k123.amazoncarttable&fields=id,text,commodity,text&pk=id");
//        Calendar cal = Calendar.getInstance();
//        Date date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=amazoncommoditytable&keyrow=id&key="+commodity+"&col=descri&val="+descri+"&time="+time;
//        request.add(url);
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=amazoncarttable&keyrow=id&key="+user+"&col=commodity&val="+commodity;
//        request.add(url);
//        url = "?type=read&key="+user+"&pk=commodity&tableName=amazoncarttable";
//        request.add(url);
//        url = "?type=read&key="+commodity+"&pk=descri&tableName=amazoncommoditytable";
//        request.add(url);
//        url = "?type=delete&key="+commodity+"&col=descri&keyrow=id&tableName=amazoncommoditytable";
//        request.add(url);
//        url = "?type=delete&key="+user+"&col=commodity&keyrow=id&tableName=amazoncarttable";
//        request.add(url);8/*/
//        cleanstmt();
//    }
//    public void url_cas_amazon_monotonicread()
//    {
//      //7
//        /*user read a descri(r1),add it to cart,then the seller change the descri and user read it (r2),fresh the web page(r3)*/
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id");
//        Calendar cal = Calendar.getInstance();
//        Date date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=amazoncommoditytable&keyrow=id&key="+commodity+"&col=descri&val="+descri+"&time="+time;
//        request.add(url);
//        url = "?type=read2&key="+commodity+"&pk=descri&tableName=amazoncommoditytable";//r1
//        request.add(url);
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=amazoncommoditytable&keyrow=id&key="+commodity+"&col=descri&val="+descri2+"&time="+time;//这个写应该在其他client？
//        request.add(url);
//        url = "?type=read2&key="+commodity+"&pk=descri&tableName=amazoncommoditytable";//r2
//        request.add(url);
//        url = "?type=read2&key="+commodity+"&pk=descri&tableName=amazoncommoditytable";//r3
//        request.add(url);
//        url = "?type=delete&key="+commodity+"&col=descri&keyrow=id&tableName=amazoncommoditytable";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_cas_amazon_monotonicwrite()
//    {
//      //8
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id");
//        //client a write 1
//        Calendar cal = Calendar.getInstance();
//        Date date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=amazoncommoditytable&keyrow=id&key="+commodity+"&col=descri&val="+descri+"&time="+time;
//        request.add(url);
//        //client a write 2
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=amazoncommoditytable&keyrow=id&key="+commodity+"&col=descri&val="+descri2+"&time="+time;
//        request.add(url);
//
//        //client b read 1
//        url = "?type=read2&key="+commodity+"&pk=descri&tableName=amazoncommoditytable";//r1
//        request.add(url);
//        //client b read 2
//        url = "?type=read2&key="+commodity+"&pk=descri&tableName=amazoncommoditytable";//r2
//        request.add(url);
//       url = "?type=delete&key="+commodity+"&col=descri&keyrow=id&tableName=amazoncommoditytable";
//       request.add(url);
//        cleanstmt();
//    }
//    public void url_cas_flickr_readyourwrite()
//    {
//      //9
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id");
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=content&val="+content+"&time="+time;//edit picture:rotate
//        request.add(url);
//        url = "?type=read&key="+picture+"&pk=content&tableName=flickrpicturetable";
//        request.add(url);
//        url = "?type=delete&key="+picture+"&col=content&keyrow=id&tableName=flickrpicturetable";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_cas_flickr_writefollowread()
//    {
//
//      //10
//        //read the content of picture,then comment,if one can read this comment but cannot read this content,---->violate
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id");
//        Calendar cal = Calendar.getInstance();
//        Date date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert2&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=content&val="+content+"&time="+time;
//        request.add(url);
//        url = "?type=read&key="+picture+"&pk=content&tableName=flickrpicturetable";
//        request.add(url);
//        url = "?type=insert&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=content&val="+content2+"&time="+time;
//        request.add(url);
//        url = "?type=read2&key="+picture+"&pk=content&tableName=flickrpicturetable";
//        request.add(url);
//        url = "?type=delete&key="+picture+"&col=content&keyrow=id&tableName=flickrpicturetable";
//        request.add(url);
//        /*cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=comment&val="+comment;
//        request.add(url);
//
//        url = "?type=read&key="+picture+"&pk=comment&tableName=flickrpicturetable";
//        request.add(url);
//        url = "?type=read&key="+picture+"&pk=content&tableName=flickrpicturetable";
//        request.add(url);
//        url = "?type=delete&key="+picture+"&col=comment&keyrow=id&tableName=flickrpicturetable";
//        request.add(url);
//        url = "?type=delete&key="+picture+"&col=content&keyrow=id&tableName=flickrpicturetable";
//        request.add(url);*/
//        cleanstmt();
//    }
//    public void url_cas_flickr_monotonicread()
//    {
//      //11
//        //client b insert (w1), client a read it (r1) ,client b edit it(w2),then client a read it  (r2),fresh web page and client a read it again(r3)
//        //-----client b--------w1----------------
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id");
//        Calendar cal = Calendar.getInstance();
//        Date date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=content&val="+content+"&time="+time;
//        request.add(url);
//
//        //-------client a-----r1--------------------
//        url = "?type=read2&key="+picture+"&pk=content&tableName=flickrpicturetable";
//        request.add(url);
//
//        //-------client b -----edit w2-------------
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=content&val="+content2+"&time="+time;
//        request.add(url);
//
//        //----client a ----read r2--------------------
//        url = "?type=read2&key="+picture+"&pk=content&tableName=flickrpicturetable";
//        request.add(url);
//        //---client a -----read r3------
//        url = "?type=read2&key="+picture+"&pk=content&tableName=flickrpicturetable";
//        request.add(url);
//
//        url = "?type=delete&key="+picture+"&col=content&keyrow=id&tableName=flickrpicturetable";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_cas_flickr_monotonicwrite()
//    {
//      //12
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id");
//        //-------client a-----w1------------------
//        Calendar cal = Calendar.getInstance();
//        Date date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=content&val="+content+"&time="+time;
//        request.add(url);
//        //-------client a ----- w2-------------
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=content&val="+content2+"&time="+time;
//        request.add(url);
//
//        //----client b ----read r1--------------------
//        url = "?type=read2&key="+picture+"&pk=content&tableName=flickrpicturetable";
//        request.add(url);
//        //---client b -----read r2------
//        url = "?type=read2&key="+picture+"&pk=content&tableName=flickrpicturetable";
//        request.add(url);
//
//        url = "?type=delete&key="+picture+"&col=content&keyrow=id&tableName=flickrpicturetable";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_hbase_twitter_readyouwrite()
//    {
//      //13
//        /* ?type=init
//         ?type=createtable&tableName=usertable&CFName=id,liketweet,retweet
//         ?type=insert&tableName=usertable&keyrow=user1&key=null&cf=null&col=liketweet&val=tweet1
//         ?type=read&key=user1&fields=liketweet&tableName=usertable
//         ?type=delete&tableName=usertable&key=user1&keyrow=null&cf=null&col=liketweet
//         ?type=cleanup
//        */
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=twitterusertable&CFName=id,tweetid,liketweet,retweet,commenttweet,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=twitterusertable&CFName=id,tweetid,liketweet,retweet,commenttweet,time");
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=twitterusertable&keyrow="+tweet+"&col=liketweet&val="+tweet+"&time="+time;
//        request.add(url);
//        url = "?type=read&key="+tweet+"&fields=liketweet&tableName=twitterusertable";
//        request.add(url);
//        url = "?type=delete&key="+tweet+"&tableName=twitterusertable&col=liketweet";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_hbase_twitter_writefollowread()
//    {
//      //14
//        //read the content of tweet,then comment,if one can read this comment but cannot read this content,---->violate
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=tweettable&CFName=id,content,likeduser,retweetuser,commentid,inmommentid,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=tweettable&CFName=id,content,likeduser,retweetuser,commentid,inmommentid,time");
//        //w
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert2&tableName=tweettable&keyrow="+tweet+"&col=content&val="+content+"&time="+time;
//        request.add(url);
//        url = "?type=read&key="+tweet+"&fields=content&tableName=tweettable";
//        request.add(url);
//        url = "?type=insert&tableName=tweettable&keyrow="+tweet+"&col=content&val="+content2+"&time="+time;
//        request.add(url);
//        url = "?type=read2&key="+tweet+"&fields=content&tableName=tweettable";
//        request.add(url);
//        //r
//        /*url = "?type=read&key="+tweet+"&fields=commentid&tableName=tweettable";
//        request.add(url);
//        url = "?type=read&key="+tweet+"&fields=content&tableName=tweettable";
//        request.add(url);
//        url = "?type=delete&key="+tweet+"&tableName=tweettable&col=commentid";
//        request.add(url);*/
//        url = "?type=delete&key="+tweet+"&tableName=tweettable&col=content";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_hbase_twitter_monotonicread()
//    {
//      //15
//        String url = "";
//        initstmt();
//        //client b read a moment
//        //url = "?type=createtable&tableName=twittermomenttable&CFName=id,content,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=twittermomenttable&CFName=id,content,time");
//        //w
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=twittermomenttable&keyrow="+moment+"&col=content&val="+content+"&time="+time;
//        request.add(url);
//        //client a read : r1
//        url = "?type=read2&key="+moment+"&fields=content&tableName=twittermomenttable";
//        request.add(url);
//        //client b edit
//        url = "?type=insert&tableName=twittermomenttable&keyrow="+moment+"&col=content&val="+content2+"&time="+time;
//        request.add(url);
//        //client a read r2 & fresh & r3
//        url = "?type=read2&key="+moment+"&fields=content&tableName=twittermomenttable";
//        request.add(url);
//       /* try{
//          Thread.sleep(5);
//        }catch(Exception e){
//          e.printStackTrace();
//        }*/
//        url = "?type=read2&key="+moment+"&fields=content&tableName=twittermomenttable";
//        request.add(url);
//        url = "?type=delete&key="+moment+"&tableName=twittermomenttable&col=content";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_hbase_twitter_monotonicwrite()
//    {
//      //16
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=twittermomenttable&CFName=id,content,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=twittermomenttable&CFName=id,content,time");
//        //client a w1
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=twittermomenttable&keyrow="+moment+"&col=content&val="+content+"&time="+time;
//        request.add(url);
//        //client a w2
//        url = "?type=insert&tableName=twittermomenttable&keyrow="+moment+"&col=content&val="+content2+"&time="+time;
//        request.add(url);
//        //client b read : r1
//        url = "?type=read2&key="+moment+"&fields=content&tableName=twittermomenttable";
//        request.add(url);
//        //client b r2
//        url = "?type=read2&key="+moment+"&fields=content&tableName=twittermomenttable";
//        request.add(url);
//
//        url = "?type=delete&key="+moment+"&tableName=twittermomenttable&col=content";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_hbase_amazon_readyourwrite()
//    {
//      //17
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=amazoncommoditytable&CFName=id,price,descri,question,comment,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=amazoncommoditytable2&CFName=id,price,descri,question,comment,time");
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=amazoncommoditytable2&keyrow="+commodity+"&col=descri&val="+descri+"&time="+time;
//        request.add(url);
//        url = "?type=read&key="+commodity+"&fields=descri&tableName=amazoncommoditytable2";
//        request.add(url);
//        url = "?type=delete&key="+commodity+"&tableName=amazoncommoditytable2&col=descri";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_hbase_amazon_writefollowread()
//    {
//      //18
//        String url = "";
//        initstmt();
//        //create
//        //url = "?type=createtable&tableName=amazonquestiontable&CFName=id,content,comment,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=amazonquestiontable2&CFName=id,content,comment,time");
//        //w
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert2&tableName=amazonquestiontable2&keyrow="+question+"&col=content&val="+content+"&time="+time;
//        request.add(url);
//        url = "?type=read&key="+question+"&fields=content&tableName=amazonquestiontable2";
//        request.add(url);
//        url = "?type=insert&tableName=amazonquestiontable2&keyrow="+question+"&col=content&val="+content2+"&time="+time;
//        request.add(url);
//        url = "?type=read2&key="+question+"&fields=content&tableName=amazonquestiontable2";
//        request.add(url);
//       /* //w comment
//        url = "?type=insert&tableName=amazonquestiontable&keyrow="+question+"&col=comment&val="+comment+"&time="+time;
//        request.add(url);
//
//        //r    read the comment but cant read the question ,violate
//        url = "?type=read&key="+question+"&fields=comment&tableName=amazonquestiontable";
//        request.add(url);
//        url = "?type=read&key="+question+"&fields=content&tableName=amazonquestiontable";
//        request.add(url);
//        url = "?type=delete&key="+question+"&tableName=amazonquestiontable&col=comment";
//        request.add(url);*/
//        url = "?type=delete&key="+question+"&tableName=amazonquestiontable&col=content";
//        request.add(url);
//       cleanstmt();
//    }
//    public void url_hbase_amazon_monotonicread()
//    {
//      //19
//        String url = "";
//        initstmt();
//        //client b read a quesiton
//        //url = "?type=createtable&tableName=amazonquestiontable&CFName=id,content,comment,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=amazonquestiontable2&CFName=id,content,comment,time");
//        //w
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=amazonquestiontable2&keyrow="+question+"&col=content&val="+content+"&time="+time;
//        request.add(url);
//        //client a read : r1
//        url = "?type=read2&key="+question+"&fields=content&tableName=amazonquestiontable2";
//        request.add(url);
//        //client b edit
//        url = "?type=insert&tableName=amazonquestiontable2&keyrow="+question+"&col=content&val="+content2+"&time="+time;
//        request.add(url);
//        //client a read r2 & fresh & r3
//        url = "?type=read2&key="+question+"&fields=content&tableName=amazonquestiontable2";
//        request.add(url);
//        /*try{
//          Thread.sleep(50);
//        }catch(Exception e){
//          e.printStackTrace();
//        }*/
//        url = "?type=read2&key="+question+"&fields=content&tableName=amazonquestiontable";
//        request.add(url);
//         url = "?type=delete&key="+question+"&tableName=amazonquestiontable&col=content";
//       request.add(url);
//        cleanstmt();
//    }
//    public void url_hbase_amazon_monotonicwrite()
//    {
//      //20
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=amazonquestiontable&CFName=id,content,comment,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=amazonquestiontable2&CFName=id,content,comment,time");
//        //client a w1
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=amazonquestiontable2&keyrow="+question+"&col=content&val="+content+"&time="+time;
//        request.add(url);
//        //client a w2
//        url = "?type=insert&tableName=amazonquestiontable2&keyrow="+question+"&col=content&val="+content2+"&time="+time;
//        request.add(url);
//        //client b read : r1
//        url = "?type=read2&key="+question+"&fields=content&tableName=amazonquestiontable2";
//        request.add(url);
//        //client b r2
//        url = "?type=read2&key="+question+"&fields=content&tableName=amazonquestiontable2";
//        request.add(url);
//       /* try{
//          Thread.sleep(50);
//        }catch(Exception e){
//          e.printStackTrace();
//        }*/
//         url = "?type=delete&key="+question+"&tableName=amazonquestiontable2&col=content";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_hbase_flickr_readyourwrite()
//    {
//      //21
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=flickrpicturetable&CFName=id,content,permit,likeduser,album,location,comment,tag,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=flickrpicturetable&CFName=id,content,permit,likeduser,album,location,comment,tag,time");
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=flickrpicturetable&keyrow="+picture+"&col=content&val="+content+"&time="+time;
//        request.add(url);
//        url = "?type=read&key="+picture+"&fields=content&tableName=flickrpicturetable";
//        request.add(url);
//        url = "?type=delete&key="+picture+"&tableName=flickrpicturetable&col=content";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_hbase_flickr_writefollowread()
//    {
//      //22
//        String url = "";
//        initstmt();
//        //create
//        //url = "?type=createtable&tableName=flickrpicturetable&CFName=id,content,permit,likeduser,album,location,comment,tag,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=flickrpicturetable&CFName=id,content,permit,likeduser,album,location,comment,tag,time");
//        //w tag
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert2&tableName=flickrpicturetable&keyrow="+picture+"&col=tag&val="+tag+"&time="+time;
//        request.add(url);
//      	url = "?type=read&key="+picture+"&fields=tag&tableName=flickrpicturetable";
//      	request.add(url);
//	url = "?type=insert&tableName=flickrpicturetable&keyrow="+picture+"&col=tag&val="+tag2+"&time="+time;
//        request.add(url);
//      	url = "?type=read2&key="+picture+"&fields=tag&tableName=flickrpicturetable";
//      	request.add(url);
//    //w  likeuserid
//       /* url = "?type=insert&tableName=flickrpicturetable&keyrow="+picture+"&col=likeduser&val="+user+"&time="+time;
//        request.add(url);
//        //r1 the user ,r2 if or not read the tag?
//        url = "?type=read&key="+picture+"&fields=tag&tableName=flickrpicturetable";
//        request.add(url);
//        url = "?type=read&key="+picture+"&fields=likeduser&tableName=flickrpicturetable";
//        request.add(url);
//        url = "?type=delete&key="+picture+"&tableName=flickrpicturetable&col=tag";
//        request.add(url);*/
//        url = "?type=delete&key="+picture+"&tableName=flickrpicturetable&col=likeduser";
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_hbase_flickr_monotonicread()
//    {
//      //23
//        String url = "";
//        initstmt();
//        //client b read a moment
//        //url = "?type=createtable&tableName=flickrpicturetable&CFName=id,content,permit,likeduser,album,location,comment,tag,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=flickrpicturetable&CFName=id,content,permit,likeduser,album,location,comment,tag,time");
//        //w
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=flickrpicturetable&keyrow="+picture+"&col=permit&val=1&time="+time;
//        request.add(url);
//        //client a read : r1
//        url = "?type=read2&key="+picture+"&fields=permit&tableName=flickrpicturetable";
//        request.add(url);
//        //client b edit
//        url = "?type=insert&tableName=flickrpicturetable&keyrow="+picture+"&col=permit&val=0&time="+time;
//        request.add(url);
//        //client a read r2 & fresh & r3
//        url = "?type=read2&key="+picture+"&fields=permit&tableName=flickrpicturetable";
//        request.add(url);
//        url = "?type=read2&key="+picture+"&fields=permit&tableName=flickrpicturetable";
//        request.add(url);
//         //url = "?type=delete&key="+picture+"&tableName=flickrpicturetable&col=permit";
//        //request.add(url);
//        cleanstmt();
//    }
//    public void url_hbase_flickr_monotonicwrite()
//    {
//      //24
//        String url = "";
//        initstmt();
//        //url = "?type=createtable&tableName=flickrpicturetable&CFName=id,content,permit,likeduser,album,location,comment,tag,time";
//        //request.add(url);
//        createtablestmt("?type=createtable&tableName=flickrpicturetable&CFName=id,content,permit,likeduser,album,location,comment,tag,time");
//        //a w1
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&tableName=flickrpicturetable&keyrow="+picture+"&col=permit&val=1&time="+time;
//        request.add(url);
//        //client a w 2
//        url = "?type=insert&tableName=flickrpicturetable&keyrow="+picture+"&col=permit&val=0&time="+time;
//        request.add(url);
//        //client b read : r1
//        url = "?type=read2&key="+picture+"&fields=permit&tableName=flickrpicturetable";
//        request.add(url);
//
//        //client b read r2
//        url = "?type=read2&key="+picture+"&fields=permit&tableName=flickrpicturetable";
//        request.add(url);
//
//        // url = "?type=delete&key="+picture+"&tableName=flickrpicturetable&col=permit";
//        //request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_twitter_readyouwrite()
//    {
//      //25
//        /*?type=insert&key=k1&tableName,=null&keyrow=null&cf=null&col=null&val=v1
//        ?type=read&tableName=null&key=k1&pk=null
//        ?type=delete&tableName=null&keyrow=null&key=k1&cf=null&col=null*/
//        String url = "";
//        initstmt();
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&key="+user+"&val="+tweet;
//        request.add(url);
//        url = "?type=read&key="+user;
//        request.add(url);
//        url = "?type=delete&key="+user;
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_twitter_writefollowread()
//    {
//        //26
//        //需要两个client?
//        String url = "";
//        initstmt();
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert2&key="+user+"&val="+tweet;
//        request.add(url);
//	url = "?type=read&key="+user;
//        request.add(url);
//	url = "?type=insert&key="+user+"&val="+tweet2;
//        request.add(url);
//        url = "?type=read2&key="+user;
//        request.add(url);
//        //url = "?type=read&key="+user;
//       // request.add(url);
//       /* url = "?type=insert&key="+user2+"&val="+tweet;
//        request.add(url);
//        url = "?type=read&key="+user2;
//        request.add(url);
//        url = "?type=read&key="+user;
//        request.add(url);
//        url = "?type=delete&key="+user;
//        request.add(url);*/
//        url = "?type=delete&key="+user;
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_twitter_monotonicread()
//    {
//      //27
//        String url = "";
//        initstmt();
//        //client b
//        url = "?type=insert&key="+user+"&val="+tweet;
//        request.add(url);
//        //client a
//        url = "?type=read2&key="+user;
//        request.add(url);
//        //client b
//        url = "?type=insert&key="+user+"&val="+tweet2;
//        request.add(url);
//        //client a
//        url = "?type=read2&key="+user;
//        request.add(url);
//        //client a
//        url = "?type=read2&key="+user;
//        request.add(url);
//        url = "?type=delete&key="+user;
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_twitter_monotonicwrite()
//    {
//      //28
//         String url = "";
//         initstmt();
//        //client a w1
//        url = "?type=insert&key="+user+"&val="+tweet;
//        request.add(url);
//        //client a w2
//        url = "?type=insert&key="+user+"&val="+tweet2;
//        request.add(url);
//
//        //client b r1
//        url = "?type=read2&key="+user;
//        request.add(url);
//        //client b r2
//        url = "?type=read2&key="+user;
//        request.add(url);
//
//        url = "?type=delete&key="+user;
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_amazon_readyourwrite()
//    {
//      //29
//      String url = "";
//      initstmt();
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&key="+commodity+"&val="+descri;
//        request.add(url);
//        url = "?type=read&key="+commodity;
//        request.add(url);
//        url = "?type=delete&key="+commodity;
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_amazon_writefollowread()
//    {
//      //30
//        String url = "";
//        initstmt();
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert2&key="+user+"&val="+tweet;
//        request.add(url);
//	url = "?type=read&key="+user;
//        request.add(url);
//       url = "?type=insert&key="+user2+"&val="+tweet2;
//        request.add(url);
//        url = "?type=read2&key="+user;
//	 //url = "?type=read&key="+user;
//        request.add(url);
//        url = "?type=read2&key="+user2;
//   //url = "?type=read&key="+user;
//        request.add(url);
//       /* url = "?type=insert&key="+user2+"&val="+tweet;
//        request.add(url);
//        url = "?type=read&key="+user2;
//        request.add(url);
//        url = "?type=read&key="+user;
//        request.add(url);
//        url = "?type=delete&key="+user;
//        request.add(url);*/
//        url = "?type=delete&key="+user;
//        request.add(url);
//        url = "?type=delete&key="+user2;
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_amazon_monotonicread()
//    {
//      //31
//         String url = "";
//         initstmt();
//        //client b
//        url = "?type=insert&key="+user+"&val="+tweet;
//        request.add(url);
//        //client a
//        url = "?type=read2&key="+user;
//        request.add(url);
//        //client b
//        url = "?type=insert&key="+user+"&val="+tweet2;
//        request.add(url);
//        //client a
//        url = "?type=read2&key="+user;
//        request.add(url);
//       /* try{
//          Thread.sleep(50);
//        }catch(Exception e){
//          e.printStackTrace();
//        }*/
//        //client a
//        url = "?type=read2&key="+user;
//        request.add(url);
//       url = "?type=delete&key="+user;
//       request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_amazon_monotonicwrite()
//    {
//      //32
//        String url = "";
//        initstmt();
//        //client a w1
//        url = "?type=insert&key="+user+"&val="+tweet;
//        request.add(url);
//        //client a w2
//        url = "?type=insert&key="+user+"&val="+tweet2;
//        request.add(url);
//
//        //client b r1
//        url = "?type=read2&key="+user;
//        request.add(url);
//        //client b r2
//        url = "?type=read2&key="+user;
//        request.add(url);
//
//       url = "?type=delete&key="+user;
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_flickr_readyourwrite()
//    {
//      //33
//        String url = "";
//        initstmt();
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert&key="+picture+"&val="+descri;
//        request.add(url);
//        url = "?type=read&key="+picture;
//        request.add(url);
//       url = "?type=delete&key="+picture;
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_flickr_writefollowread()
//    {
//      //34
//        String url = "";
//        initstmt();
//        cal = Calendar.getInstance();
//        date = cal.getTime();
//        time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//        url = "?type=insert2&key="+user+"&val="+tweet;
//        request.add(url);
//	url = "?type=read&key="+user;
//        request.add(url);
//	url = "?type=insert&key="+user+"&val="+tweet2;
//        request.add(url);
//        url = "?type=read2&key="+user;
//        request.add(url);
//      /*  //url = "?type=read&key="+user;
//       // request.add(url);
//        url = "?type=insert&key="+user2+"&val="+tweet;
//        request.add(url);
//        url = "?type=read&key="+user2;
//        request.add(url);
//        url = "?type=read&key="+user;
//        request.add(url);
//        url = "?type=delete&key="+user;
//        request.add(url);
//        */url = "?type=delete&key="+user;
//        request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_flickr_monotonicread()
//    {
//      //35
//        String url = "";
//        initstmt();
//        //client b
//        url = "?type=insert&key="+user+"&val="+tweet;
//        request.add(url);
//        //client a
//        url = "?type=read2&key="+user;
//        request.add(url);
//        //client b
//        url = "?type=insert&key="+user+"&val="+tweet2;
//        request.add(url);
//        //client a
//        url = "?type=read2&key="+user;
//        request.add(url);
//        /*try{
//          Thread.sleep(50);
//        }catch(Exception e){
//          e.printStackTrace();
//        }*/
//        //client a
//        url = "?type=read2&key="+user;
//        request.add(url);
//       // url = "?type=delete&key="+user;
//       // request.add(url);
//        cleanstmt();
//    }
//    public void url_redis_flickr_monotonicwrite()
//    {
//      //36
//        String url = "";
//        initstmt();
//        //client a w1
//        url = "?type=insert&key="+user+"&val="+tweet;
//        request.add(url);
//        //client a w2
//        url = "?type=insert&key="+user+"&val="+tweet2;
//        request.add(url);
//
//        //client b r1
//        url = "?type=read2&key="+user;
//        request.add(url);
//        //client b r2
//        url = "?type=read2&key="+user;
//        request.add(url);
//
//       // url = "?type=delete&key="+user;
//       // request.add(url);
//        cleanstmt();
//    }
//    public void url_mongodb_twitter_readyourwrite()
//    {
//      //37
//      String url = "";
//      initstmt();
//      url = "?type=createtable&tableName=twitterusertable";
//      request.add(url);
//      url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet;
//      request.add(url);
//      url = "?type=read&tableName=twitterusertable&key="+user+"&col=liketweet";
//      request.add(url);
//      url = "?type=delete&tableName=twitterusertable&id="+user;
//      request.add(url);
//      cleanstmt();
//
//    }
//    public void url_mongodb_twitter_writefollowread()
//    {
//        //38
//      String url = "";
//      initstmt();
//      //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//      //request.add(url);
//      Calendar cal = Calendar.getInstance();
//      Date date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=createtable&tableName=twitterusertable";
//      request.add(url);
//      url = "?type=insert2&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//      request.add(url);
//	url = "?type=read&key="+user+"&col=liketweet&tableName=twitterusertable";
//      request.add(url);
//	url = "?type=update&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//      request.add(url);
//	url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//      request.add(url);
//      /*cal = Calendar.getInstance();
//      date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=insert&tableName=twitterusertable&id="+user2+"&col=liketweet&val="+tweet+"&time="+time;
//      request.add(url);
//     // url = "?type=update&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//     // request.add(url);
//     // url = "?type=update&tableName=twitterusertable&id="+user2+"&col=liketweet&val="+tweet+"&time="+time;
//     // request.add(url);
//      url = "?type=read&key="+user2+"&col=liketweet&tableName=twitterusertable";
//      request.add(url);
//      url = "?type=read&key="+user+"&col=liketweet&tableName=twitterusertable";
//      request.add(url);
//      url = "?type=delete&id="+user+"&tableName=twitterusertable";
//      request.add(url);*/
//      url = "?type=delete&id="+user+"&tableName=twitterusertable";
//      request.add(url);
//      cleanstmt();
//    }
//    public void url_mongodb_twitter_monotonicread()
//    {
//      //39
//      String url = "";
//      initstmt();
//      url = "?type=createtable&tableName=twitterusertable";
//      request.add(url);
//
//      Calendar cal = Calendar.getInstance();
//      Date date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//      request.add(url);
//      url = "?type=update&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//      request.add(url);
//      url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//      request.add(url);
//      cal = Calendar.getInstance();
//      date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=update&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//      request.add(url);
//      url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//      request.add(url);
//     /* try{
//        Thread.sleep(50);
//      }catch(Exception e){
//        e.printStackTrace();
//      }*/
//      url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//      request.add(url);
//       url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//      request.add(url);
//      cleanstmt();
//    }
//    public void url_mongodb_twitter_monotonicwrite()
//    {
//      //40
//      String url = "";
//      initstmt();
//      url = "?type=createtable&tableName=twitterusertable";
//      request.add(url);
//      //-----client b write 1------------------------------------
//      cal = Calendar.getInstance();
//      date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//      request.add(url);
//      //-----client b write 2------------------------------------
//      cal = Calendar.getInstance();
//      date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=update&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//      request.add(url);
//      url = "?type=update&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//      request.add(url);
//      //-----client a read 1------------------------------------
//      url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//      request.add(url);
//      //-----client a read 2------------------------------------
//      url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//      request.add(url);
//      url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//      request.add(url);
//      //url = "?type=cleanup";
//      //request.add(cleanstmt());
//      cleanstmt();
//    }
//    public void url_mongodb_flickr_readyourwrite()
//    {
//      //41
//      String url = "";
//      initstmt();
//      url = "?type=createtable&tableName=flickrpicturetable";
//      request.add(url);
//      url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content;
//      request.add(url);
//      url = "?type=read&tableName=flickrpicturetable&key="+picture+"&col=content";
//      request.add(url);
//      url = "?type=delete&tableName=flickrpicturetable&id="+picture;
//      request.add(url);
//      cleanstmt();
//    }
//    public void url_mongodb_flickr_writefollowread()
//    {
//      //42
//      String url = "";
//      initstmt();
//      //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//      //request.add(url);
//      Calendar cal = Calendar.getInstance();
//      Date date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=createtable&tableName=flickrpicturetable";
//      request.add(url);
//      url = "?type=insert2&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;
//      request.add(url);
//	url = "?type=read&key="+picture+"&col=content&tableName=flickrpicturetable";
//      request.add(url);
//	url = "?type=update&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content2+"&time="+time;
//      request.add(url);
//	url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//      request.add(url);
//      /*cal = Calendar.getInstance();
//      date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=update&tableName=flickrpicturetable&id="+picture+"&col=comment&val="+comment+"&time="+time;
//      request.add(url);
//      url = "?type=read&key="+picture+"&col=comment&tableName=flickrpicturetable";
//      request.add(url);
//      url = "?type=read&key="+picture+"&col=content&tableName=flickrpicturetable";
//      request.add(url);
//      url = "?type=delete&id="+picture+"&tableName=flickrpicturetable";
//      request.add(url);*/
//      url = "?type=delete&id="+picture+"&tableName=flickrpicturetable";
//      request.add(url);
//      cleanstmt();
//    }
//    public void url_mongodb_flickr_monotonicread()
//    {
//
//      //43
//      String url = "";
//      initstmt();
//      url = "?type=createtable&tableName=flickrpicturetable";
//      request.add(url);
//
//      Calendar cal = Calendar.getInstance();
//      Date date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;
//      request.add(url);
//      url = "?type=update&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;
//      request.add(url);
//      url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//      request.add(url);
//      cal = Calendar.getInstance();
//      date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=update&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content2+"&time="+time;
//      request.add(url);
//      url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//      request.add(url);
//     /* try{
//        Thread.sleep(50);
//      }catch(Exception e){
//        e.printStackTrace();
//      }*/
//      url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//      request.add(url);
//       url = "?type=delete&id="+picture+"&tableName=flickrpicturetable";
//      request.add(url);
//      cleanstmt();
//    }
//    public void url_mongodb_flickr_monotonicwrite()
//    {
//
//      //44
//      String url = "";
//      initstmt();
//      url = "?type=createtable&tableName=flickrpicturetable";
//      request.add(url);
//      //-----client b write 1------------------------------------
//      cal = Calendar.getInstance();
//      date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=insert&tableName=flickrpicturetable&"+picture+"&col=content&val="+content+"&time="+time;
//      request.add(url);
//      //-----client b write 2------------------------------------
//      cal = Calendar.getInstance();
//      date = cal.getTime();
//      url = "?type=update&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;
//      request.add(url);
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=update&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content2+"&time="+time;
//      request.add(url);
//      //-----client a read 1------------------------------------
//      url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//      request.add(url);
//      //-----client a read 2------------------------------------
//      url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//      request.add(url);
//      url = "?type=delete&id="+picture+"&tableName=flickrpicturetable";
//      request.add(url);
//      //url = "?type=cleanup";
//      //request.add(cleanstmt());
//      cleanstmt();
//    }
//    public void url_mongodb_amazon_readyourwrite()
//    {
//      //45
//      String url = "";
//      initstmt();
//      url = "?type=createtable&tableName=amazoncommenttable";
//      request.add(url);
//      url = "?type=insert&tableName=amazoncommenttable&id="+comment+"&col=ifuseful&val=true";
//      request.add(url);
//      url = "?type=read&tableName=amazoncommenttable&key="+comment+"&col=ifuseful";
//      request.add(url);
//      url = "?type=delete&tableName=amazoncommenttable&id="+comment;
//      request.add(url);
//      cleanstmt();
//    }
//    public void url_mongodb_amazon_writefollowread()
//    {
//
//      //46
//      String url = "";
//      initstmt();
//      //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//      //request.add(url);
//      Calendar cal = Calendar.getInstance();
//      Date date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=createtable&tableName=amazoncommoditytable";
//      request.add(url);
//    //  url = "?type=createtable&tableName=amazoncarttable";
//     // request.add(url);
//      url = "?type=insert2&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+time;
//      request.add(url);
//	url = "?type=read&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
//      request.add(url);
//	url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri2+"&time="+time;
//      request.add(url);
//        url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
//	request.add(url);
//     /* cal = Calendar.getInstance();
//      date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=insert&tableName=amazoncarttable&id="+user+"&col=liketweet&val="+commodity+"&time="+time;
//      request.add(url);
//      url = "?type=read&key="+user+"&col=liketweet&tableName=amazoncarttable";
//      request.add(url);
//      url = "?type=read&key="+commodity+"&col=liketweet&tableName=amazoncommoditytable";
//      request.add(url);
//      url = "?type=delete&id="+user+"&tableName=amazoncarttable";
//      request.add(url);*/
//      url = "?type=delete&id="+commodity+"tableName=amazoncommoditytable";
//      request.add(url);
//      cleanstmt();
//    }
//    public void url_mongodb_amazon_monotonicread()
//    {
//      //47
//      String url = "";
//      initstmt();
//      url = "?type=createtable&tableName=amazoncommoditytable";
//      request.add(url);
//
//      Calendar cal = Calendar.getInstance();
//      Date date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+time;
//      request.add(url);
//      url = "?type=read2&key="+user+"&col=descri&tableName=amazoncommoditytable";
//      request.add(url);
//      cal = Calendar.getInstance();
//      date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=update&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri2+"&time="+time;
//      request.add(url);
//      url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
//      request.add(url);
//     /* try{
//        Thread.sleep(50);
//      }catch(Exception e){
//        e.printStackTrace();
//      }*/
//      url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
//      request.add(url);
//      // url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//      //request.add(url);
//      cleanstmt();
//    }
//    public void url_mongodb_amazon_monotonicwrite()
//    {
//      //48
//      String url = "";
//      initstmt();
//      url = "?type=createtable&tableName=amazoncommoditytable";
//      request.add(url);
//      //-----client b write 1------------------------------------
//      cal = Calendar.getInstance();
//      date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=insert&tableName=amazoncommoditytable="+commodity+"&col=descri&val="+descri+"&time="+time;
//      request.add(url);
//      //-----client b write 2------------------------------------
//      cal = Calendar.getInstance();
//      date = cal.getTime();
//      time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//      url = "?type=update&tableName=amazoncommoditytable&id="+commodity+"&col=liketweet&val="+descri2+"&time="+time;
//      request.add(url);
//      //-----client a read 1------------------------------------
//      url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
//      request.add(url);
//      //-----client a read 2------------------------------------
//      url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
//      request.add(url);
//      //url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//      //request.add(url);
//      //url = "?type=cleanup";
//      //request.add(cleanstmt());
//      cleanstmt();
//    }
//
//
//
//
//
//
//
//
//
//
//
//  public void url_mysql_twitter_readyouwrite()
//  {
//    //49
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=twitterusertable");
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//    request.add(url);
//    url = "?type=read&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    url = "?type=delete&id="+user+"&tableName=twitterusertable";
//    request.add(url);
//    cleanstmt();
//
//  }
//  public void url_mysql_twitter_writefollowread()
//  {
//    //50
//        /* ?type=createtable&tableName=k123.utable1&fields=id,text,liketweet,text,retweet,text,commenttweet,text&pk=id
//                    ?type=insert&tableName=k123.utable1&keyrow=id&key=user1&cf=1&col=liketweet&val=tweet1
//                    ?type=insert&tableName=k123.utable1&keyrow=id&key=user2&cf=1&col=liketweet&val=tweet1
//                    ?type=read&key=user1&pk=liketweet&tableName=k123.utable1
//                    ?type=delete&key=user1&col=liketweet&keyrow=id&cf=1&tableName=k123.utable1
//                   ?type=delete&key=user2&col=liketweet&keyrow=id&cf=1&tableName=k123.utable1
//         */
//    //看见别人点赞
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=twitterusertable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert2&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//    request.add(url);
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=read&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//    request.add(url);
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//
//    url = "?type=delete&id="+user+"&tableName=twitterusertable";
//    request.add(url);
//
//    cleanstmt();
//  }
//  public void url_mysql_twitter_monotonicread()
//  {
//    //51
//        /*?type=createtable&tableName=k123.utable1&fields=id,text,liketweet,text,retweet,text,commenttweet,text&pk=id
//            ?type=insert&tableName=k123.utable1&keyrow=id&key=user1&cf=1&col=liketweet&val=tweet1 #第一次插入
//            ?type=read&key=user1&pk=liketweet&tableName=k123.utable1 #第一次读取
//            ?type=insert&tableName=k123.utable1&keyrow=id&key=user1&cf=1&col=liketweet&val=tweet2 #第二次、更新
//            ?type=read&key=user1&pk=liketweet&tableName=k123.utable1 #第二次读取
//            ?type=read&key=user1&pk=liketweet&tableName=k123.utable1 #第三次读取
//            ?type=delete&key=user1&col=liketweet&keyrow=id&cf=1&tableName=k123.utable1
//        * */
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=twitterusertable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//    request.add(url);
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//    request.add(url);
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    /*try{
//      Thread.sleep(50);
//    }catch(Exception e){
//      e.printStackTrace();
//    }*/
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    // url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//    //request.add(url);
//    cleanstmt();
//
//  }
//  public void url_mysql_twitter_monotonicwrite()
//  {
//    //52
//    String url ="";
//    //String url = "";initstmt();
//    // request.add(initstmt());
//    initstmt();
//    //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=twitterusertable");
//    //-----client b write 1------------------------------------
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//    request.add(url);
//    //-----client b write 2------------------------------------
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//    request.add(url);
//    //-----client a read 1------------------------------------
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    //-----client a read 2------------------------------------
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    //url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//    //request.add(url);
//    //url = "?type=cleanup";
//    //request.add(cleanstmt());
//    cleanstmt();
//  }
//  public void url_mysql_amazon_readyourwrite()
//  {
//    //53
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.amazoncommenttable&fields=id,text,descri,text,user,text,ifuseful,text,belongsto,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=amazoncommenttable");
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=amazoncommenttable&id="+comment+"&col=ifuseful&val=true&time="+time;
//    request.add(url);
//    url = "?type=read&key="+comment+"&col=ifuseful&tableName=amazoncommenttable";
//    request.add(url);
//    url = "?type=delete&id="+comment+"&tableName=amazoncommenttable";
//    request.add(url);
//    cleanstmt();
//
//  }
//  public void url_mysql_amazon_writefollowread()
//  {
//    //54
//    /* read des ,add into cart,then read the descri of commodity again
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id";
//    //request.add(url);
//    //url = "?type=createtable&tableName=k123.amazoncarttable&fields=id,text,commodity,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=amazoncommoditytable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert2&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+time;
//    request.add(url);
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//	url = "?type=read&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
//    request.add(url);
//    url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri2+"&time="+time;
//    request.add(url);
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
//    request.add(url);
//
//    url = "?type=delete&id="+commodity+"&tableName=amazoncommoditytable";
//    request.add(url);
//
//    cleanstmt();     2018-10-28*/
//    String url = "";
//    initstmt();
//    createtablestmt("?type=createtable&tableName=amazoncommoditytable2");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert2&tableName=amazoncommoditytable2&id="+commodity+"&col=descri&val="+descri+"&time="+time;
//    request.add(url);
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=read&key="+commodity+"&col=descri&tableName=amazoncommoditytable2";
//    request.add(url);
//    url = "?type=insert&tableName=amazoncommoditytable2&id="+commodity2+"&col=descri&val="+descri2+"&time="+time;
//    request.add(url);
//    url = "?type=read2&key="+commodity2+"&col=descri&tableName=amazoncommoditytable2";
//    request.add(url);
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable2";
//    request.add(url);
//    url = "?type=delete&id="+commodity+"&tableName=amazoncommoditytable2";
//    request.add(url);
//    url = "?type=delete&id="+commodity2+"&tableName=amazoncommoditytable2";
//    request.add(url);
//    cleanstmt();
//
//
//  }
//  public void url_mysql_amazon_monotonicread()
//  {
//    //55
//    /*user read a descri(r1),add it to cart,then the seller change the descri and user read it (r2),fresh the web page(r3)*/
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=amazoncommoditytable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+time;
//    request.add(url);
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";//r1
//    request.add(url);
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri2+"&time="+time;//这个写应该在其他client？
//    request.add(url);
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";//r2
//    request.add(url);
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";//r3
//    request.add(url);
//    //url = "?type=delete&key="+commodity+"&col=descri&keyrow=id&tableName=amazoncommoditytable";
//    // request.add(url);
//    cleanstmt();
//  }
//  public void url_mysql_amazon_monotonicwrite()
//  {
//    //56
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=amazoncommoditytable");
//    //client a write 1
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+time;
//    request.add(url);
//    //client a write 2
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri2+"&time="+time;
//    request.add(url);
//
//    //client b read 1
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";//r1
//    request.add(url);
//    //client b read 2
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";//r2
//    request.add(url);
//    // url = "?type=delete&key="+commodity+"&col=descri&keyrow=id&tableName=amazoncommoditytable";
//    // request.add(url);
//    cleanstmt();
//  }
//  public void url_mysql_flickr_readyourwrite()
//  {
//    //57
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=flickrpicturetable");
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;//edit picture:rotate
//    request.add(url);
//    url = "?type=read&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//    url = "?type=delete&id="+picture+"&tableName=flickrpicturetable";
//    request.add(url);
//    cleanstmt();
//  }
//  public void url_mysql_flickr_writefollowread()
//  {
//
//    //58
//    //read the content of picture,then comment,if one can read this comment but cannot read this content,---->violate
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=flickrpicturetable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert2&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;
//    request.add(url);
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//	url = "?type=read&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//    url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content2+"&time="+time;
//    request.add(url);
//
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//
//    url = "?type=delete&id="+picture+"&tableName=flickrpicturetable";
//    request.add(url);
//    cleanstmt();
//  }
//  public void url_mysql_flickr_monotonicread()
//  {
//    //59
//    //client b insert (w1), client a read it (r1) ,client b edit it(w2),then client a read it  (r2),fresh web page and client a read it again(r3)
//    //-----client b--------w1----------------
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=flickrpicturetable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;
//    request.add(url);
//
//    //-------client a-----r1--------------------
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//
//    //-------client b -----edit w2-------------
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=content&val="+content2+"&time="+time;
//    request.add(url);
//
//    //----client a ----read r2--------------------
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//    //---client a -----read r3------
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//
//    //  url = "?type=delete&key="+picture+"&col=content&keyrow=id&tableName=flickrpicturetable";
//    //  request.add(url);
//    cleanstmt();
//  }
//  public void url_mysql_flickr_monotonicwrite()
//  {
//    //60
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=flickrpicturetable");
//    //-------client a-----w1------------------
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;
//    request.add(url);
//    //-------client a ----- w2-------------
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content2+"&time="+time;
//    request.add(url);
//
//    //----client b ----read r1--------------------
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//    //---client b -----read r2------
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//
//    //  url = "?type=delete&key="+picture+"&col=content&keyrow=id&tableName=flickrpicturetable";
//    //  request.add(url);
//    cleanstmt();
//  }
//
//
//
//
//
//
//
//
//
//  public void url_postgres_twitter_readyouwrite()
//  {
//    //61
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=twitterusertable");
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//    request.add(url);
//    url = "?type=read&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    url = "?type=delete&id="+user+"&tableName=twitterusertable";
//    request.add(url);
//    cleanstmt();
//
//  }
//  public void url_postgres_twitter_writefollowread()
//  {
//    //62
//        /* ?type=createtable&tableName=k123.utable1&fields=id,text,liketweet,text,retweet,text,commenttweet,text&pk=id
//                    ?type=insert&tableName=k123.utable1&keyrow=id&key=user1&cf=1&col=liketweet&val=tweet1
//                    ?type=insert&tableName=k123.utable1&keyrow=id&key=user2&cf=1&col=liketweet&val=tweet1
//                    ?type=read&key=user1&pk=liketweet&tableName=k123.utable1
//                    ?type=delete&key=user1&col=liketweet&keyrow=id&cf=1&tableName=k123.utable1
//                   ?type=delete&key=user2&col=liketweet&keyrow=id&cf=1&tableName=k123.utable1
//         */
//    //看见别人点赞
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=twitterusertable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert2&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//    request.add(url);
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
// 	url = "?type=read&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//	url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//    request.add(url);
//
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    url = "?type=delete&id="+user+"&tableName=twitterusertable";
//    request.add(url);
//
//    cleanstmt();
//  }
//  public void url_postgres_twitter_monotonicread()
//  {
//    //63
//        /*?type=createtable&tableName=k123.utable1&fields=id,text,liketweet,text,retweet,text,commenttweet,text&pk=id
//            ?type=insert&tableName=k123.utable1&keyrow=id&key=user1&cf=1&col=liketweet&val=tweet1 #第一次插入
//            ?type=read&key=user1&pk=liketweet&tableName=k123.utable1 #第一次读取
//            ?type=insert&tableName=k123.utable1&keyrow=id&key=user1&cf=1&col=liketweet&val=tweet2 #第二次、更新
//            ?type=read&key=user1&pk=liketweet&tableName=k123.utable1 #第二次读取
//            ?type=read&key=user1&pk=liketweet&tableName=k123.utable1 #第三次读取
//            ?type=delete&key=user1&col=liketweet&keyrow=id&cf=1&tableName=k123.utable1
//        * */
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=twitterusertable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//    request.add(url);
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//    request.add(url);
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    /*try{
//      Thread.sleep(50);
//    }catch(Exception e){
//      e.printStackTrace();
//    }*/
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    // url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//    //request.add(url);
//    cleanstmt();
//
//  }
//  public void url_postgres_twitter_monotonicwrite()
//  {
//    //64
//    String url ="";
//    //String url = "";initstmt();
//    // request.add(initstmt());
//    initstmt();
//    //url = "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=twitterusertable");
//    //-----client b write 1------------------------------------
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+time;
//    request.add(url);
//    //-----client b write 2------------------------------------
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+time;
//    request.add(url);
//    //-----client a read 1------------------------------------
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    //-----client a read 2------------------------------------
//    url = "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
//    request.add(url);
//    //url = "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
//    //request.add(url);
//    //url = "?type=cleanup";
//    //request.add(cleanstmt());
//    cleanstmt();
//  }
//  public void url_postgres_amazon_readyourwrite()
//  {
//    //65
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.amazoncommenttable&fields=id,text,descri,text,user,text,ifuseful,text,belongsto,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=amazoncommenttable");
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=amazoncommenttable&id="+comment+"&col=ifuseful&val=true&time="+time;
//    request.add(url);
//    url = "?type=read&key="+comment+"&col=ifuseful&tableName=amazoncommenttable";
//    request.add(url);
//    url = "?type=delete&id="+comment+"&tableName=amazoncommenttable";
//    request.add(url);
//    cleanstmt();
//
//  }
//  public void url_postgres_amazon_writefollowread()
//  {
//    //66
//    /* read des ,add into cart,then read the descri of commodity again
//     */
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id";
//    //request.add(url);
//    //url = "?type=createtable&tableName=k123.amazoncarttable&fields=id,text,commodity,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=amazoncommoditytable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert2&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+time;
//    request.add(url);
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//	url = "?type=read&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
//    request.add(url);
//	url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri2;
//    request.add(url);
//
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
//    request.add(url);
//    url = "?type=delete&id="+commodity+"&tableName=amazoncommoditytable";
//
//    request.add(url);
//    cleanstmt();
//  }
//  public void url_postgres_amazon_monotonicread()
//  {
//    //67
//    /*user read a descri(r1),add it to cart,then the seller change the descri and user read it (r2),fresh the web page(r3)*/
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=amazoncommoditytable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+time;
//    request.add(url);
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";//r1
//    request.add(url);
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri2+"&time="+time;//这个写应该在其他client？
//    request.add(url);
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";//r2
//    request.add(url);
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";//r3
//    request.add(url);
//    //url = "?type=delete&key="+commodity+"&col=descri&keyrow=id&tableName=amazoncommoditytable";
//    // request.add(url);
//    cleanstmt();
//  }
//  public void url_postgres_amazon_monotonicwrite()
//  {
//    //68
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=amazoncommoditytable");
//    //client a write 1
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+time;
//    request.add(url);
//    //client a write 2
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri2+"&time="+time;
//    request.add(url);
//
//    //client b read 1
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";//r1
//    request.add(url);
//    //client b read 2
//    url = "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";//r2
//    request.add(url);
//    // url = "?type=delete&key="+commodity+"&col=descri&keyrow=id&tableName=amazoncommoditytable";
//    // request.add(url);
//    cleanstmt();
//  }
//  public void url_postgres_flickr_readyourwrite()
//  {
//    //69
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=flickrpicturetable");
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;//edit picture:rotate
//    request.add(url);
//    url = "?type=read&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//    url = "?type=delete&id="+picture+"&tableName=flickrpicturetable";
//    request.add(url);
//    cleanstmt();
//  }
//  public void url_postgres_flickr_writefollowread()
//  {
//
//    //70
//    //read the content of picture,then comment,if one can read this comment but cannot read this content,---->violate
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=flickrpicturetable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert2&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;
//    request.add(url);
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//	url = "?type=read&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//	url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content2;
//    request.add(url);
//
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//
//    url = "?type=delete&id="+picture+"&tableName=flickrpicturetable";
//    request.add(url);
//    cleanstmt();
//  }
//  public void url_postgres_flickr_monotonicread()
//  {
//    //71
//    //client b insert (w1), client a read it (r1) ,client b edit it(w2),then client a read it  (r2),fresh web page and client a read it again(r3)
//    //-----client b--------w1----------------
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=flickrpicturetable");
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;
//    request.add(url);
//
//    //-------client a-----r1--------------------
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//
//    //-------client b -----edit w2-------------
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=content&val="+content2+"&time="+time;
//    request.add(url);
//
//    //----client a ----read r2--------------------
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//    //---client a -----read r3------
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//
//    //  url = "?type=delete&key="+picture+"&col=content&keyrow=id&tableName=flickrpicturetable";
//    //  request.add(url);
//    cleanstmt();
//  }
//  public void url_postgres_flickr_monotonicwrite()
//  {
//    //72
//    String url = "";
//    initstmt();
//    //url = "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
//    //request.add(url);
//    createtablestmt("?type=createtable&tableName=flickrpicturetable");
//    //-------client a-----w1------------------
//    Calendar cal = Calendar.getInstance();
//    Date date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+time;
//    request.add(url);
//    //-------client a ----- w2-------------
//    cal = Calendar.getInstance();
//    date = cal.getTime();
//    time = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date);
//    url = "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content2+"&time="+time;
//    request.add(url);
//
//    //----client b ----read r1--------------------
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//    //---client b -----read r2------
//    url = "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
//    request.add(url);
//
//    //  url = "?type=delete&key="+picture+"&col=content&keyrow=id&tableName=flickrpicturetable";
//    //  request.add(url);
//    cleanstmt();
//  }*/
}
