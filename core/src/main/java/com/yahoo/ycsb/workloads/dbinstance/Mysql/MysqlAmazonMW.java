package com.yahoo.ycsb.workloads.dbinstance.Mysql;

import com.yahoo.ycsb.workloads.testcase.AmazonMW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MysqlAmazonMW extends AmazonMW
{
    int typid = 56;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public MysqlAmazonMW()
    {

        this.request = super.request;
    }

    public String init()
    {
        return "?type=init";
    }
    public String createtable()
    {
        return  "?type=createtable&tableName=amazoncommoditytable";
    }
    public String read()
    {
        return "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
    }
    public String insert()
    {
        return "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri2+"&time="+
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
