package com.yahoo.ycsb.workloads.dbinstance.Cassandra;

import com.yahoo.ycsb.workloads.testcase.FlickrMR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CassandraFlickrMR extends FlickrMR
{

    int typid = 11;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public CassandraFlickrMR()
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
        return "?type=createtable&tableName=k123.flickrpicturetable&fields=id,text,content,text,permit,text,likeduser,text,album,text,location,text,comment,text,tag,text,time,text&pk=id";
    }
    public String read()
    {
        return "?type=read2&key="+picture+"&pk=content&tableName=flickrpicturetable";
    }
    public String insert()
    {
        return "?type=insert&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=content&val="+content+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=flickrpicturetable&keyrow=id&key="+picture+"&col=content&val="+content2+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key="+picture+"&col=content&keyrow=id&tableName=flickrpicturetable";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
