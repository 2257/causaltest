package com.yahoo.ycsb.workloads.causalsemantic;

public interface MonotonicWrite {
    String init();
    String createtable();
    String read();
    String insert();
    String delete();
    String cleanup();
}
