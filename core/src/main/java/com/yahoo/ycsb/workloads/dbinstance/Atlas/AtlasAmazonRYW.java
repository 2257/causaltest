package com.yahoo.ycsb.workloads.dbinstance.Atlas;

import com.yahoo.ycsb.workloads.testcase.AmazonRYW;

import java.util.ArrayList;
import java.util.List;

public class AtlasAmazonRYW extends AmazonRYW
{
    int typid = 45;
    int loopStartIndex = 2;

    List<String> request = new ArrayList<>();

    public AtlasAmazonRYW()
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
        return "?type=read&tableName=amazoncommenttable&key="+comment+"&col=ifuseful";
    }
    public String insert()
    {
        return "?type=insert&tableName=amazoncommenttable&id="+comment+"&col=ifuseful&val=true";
    }
    public String delete()
    {
        return "?type=delete&tableName=amazoncommenttable&id="+comment;
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
