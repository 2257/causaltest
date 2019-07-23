package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.AmazonWFR;

import java.util.ArrayList;
import java.util.List;

public class RedisAmazonWFR extends AmazonWFR
{
    int typid = 30;
    int loopStartIndex = 1;

    List<String> request = new ArrayList<>();

    public RedisAmazonWFR()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return "";
    }
    public String read()
    {
        return "?type=read&key="+commodity;
    }
    public String read2()
    {
        return "?type=read2&key="+commodity;
    }
    public String insert()
    {
        return "?type=insert2&key="+commodity+"&val="+descri;
    }
    public String insert2()
    {
        return "?type=insert&key="+commodity+"&val="+descri2;
    }
    public String delete()
    {
        return "?type=delete&key="+commodity;
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
    public void combine()
    {
        request.add(init());
        // request.add(createtable());
        request.add(insert2());
        request.add(read());
        request.add(insert());
        request.add(read2());
        request.add(delete());
        request.add(cleanup());
    }
}
