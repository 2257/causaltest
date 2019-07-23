package com.yahoo.ycsb.workloads.testcase;

import com.yahoo.ycsb.workloads.causalsemantic.ReadYourWrite;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class TwitterRYW implements ReadYourWrite
{
    public String user;
    public String tweet;
    public String descri;
    public String descri2;
    public String content;
    public List<String> request = new ArrayList<>();

    public TwitterRYW()
    {
        user = RandomStringUtils.randomNumeric(19);
        tweet = RandomStringUtils.randomNumeric(19);
    }
    abstract public String init();
    abstract public String createtable();
    abstract public String read();
    abstract public String insert();
    abstract public String delete();
    abstract public String cleanup();
     public void combine()
     {
         request.add(init());
         request.add(createtable());
         request.add(insert());
         request.add(read());
         request.add(delete());
         request.add(cleanup());
     }
}
