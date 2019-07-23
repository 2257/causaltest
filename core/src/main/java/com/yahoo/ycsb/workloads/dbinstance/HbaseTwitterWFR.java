package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.TwitterWFR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HbaseTwitterWFR extends TwitterWFR
{

    int typid = 14;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public HbaseTwitterWFR()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return  "?type=createtable&tableName=tweettable&CFName=id,content,likeduser,retweetuser,commentid,inmommentid,time";
    }
    public String read()
    {
        return "?type=read&key="+tweet+"&fields=content&tableName=tweettable";
    }
    public String read2()
    {
        return "?type=read2&key="+tweet+"&fields=content&tableName=tweettable";
    }
    public String insert()
    {
        return "?type=insert2&tableName=tweettable&keyrow="+tweet+"&col=content&val="+content+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=tweettable&keyrow="+tweet+"&col=content&val="+content2+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key="+tweet+"&tableName=tweettable&col=content";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
