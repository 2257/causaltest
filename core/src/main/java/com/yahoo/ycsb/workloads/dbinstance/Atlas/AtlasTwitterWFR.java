package com.yahoo.ycsb.workloads.dbinstance.Atlas;

import com.yahoo.ycsb.workloads.testcase.TwitterWFR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AtlasTwitterWFR extends TwitterWFR
{
    int typid = 38;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public AtlasTwitterWFR()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return  "?type=createtable&tableName=twitterusertable";
    }
    public String read()
    {
        return "?type=read&key="+user+"&col=liketweet&tableName=twitterusertable";
    }
    public String read2()
    {
        return "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
    }
    public String insert()
    {
        return "?type=insert2&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=update&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&id="+user+"&tableName=twitterusertable";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
