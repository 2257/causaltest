package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.AmazonMR;

import java.util.ArrayList;
import java.util.List;

public class RedisAmazonMR extends AmazonMR
{
    int typid = 31;
    int loopStartIndex = 1;

    List<String> request = new ArrayList<>();

    public RedisAmazonMR()
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
        return "?type=read2&key="+question;
    }

    public String insert()
    {
        return "?type=insert&key="+question+"&val="+content;
    }
    public String insert2()
    {
        return "?type=insert&key="+question+"&val="+content2;
    }
    public String delete()
    {
        return "?type=delete&key="+question;
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
    public void combine()
    {
        request.add(init());
        // request.add(createtable());
        request.add(insert());
        request.add(read());
        request.add(insert2());
        request.add(read());
        request.add(read());
      //  request.add(delete());
        request.add(cleanup());
    }
}
