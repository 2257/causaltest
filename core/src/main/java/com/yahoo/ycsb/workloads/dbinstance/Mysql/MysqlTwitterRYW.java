package com.yahoo.ycsb.workloads.dbinstance.Mysql;

import com.yahoo.ycsb.workloads.testcase.TwitterRYW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MysqlTwitterRYW extends TwitterRYW
{
    int typid = 49;
    int loopStartIndex = 2;

    List<String> request = new ArrayList<>();

    public MysqlTwitterRYW()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return "?type=createtable&tableName=twitterusertable";
    }
    public String read()
    {
        return  "?type=read&key="+user+"&col=liketweet&tableName=twitterusertable";
    }
    public String insert()
    {
        return "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+
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
