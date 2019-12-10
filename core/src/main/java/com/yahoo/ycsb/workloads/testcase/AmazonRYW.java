package com.yahoo.ycsb.workloads.testcase;

import com.yahoo.ycsb.workloads.causalsemantic.ReadYourWrite;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AmazonRYW implements ReadYourWrite
{
    public String comment;
    public String commodity;
    public String descri;
    public String tweet;
    public String descri2;
    public List<String> request = new ArrayList<>();
    public String content;
    public String content2;
    public String question;
    public AmazonRYW()
    {
        comment = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        commodity = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        descri = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        descri2 = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        tweet = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        question = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        content = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        content2 = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        commodity = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
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
