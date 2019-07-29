package com.yahoo.ycsb.workloads.dbinstance.Hbase;

import com.yahoo.ycsb.workloads.testcase.FlickrWFR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HbaseFlickrWFR extends FlickrWFR
{
    int typid = 22;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public HbaseFlickrWFR()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return  "?type=createtable&tableName=flickrpicturetable&CFName=id,content,permit,likeduser,album,location,comment,tag,time";
    }
    public String read()
    {
        return "?type=read&key="+picture+"&fields=tag&tableName=flickrpicturetable";
    }
    public String read2()
    {
        return "?type=read2&key="+picture+"&fields=tag&tableName=flickrpicturetable";
    }
    public String insert()
    {
        return "?type=insert2&tableName=flickrpicturetable&keyrow="+picture+"&col=tag&val="+tag+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=flickrpicturetable&keyrow="+picture+"&col=tag&val="+tag2+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key="+picture+"&tableName=flickrpicturetable&col=likeduser";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
