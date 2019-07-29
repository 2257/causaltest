package com.yahoo.ycsb.workloads.dbinstance.Cassandra;

import com.yahoo.ycsb.workloads.testcase.AmazonRYW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CassandraAmazonRYW extends AmazonRYW
{
    int typid = 5;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public CassandraAmazonRYW()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return "?type=createtable&tableName=k123.amazoncommenttable&fields=id,text,descri,text,user,text,ifuseful,text,belongsto,text,time,text&pk=id";
    }
    public String read()
    {
        return "?type=read&key="+comment+"&pk=ifuseful&tableName=amazoncommenttable";
    }
    public String insert()
    {
        return "?type=insert&tableName=amazoncommenttable&keyrow=id&key="+comment+"&col=ifuseful&val=true&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key="+comment+"&col=ifuseful&keyrow=id&tableName=amazoncommenttable";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
