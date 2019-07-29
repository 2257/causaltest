package com.yahoo.ycsb.workloads.dbinstance.Hbase;

import com.yahoo.ycsb.workloads.testcase.AmazonMR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HbaseAmazonMR extends AmazonMR
{
    int typid = 19;
    int loopStartIndex = 2;
    public List<String> request = new ArrayList<>();

    public HbaseAmazonMR()
    {

        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return  "?type=createtable&tableName=amazonquestiontable2&CFName=id,content,comment,time";
    }
    public String read()
    {
        return "?type=read2&key="+question+"&fields=content&tableName=amazonquestiontable2";
    }
    public String read2()
    {
        return "?type=read2&key="+question+"&fields=content&tableName=amazonquestiontable2";
    }
    public String insert()
    {
        return "?type=insert&tableName=amazonquestiontable2&keyrow="+question+"&col=content&val="+content+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=insert&tableName=amazonquestiontable2&keyrow="+question+"&col=content&val="+content2+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=delete&key="+question+"&tableName=amazonquestiontable&col=content";
    }
    public String cleanup()
    {
        return  "?type=cleanup";
    }
}
