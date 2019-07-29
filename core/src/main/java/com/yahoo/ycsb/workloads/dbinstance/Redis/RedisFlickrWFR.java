package com.yahoo.ycsb.workloads.dbinstance.Redis;

import com.yahoo.ycsb.workloads.testcase.FlickrWFR;

import java.util.ArrayList;
import java.util.List;

public class RedisFlickrWFR extends FlickrWFR
{
    int typid = 34;
    int loopStartIndex = 1;

    List<String> request = new ArrayList<>();

    public RedisFlickrWFR()
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
        return "?type=read&key="+picture;
    }
    public String read2()
    {
        return "?type=read2&key="+picture;
    }
    public String insert()
    {
        return "?type=insert2&key="+picture+"&val="+content;
    }
    public String insert2()
    {
        return "?type=insert&key="+picture+"&val="+content2;
    }
    public String delete()
    {
        return "?type=delete&key="+picture;
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
