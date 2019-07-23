package com.yahoo.ycsb.workloads.testcase;

import com.yahoo.ycsb.workloads.causalsemantic.MonotonicWrite;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class FlickrMW implements MonotonicWrite{
    public String picture;
    public String content;
    public String picture2;
    public String content2;
    public List<String> request = new ArrayList<>();

    public FlickrMW()
    {
        picture2 = "yui"+"_"+ RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(2).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(13).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        picture = "yui"+"_"+ RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(2).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(13).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        content = "yui"+"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(2).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(13).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        content2 = "yui"+"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(2).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(1).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(13).toLowerCase()
                +"_"+RandomStringUtils.randomAlphanumeric(4).toLowerCase();
    }
    abstract public String init();
    abstract public String createtable();
    abstract public String read();
    abstract public String insert();
    abstract public String insert2();
    abstract public String delete();
    abstract public String cleanup();
    public void combine()
    {
        request.add(init());
        request.add(createtable());
        request.add(insert());
        request.add(insert2());
        request.add(read());
        request.add(read());
        request.add(delete());
        request.add(cleanup());
    }
}
