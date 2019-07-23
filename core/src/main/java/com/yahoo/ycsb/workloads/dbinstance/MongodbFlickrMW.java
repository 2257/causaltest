package com.yahoo.ycsb.workloads.dbinstance;

import com.yahoo.ycsb.workloads.testcase.FlickrMW;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MongodbFlickrMW extends FlickrMW
{
    int typid = 44;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public MongodbFlickrMW()
    {
        this.request = super.request;
    }

    public String init()
    {
        return "?type=init";
    }
    public String createtable()
    {
        return  "?type=createtable&tableName=flickrpicturetable";
    }
    public String read()
    {
        return "?type=read2&key="+picture+"&col=content&tableName=flickrpicturetable";
    }
    public String insert()
    {
        return "?type=insert&tableName=flickrpicturetable&"+picture+"&col=content&val="+content+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=update&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert3()
    {
        return "?type=update&tableName=flickrpicturetable&id="+picture+"&col=content&val="+content2+"&time="+
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
    public void combine()
    {
        request.add(init());
        request.add(createtable());
        request.add(insert());
        request.add(insert2());
        request.add(insert3());
        request.add(read());
        request.add(read());
       // request.add(delete());
        request.add(cleanup());
    }
}
