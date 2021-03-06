package com.yahoo.ycsb.workloads.dbinstance.Cassandra;

import com.yahoo.ycsb.workloads.testcase.TwitterMW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CassandraTwitterMW extends TwitterMW
{

    int typid = 4;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public CassandraTwitterMW()
    {
        this.request = super.request;
    }

    public String init()
    {
        return "?type=init";
    }
    public String createtable()
    {
       return  "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
    }
    public String read()
    {
        return "?type=read2&key="+user+"&pk=liketweet&tableName=twitterusertable";
    }
    public String insert()
    {
        return "?type=insert&tableName=twitterusertable&keyrow=id&key="+user+"&col=liketweet&val="+tweet+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=twitterusertable&keyrow=id&key="+user+"&col=liketweet&val="+tweet2+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key="+user+"&col=liketweet&keyrow=id&tableName=twitterusertable";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
