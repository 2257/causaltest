package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.FlickrRYW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HbaseFlickrRYW extends FlickrRYW
{
    int typid = 21;
    int loopStartIndex = 2;

    List<String> request = new ArrayList<>();

    public HbaseFlickrRYW()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return "?type=createtable&tableName=flickrpicturetable&CFName=id,content,permit,likeduser,album,location,comment,tag,time";
    }
    public String read()
    {
        return "?type=read&key="+picture+"&fields=content&tableName=flickrpicturetable";
    }
    public String insert()
    {
        return "?type=insert&tableName=flickrpicturetable&keyrow="+picture+"&col=content&val="+content+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key="+picture+"&tableName=flickrpicturetable&col=content";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
