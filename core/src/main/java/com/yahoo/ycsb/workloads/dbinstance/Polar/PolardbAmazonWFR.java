package com.yahoo.ycsb.workloads.dbinstance.Polar;

import com.yahoo.ycsb.workloads.testcase.AmazonWFR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PolardbAmazonWFR extends AmazonWFR
{
    int typid = 54;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public PolardbAmazonWFR()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return "?type=createtable&tableName=amazoncommoditytable2";
    }
    public String read()
    {
        return "?type=read&key="+commodity+"&col=descri&tableName=amazoncommoditytable2";
    }
    public String read2()
    {
        return "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable2";
    }
    public String insert()
    {
        return "?type=insert2&tableName=amazoncommoditytable2&id="+commodity+"&col=descri&val="+descri+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=amazoncommoditytable2&id="+commodity+"&col=descri&val="+descri2+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&id="+commodity+"&tableName=amazoncommoditytable2";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
