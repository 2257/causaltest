package com.yahoo.ycsb.workloads.dbinstance.Hbase;

import com.yahoo.ycsb.workloads.testcase.TwitterRYW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HbaseTwitterRYW extends TwitterRYW
{
    int typid = 13;
    int loopStartIndex = 2;

    List<String> request = new ArrayList<>();

    public HbaseTwitterRYW()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return "?type=createtable&tableName=twitterusertable&CFName=id,tweetid,liketweet,retweet,commenttweet,time";
    }
    public String read()
    {
        return "?type=read&key="+tweet+"&fields=liketweet&tableName=twitterusertable";
    }
    public String insert()
    {
        return "?type=insert&tableName=twitterusertable&keyrow="+tweet+"&col=liketweet&val="+tweet+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key="+tweet+"&tableName=twitterusertable&col=liketweet";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
