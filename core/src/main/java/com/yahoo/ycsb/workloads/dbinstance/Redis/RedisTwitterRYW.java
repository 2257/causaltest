package com.yahoo.ycsb.workloads.dbinstance.Redis;

import com.yahoo.ycsb.workloads.testcase.TwitterRYW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RedisTwitterRYW extends TwitterRYW
{
    int typid = 25;
    int loopStartIndex = 1;

    List<String> request = new ArrayList<>();

    public RedisTwitterRYW()
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
        return "?type=read&key="+user;
    }
    public String insert()
    {
        return "?type=insert&key="+user+"&val="+tweet;
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
      //  request.add(createtable());
        request.add(insert());
        request.add(read());
        request.add(delete());
        request.add(cleanup());
    }
}
