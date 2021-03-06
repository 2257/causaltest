package com.yahoo.ycsb.workloads.dbinstance.Redis;

import com.yahoo.ycsb.workloads.testcase.FlickrMW;

import java.util.ArrayList;
import java.util.List;

public class RedisFlickrMW extends FlickrMW
{
    int typid = 36;
    int loopStartIndex = 1;

    List<String> request = new ArrayList<>();

    public RedisFlickrMW()
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
        return "?type=read2&key="+picture;
    }

    public String insert()
    {
        return "?type=insert&key="+picture+"&val="+content;
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
        //request.add(createtable());
        request.add(insert());
        request.add(insert2());
        request.add(read());
        request.add(read());
        // request.add(delete());
        request.add(cleanup());
    }
}
