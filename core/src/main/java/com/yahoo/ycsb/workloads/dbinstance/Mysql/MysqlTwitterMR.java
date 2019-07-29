package com.yahoo.ycsb.workloads.dbinstance.Mysql;

import com.yahoo.ycsb.workloads.testcase.TwitterMR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MysqlTwitterMR extends TwitterMR
{
    int typid = 51;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public MysqlTwitterMR()
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
        return "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
    }
    public String insert()
    {
        return "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+
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
