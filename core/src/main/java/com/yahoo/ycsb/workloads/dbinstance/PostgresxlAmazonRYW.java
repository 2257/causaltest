package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.AmazonRYW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostgresxlAmazonRYW extends AmazonRYW
{
    int typid = 65;
    int loopStartIndex = 2;

    List<String> request = new ArrayList<>();

    public PostgresxlAmazonRYW()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return "?type=createtable&tableName=amazoncommenttable";
    }
    public String read()
    {
        return  "?type=read&key="+comment+"&col=ifuseful&tableName=amazoncommenttable";
    }
    public String insert()
    {
        return "?type=insert&tableName=amazoncommenttable&id="+comment+"&col=ifuseful&val=true&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&id="+comment+"&tableName=amazoncommenttable";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
