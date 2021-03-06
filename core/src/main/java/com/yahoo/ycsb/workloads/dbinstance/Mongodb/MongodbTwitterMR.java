package com.yahoo.ycsb.workloads.dbinstance.Mongodb;

import com.yahoo.ycsb.workloads.testcase.TwitterMR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MongodbTwitterMR extends TwitterMR
{
    int typid = 39;
    int loopStartIndex = 3;
    public List<String> request = new ArrayList<>();

    public MongodbTwitterMR()
    {

        this.request = super.request;
    }

    public String init()
    {
        return  "?type=init";
    }
    public String createtable()
    {
        return  "?type=createtable&tableName=twitterusertable";
    }
    public String read()
    {
        return "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
    }
    public String insert()
    {
        return "?type=insert&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert2()
    {
        return "?type=update&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String insert3()
    {
        return "?type=update&tableName=twitterusertable&id="+user+"&col=liketweet&val="+tweet2+"&time="+
                (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(System.currentTimeMillis()));
    }
    public String delete()
    {
        return "?type=read2&key="+user+"&col=liketweet&tableName=twitterusertable";
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
