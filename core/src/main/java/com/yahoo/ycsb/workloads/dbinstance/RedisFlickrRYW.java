package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.FlickrRYW;

import java.util.ArrayList;
import java.util.List;

public class RedisFlickrRYW  extends FlickrRYW
{
    int typid = 33;
    int loopStartIndex = 1;

    List<String> request = new ArrayList<>();

    public RedisFlickrRYW()
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
        return "?type=read&key="+picture;
    }
    public String insert()
    {
        return "?type=insert&key="+picture+"&val="+descri;
    }
    public String delete()
    {
        return "?type=read&key="+picture;
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
