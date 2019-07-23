package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.FlickrMW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HbaseFlickrMW extends FlickrMW
{
    int typid = 24;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public HbaseFlickrMW()
    {

        this.request = super.request;
    }

    public String init()
    {
        return "?type=init";
    }
    public String createtable()
    {
        return  "?type=createtable&tableName=flickrpicturetable&CFName=id,content,permit,likeduser,album,location,comment,tag,time";
    }
    public String read()
    {
        return "?type=read2&key="+picture+"&fields=permit&tableName=flickrpicturetable";
    }
    public String insert()
    {
        return "?type=insert&tableName=flickrpicturetable&keyrow="+picture+"&col=permit&val=1&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=flickrpicturetable&keyrow="+picture2+"&col=permit&val=1&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key=" + picture + "&tableName=flickrpicturetable&col=permit";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
