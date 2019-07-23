package com.yahoo.ycsb.workloads.causalsemantic;

public interface MonotonicRead {
    String init();
    String createtable();
    String read();
    String insert();
    String delete();
    String cleanup();
}
