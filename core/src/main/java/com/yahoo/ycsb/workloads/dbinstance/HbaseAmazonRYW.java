package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.AmazonRYW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HbaseAmazonRYW extends AmazonRYW
{
    int typid = 17;
    int loopStartIndex = 2;

    List<String> request = new ArrayList<>();

    public HbaseAmazonRYW()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return "?type=createtable&tableName=amazoncommoditytable2&CFName=id,price,descri,question,comment,time";
    }
    public String read()
    {
        return "?type=read&key="+commodity+"&fields=descri&tableName=amazoncommoditytable2";
    }
    public String insert()
    {
        return "?type=insert&tableName=amazoncommoditytable2&keyrow="+commodity+"&col=descri&val="+descri+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key="+commodity+"&tableName=amazoncommoditytable2&col=descri";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
