package com.operation;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class AtlasOp implements Operation
{
    private static MongoClient mongoClient;
    private static MongoClient mongoClient2;
    private static String readPreference = "mongodb_readpreference";
    private static String writeConcern = "mongodb_writeconcern";
    private static String readConcern = "mongodb_readconcern";
    private static MongoDatabase db;
    private static MongoDatabase db2;
    private static Properties properties = new Properties();
    private static String publicIP1 = "publicIP1";
    private static String publicIP2 = "publicIP2";
    private static String publicIP3 = "publicIP3";
    private static String privateIP1 = "privateIP1";
    private static String privateIP2 = "privateIP2";
    private static String privateIP3 = "privateIP3";
    private static String database = "sample_supplies";
    private static String port = "mongodb_port";
//curl -i -u "zhaole:ngfuizbm" --digest "https://cloud.mongodb.com/api/atlas/v1.0/groups/0/clusters/caulsaltest/fts/indexes/sample_mflix/comments"
    //publickey = ngfuizbm
    //privatekey = dc01c794-cd52-4d55-9c5c-da179ea58bc3
    @Override
    public String init() throws IOException
    {
        properties.load(new FileInputStream("src/main/resources/conf.properties"));
        WriteConcern wc = WriteConcern.W1;
        ReadConcern rc = ReadConcern.DEFAULT;
        String x1="";
        String x2="";
        if(properties.getProperty(writeConcern).equals("w1"))
        {
            wc = WriteConcern.W1;
        }else if(properties.getProperty(writeConcern).equals("w2"))
        {
            wc = WriteConcern.W2;
        }else if(properties.getProperty(writeConcern).equals("w3"))
        {
            wc = WriteConcern.W3;
        }else if(properties.getProperty(writeConcern).equals("majority"))
        {
            wc = WriteConcern.MAJORITY;
            x1="wc=majority";
        }

        if(properties.getProperty(readConcern).equals("majority"))
        {
            x2="rc=majority";
            rc = ReadConcern.MAJORITY;
        }else if(properties.getProperty(readConcern).equals("linearizable"))
        {
            rc = ReadConcern.LINEARIZABLE;
        }else if(properties.getProperty(readConcern).equals("local"))
        {
            rc = ReadConcern.LOCAL;
        }


        try{
            // 连接到 mongodb 服务
          /*  List<ServerAddress> addresses = new ArrayList<>();
            ServerAddress address1 = new ServerAddress(properties.getProperty(privateIP1) ,Integer.parseInt(properties.getProperty(port)));
            ServerAddress address2 = new ServerAddress(properties.getProperty(privateIP2) , Integer.parseInt(properties.getProperty(port)));
            ServerAddress address3 = new ServerAddress(properties.getProperty(privateIP3) , Integer.parseInt(properties.getProperty(port)));
            addresses.add(address1);
            addresses.add(address2);
            addresses.add(address3);
            mongoClient = new MongoClient(addresses);
            mongoClient2 = new MongoClient(addresses);*/

            MongoClientURI uri = new MongoClientURI(
                    "mongodb+srv://zhaole:13061076zl.@causaltest-ocqgv.mongodb.net/test?retryWrites=true&w=majority");
            mongoClient = new MongoClient(uri);
            mongoClient2 = new MongoClient(uri);



            db = mongoClient.getDatabase(properties.getProperty(database))
                    .withWriteConcern(wc)
                    .withReadConcern(rc);
            db2 = mongoClient2.getDatabase(properties.getProperty(database))
                    .withWriteConcern(wc)
                    .withReadConcern(rc);
            //db.createCollection("xxxtable");

            System.out.println("Connect to database successfully"+db.getName());

        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return x1+x2;
    }
    @Override
    public  void cleanup()
    {
        try {
            mongoClient.close();
            mongoClient2.close();
        } catch (Exception e1)
        {
            System.err.println("Could not close MongoDB connection pool: "+e1.toString());
            e1.printStackTrace();
            return;
        } finally
        {
            db = null;
            db2 = null;
            mongoClient = null;
            mongoClient2 = null;
        }
    }
    public  String createTable(String tableName)
    {

        if(tableName.equals("twitterusertable"))
        {
            System.out.println("mongodb createtable "+db.getName());
            if(db.listCollectionNames().into(new ArrayList<String>()).contains(tableName)==false)
            {
                db.createCollection(tableName);
            }
        }
        if(tableName.equals( "amazoncommenttable"))
        {
            if(db.listCollectionNames().into(new ArrayList<String>()).contains(tableName)==false)
            {
                db.createCollection(tableName);
            }
        }
        if(tableName.equals( "amazoncommoditytable"))
        {
            if(db.listCollectionNames().into(new ArrayList<String>()).contains(tableName)==false)
            {
                db.createCollection(tableName);
            }
        }
        if(tableName.equals("flickrpicturetable"))
        {
            if(db.listCollectionNames().into(new ArrayList<String>()).contains(tableName)==false)
            {
                db.createCollection(tableName);
            }
        }
        if(tableName.equals("amazoncarttable"))
        {
            if(db.listCollectionNames().into(new ArrayList<String>()).contains(tableName)==false)
            {
                db.createCollection(tableName);
            }
        }
        System.out.println("create mongo table(collection ) successfully");
        return "mongo creatTable complete";
    }

    @Override
    public  String read(String tableName, String id,Set<String> fields, int clientFlag)throws IOException
    {
        properties.load(new FileInputStream("src/main/resources/conf.properties"));
        ReadPreference rp = ReadPreference.nearest();
        if(properties.getProperty(readPreference).equals("nearest"))
        {
            rp = ReadPreference.nearest();
        }else if(properties.getProperty(readPreference).equals("primary"))
        {
            rp = ReadPreference.primary();
        }
        ///        else if(properties.getProperty(readPreference) == "primarypreferred")
        //        {
        //            rp = ReadPreference.primaryPreferred();
        //        }
        else if(properties.getProperty(readPreference).equals("secondary"))
        {
            rp = ReadPreference.secondary();
        }


        MongoCollection<Document> collection;
        if(clientFlag == 1)
        {
            collection= db.getCollection(tableName);
        }else
        {
            collection = db2.getCollection(tableName);
        }
        String r = "";
        try
        {//
            Document mydoc = collection.withReadPreference(rp).find(eq("_id",id)).first();
            String key = null;
            for (String col : fields)
            {
                key = col;
            }
            r = mydoc.getString(key);
            System.out.println(r);
        }catch (Exception e)
        {
            e.printStackTrace();
            return "readerror";
        }
        return r;
    }
    public static String read2(String tableName, String id,String key)
    {
        MongoCollection<Document> collection = db2.getCollection(tableName);
        /**/

        String r = "";
        try{//
            Document mydoc = collection.withReadPreference(ReadPreference.secondary()).find(eq("_id",id)).first();
            //System.out.println(mydoc);
            r = mydoc.getString(key);

        }catch (Exception e){
            e.printStackTrace();
            return "readerror";
        }
        return r;
    }
    public  String update(String tableName,String id,String key,String value)
    {
        String r ="";

        MongoCollection<Document> collection = db.getCollection(tableName);
        collection.updateOne(eq("_id",id),set(key,value) );
        return "mongo update complete";
    }
    public  String update2(String tableName,String id,String key,String value)
    {
        String r ="";

        MongoCollection<Document> collection = db2.getCollection(tableName);
        collection.updateOne(eq("_id",id),set(key,value) );
        return "mongo update complete";
    }
    @Override
    public String insert(String tableName,String id,String key,String value,String nul,int clientFlag)
    {
        String r ="";
        MongoCollection<Document> collection;
        if(clientFlag == 1){
            collection = db.getCollection(tableName);
        }
        else{
            collection = db2.getCollection(tableName);
        }
        if(tableName.equals("twitterusertable"))
        {
            Document _twitterusertable = new Document("_id",id)
                    .append("tweetid","tweetid_default")
                    .append("liketweet","liketweet_default")
                    .append("retweet","retweet_default")
                    .append("commenttweet","commenttweet_default")
                    .append("time","time_default");
            collection.insertOne(_twitterusertable);
            //  System.out.println("^^^^^^^^^^");
        }
        if(tableName.equals("amazoncommenttable"))
        {
            Document _amazoncommenttable = new Document("_id",id)
                    .append("descri","descri_default")
                    .append("user","user_default")
                    .append("ifuseful","ifuserful_default")
                    .append("belongsto","belongsto_default")
                    .append("time","time_default");
            collection.insertOne(_amazoncommenttable);
        }
        if(tableName.equals("amazoncommoditytable"))
        {
            Document _amazoncommoditytable = new Document("_id",id)
                    .append("price","price_default")
                    .append("descri","descri_default")
                    .append("comment","comment_default")
                    .append("question","question_default")
                    .append("time","time_default");
            collection.insertOne(_amazoncommoditytable);
        }
        if(tableName.equals("amazoncarttable"))
        {
            Document _twitterusertable = new Document("_id",id)
                    .append("commodity","commodity_default");
            collection.insertOne(_twitterusertable);
        }
        if(tableName.equals("flickrpicturetable"))
        {
            Document _flickrpicturetable = new Document("_id",id)
                    .append("content","content_default")
                    .append("permit","permit_default")
                    .append("likeduser","likeduserdefault")
                    .append("album","album_default")
                    .append("location","location_default")
                    .append("comment","comment_default")
                    .append("tag","tag_default")
                    .append("time","time_default");
            collection.insertOne(_flickrpicturetable);
        }
        collection.updateOne(eq("_id",id),set(key,value) );
        return "mongodb insert complete";
    }
    public static String insert2(String tableName,String id,String key,String value)
    {
        String r ="";
        MongoCollection<Document> collection = db2.getCollection(tableName);
        if(tableName.equals("twitterusertable"))
        {
            Document _twitterusertable = new Document("_id",id)
                    .append("tweetid","tweetid_default")
                    .append("liketweet","liketweet_default")
                    .append("retweet","retweet_default")
                    .append("commenttweet","commenttweet_default")
                    .append("time","time_default");
            collection.insertOne(_twitterusertable);
            //  System.out.println("^^^^^^^^^^");
        }
        if(tableName.equals("amazoncommenttable"))
        {
            Document _amazoncommenttable = new Document("_id",id)
                    .append("descri","descri_default")
                    .append("user","user_default")
                    .append("ifuseful","ifuserful_default")
                    .append("belongsto","belongsto_default")
                    .append("time","time_default");
            collection.insertOne(_amazoncommenttable);
        }
        if(tableName.equals("amazoncommoditytable"))
        {
            Document _amazoncommoditytable = new Document("_id",id)
                    .append("price","price_default")
                    .append("descri","descri_default")
                    .append("comment","comment_default")
                    .append("question","question_default")
                    .append("time","time_default");
            collection.insertOne(_amazoncommoditytable);
        }
        if(tableName.equals("amazoncarttable"))
        {
            Document _twitterusertable = new Document("_id",id)
                    .append("commodity","commodity_default");
            collection.insertOne(_twitterusertable);
        }
        if(tableName.equals("flickrpicturetable"))
        {
            Document _flickrpicturetable = new Document("_id",id)
                    .append("content","content_default")
                    .append("permit","permit_default")
                    .append("likeduser","likeduserdefault")
                    .append("album","album_default")
                    .append("location","location_default")
                    .append("comment","comment_default")
                    .append("tag","tag_default")
                    .append("time","time_default");
            collection.insertOne(_flickrpicturetable);
        }
        collection.updateOne(eq("_id",id),set(key,value) );
        return "mongodb insert complete";
    }
    @Override
    public  String delete(String tableName,String id,String nul)
    {
        MongoCollection<Document> collection = db.getCollection(tableName);

        collection.deleteOne(Filters.eq("_id",id));
        return "mongo delete complete";
    }

}
