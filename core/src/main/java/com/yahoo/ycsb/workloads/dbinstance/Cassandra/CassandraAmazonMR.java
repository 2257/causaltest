package com.yahoo.ycsb.workloads.dbinstance.Cassandra;

import com.yahoo.ycsb.workloads.testcase.AmazonMR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CassandraAmazonMR extends AmazonMR
{

    int typid = 7;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public CassandraAmazonMR()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return "?type=createtable&tableName=k123.amazoncommoditytable&fields=id,text,price,text,descri,text,comment,text,question,text,time,text&pk=id";
    }
    public String read()
    {
        return "?type=read2&key="+commodity+"&pk=descri&tableName=amazoncommoditytable";
    }
    public String insert()
    {
        return "?type=insert&tableName=amazoncommoditytable&keyrow=id&key="+commodity+"&col=descri&val="+descri+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=amazoncommoditytable&keyrow=id&key="+commodity+"&col=descri&val="+descri2+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key="+commodity+"&col=descri&keyrow=id&tableName=amazoncommoditytable";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
