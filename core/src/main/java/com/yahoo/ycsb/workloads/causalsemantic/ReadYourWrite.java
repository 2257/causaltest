package com.yahoo.ycsb.workloads.causalsemantic;

public interface ReadYourWrite {
    String init();
    String createtable();
    String read();
    String insert();
    String delete();
    String cleanup();
}
