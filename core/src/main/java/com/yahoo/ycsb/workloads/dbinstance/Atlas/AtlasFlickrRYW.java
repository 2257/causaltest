package com.yahoo.ycsb.workloads.dbinstance.Atlas;

import com.yahoo.ycsb.workloads.testcase.FlickrRYW;

import java.util.ArrayList;
import java.util.List;

public class AtlasFlickrRYW extends FlickrRYW
{
    int typid = 41;
    int loopStartIndex = 2;

    List<String> request = new ArrayList<>();

    public AtlasFlickrRYW()
    {
        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return "?type=createtable&tableName=flickrpicturetable";
    }
    public String read()
    {
        return "?type=read&tableName=flickrpicturetable&key="+picture+"&col=content";
    }
    public String insert()
    {
        return "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content;
    }
    public String delete()
    {
        return "?type=delete&tableName=flickrpicturetable&id="+picture;
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
