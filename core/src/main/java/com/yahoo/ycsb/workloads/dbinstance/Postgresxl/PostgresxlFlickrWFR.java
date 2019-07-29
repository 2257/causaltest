package com.yahoo.ycsb.workloads.dbinstance.Postgresxl;

import com.yahoo.ycsb.workloads.testcase.FlickrWFR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostgresxlFlickrWFR extends FlickrWFR
{
    int typid = 70;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public PostgresxlFlickrWFR()
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
        return "?type=read&key="+picture+"&col=content&tableName=flickrpicturetable";
    }
    public String read2()
    {
        return "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
    }
    public String insert()
    {
        return "?type=insert2&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content2+"&time="+
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
