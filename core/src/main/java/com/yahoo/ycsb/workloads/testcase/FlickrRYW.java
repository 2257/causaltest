package com.yahoo.ycsb.workloads.testcase;

import com.yahoo.ycsb.workloads.causalsemantic.ReadYourWrite;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class FlickrRYW implements ReadYourWrite
{

    public String picture;
    public String picture2;
    public String content;
    public String descri;
    public String user;
    public String tag;
    public String tag2;
    public String content2;
    public String tweet;
    public String descri2;
    public String commodity;
    public String question;
    public List<String> request = new ArrayList<>();

    public FlickrRYW()
    {
        tweet="";
        descri2="";
        commodity="";
        question="";
        user="";
        tag="";
        tag2="";
        content2="";
        picture2="";
        picture = "yui"+"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(2).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(3).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        descri = "yui"+"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(2).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(3).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        content = "yui"+"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(2).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(3).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(4).toLowerCase();

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
