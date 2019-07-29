package com.yahoo.ycsb.workloads.dbinstance.Atlas;

import com.yahoo.ycsb.workloads.testcase.AmazonMR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AtlasAmazonMR extends AmazonMR
{
    int typid = 47;
    int loopStartIndex = 3;
    public List<String> request = new ArrayList<>();

    public AtlasAmazonMR()
    {

        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return  "?type=createtable&tableName=amazoncommoditytable";
    }
    public String read()
    {
        return "?type=read2&key="+commodity+"&col=descri&tableName=amazoncommoditytable";
    }
    public String insert()
    {
        return "?type=insert&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=update&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert3()
    {
        return "?type=update&tableName=amazoncommoditytable&id="+commodity+"&col=descri&val="+descri2+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "";
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
        request.add(read());
        request.add(insert3());

        request.add(read());
        request.add(read());
        //  request.add(delete());
        request.add(cleanup());
    }
}
