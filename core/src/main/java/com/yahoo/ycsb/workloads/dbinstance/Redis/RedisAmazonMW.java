package com.yahoo.ycsb.workloads.dbinstance.Redis;

import com.yahoo.ycsb.workloads.testcase.AmazonMW;

import java.util.ArrayList;
import java.util.List;

public class RedisAmazonMW extends AmazonMW
{
    int typid = 32;
    int loopStartIndex = 1;

    List<String> request = new ArrayList<>();

    public RedisAmazonMW()
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
        return "?type=read2&key="+commodity;
    }

    public String insert()
    {
        return "?type=insert&key="+commodity+"&val="+content;
    }
    public String insert2()
    {
        return "?type=insert&key="+commodity+"&val="+content2;
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
        //request.add(createtable());
        request.add(insert());
        request.add(insert2());
        request.add(read());
        request.add(read());
       // request.add(delete());
        request.add(cleanup());
    }
}
