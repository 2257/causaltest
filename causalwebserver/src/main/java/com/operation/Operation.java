package com.operation;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface Operation
{
   String init() throws IOException;
   void cleanup();
//  public String createTable(String tableName, Map<String, String> fields, Set<String> primarykey);
   String read(String table, String key, Set<String> fields,int clientFlag)throws IOException;
   String insert(String table, String KEYROW, String key, String col, String val,int clientFlag) throws IOException;
   String delete(String tableName, String KEYROW,String key) throws IOException;



}
