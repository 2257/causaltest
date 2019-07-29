package com.yahoo.ycsb.workloads.dbinstance.Redis;

import com.yahoo.ycsb.workloads.testcase.TwitterWFR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RedisTwitterWFR extends TwitterWFR
{
    int typid = 26;
    int loopStartIndex = 1;

    List<String> request = new ArrayList<>();

    public RedisTwitterWFR()
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
        return "?type=read&key="+user;
    }
    public String read2()
    {
        return "?type=read2&key="+user;
    }
    public String insert()
    {
        return "?type=insert2&key="+user+"&val="+tweet;
    }
    public String insert2()
    {
        return "?type=insert&key="+user+"&val="+tweet2;
    }
    public String delete()
    {
        return "?type=delete&key="+user;
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
