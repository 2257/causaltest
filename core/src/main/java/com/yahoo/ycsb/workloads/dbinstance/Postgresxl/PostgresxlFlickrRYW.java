package com.yahoo.ycsb.workloads.dbinstance.Postgresxl;

import com.yahoo.ycsb.workloads.testcase.FlickrRYW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostgresxlFlickrRYW extends FlickrRYW
{
    int typid = 69;
    int loopStartIndex = 2;

    List<String> request = new ArrayList<>();

    public PostgresxlFlickrRYW()
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
        return  "?type=read&key="+picture+"&col=content&tableName=flickrpicturetable";
    }
    public String insert()
    {
        return "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&id="+picture+"&tableName=flickrpicturetable";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
