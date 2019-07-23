package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.TwitterRYW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CassandraTwitterRYW extends TwitterRYW {
     int typid = 1;
     int loopStartIndex = 2;

     List<String> request = new ArrayList<>();

    public CassandraTwitterRYW()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return "?type=createtable&tableName=k123.twitterusertable&fields=id,text,tweetid,text,liketweet,text,retweet,text,commenttweet,text,time,text&pk=id";
    }
    public String read()
    {
        return "?type=read&key="+user+"&pk=liketweet&tableName=twitterusertable";
    }
    public String insert()
    {
        return "?type=insert&tableName=twitterusertable&keyrow=id&key="+user+"&col=liketweet&val="+tweet+"&time="+
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
