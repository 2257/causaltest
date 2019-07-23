package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.TwitterMW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MysqlTwitterMW extends TwitterMW
{
    int typid = 52;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public MysqlTwitterMW()
    {

        this.request = super.request;
    }

    public String init()
    {
        return "?type=init";
    }
    public String createtable()
    {
        return  "?type=createtable&tableName=twitterusertable";
    }
    public String read()
    {
        return "?type=read2&key="+moment+"&fields=content&tableName=twittermomenttable";
    }
    public String insert()
    {
        return "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=twittermomenttable&keyrow="+moment+"&col=content&val="+content2+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key="+moment+"&tableName=twittermomenttable&col=content";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
