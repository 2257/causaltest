package com.yahoo.ycsb.workloads.testcase;

import com.yahoo.ycsb.workloads.causalsemantic.WriteFollowRead;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AmazonWFR implements WriteFollowRead {
    public String tweet;
    public String commodity;
    public String commodity2;
    public String descri;
    public String descri2;
    public String content;
    public String content2;
    public String question;
    public List<String> request = new ArrayList<>();

    public AmazonWFR()
    {
        commodity = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        commodity2 = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        descri = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        descri2 = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        tweet = RandomStringUtils.randomAlphanumeric(10).toLowerCase();

        question = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        content = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        content2 = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
    }
    abstract public String init();
    abstract public String createtable();
    abstract public String read();
    abstract public String insert();
    abstract public String read2();
    abstract public String insert2();
    abstract public String delete();
    abstract public String cleanup();
    public void combine()
    {
        request.add(init());
        request.add(createtable());
        request.add(insert2());
        request.add(read());
        request.add(insert());
        request.add(read2());
        request.add(delete());
        request.add(cleanup());
    }
}
