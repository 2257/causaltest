package com.yahoo.ycsb.workloads.causalsemantic;

public interface WriteFollowRead {
    String init();
    String createtable();
    String read();
    String insert();
    String delete();
    String cleanup();
}
