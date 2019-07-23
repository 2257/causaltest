package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.AmazonRYW;

import java.util.ArrayList;
import java.util.List;

public class RedisAmazonRYW extends AmazonRYW
{
    int typid = 29;
    int loopStartIndex = 1;

    List<String> request = new ArrayList<>();

    public RedisAmazonRYW()
    {
        // this.id1 = super.id1;

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
    public String insert()
    {
        return "?type=insert&key="+commodity+"&val="+descri;
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
        //  request.add(createtable());
        request.add(insert());
        request.add(read());
        request.add(delete());
        request.add(cleanup());
    }
}
