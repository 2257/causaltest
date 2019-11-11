package com.yahoo.ycsb.workloads.testcase;

import com.yahoo.ycsb.workloads.causalsemantic.WriteFollowRead;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class TwitterWFR implements WriteFollowRead {
    public String user;
    public String tweet;
    public String tweet2;
    public String content;
    public String descri;
    public String descri2;
    public String content2;
    public List<String> request = new ArrayList<>();
    public String commodity;
    public String question;
    public TwitterWFR()
    {
        user = RandomStringUtils.randomNumeric(19);
        tweet = RandomStringUtils.randomNumeric(19);
        tweet2 = RandomStringUtils.randomNumeric(19);
        content = RandomStringUtils.randomNumeric(19);
        content2 = RandomStringUtils.randomNumeric(19);
        commodity = "";
        question = "";
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
        request.add(insert());
        request.add(read());
        request.add(insert2());
        request.add(read2());
        request.add(delete());
        request.add(cleanup());
    }
}
