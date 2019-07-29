package com.yahoo.ycsb.workloads.dbinstance.Mongodb;

import com.yahoo.ycsb.workloads.testcase.AmazonMR;
import com.yahoo.ycsb.workloads.testcase.AmazonWFR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MongodbAmazonWFR extends AmazonWFR
{
    int typid = 46;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public MongodbAmazonWFR()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return  "?type=createtable&tableName=amazoncommoditytable";
    }
    public String read()
    {
        return "?type=read&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
    }
    public String read2()
    {
        return "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
    }
    public String insert()
    {
        return "?type=insert2&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert2&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&id="+commodity+"tableName=amazoncommoditytable";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
