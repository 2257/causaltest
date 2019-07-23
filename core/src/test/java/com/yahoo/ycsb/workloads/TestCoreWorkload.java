/**                                                                                                                                                                                
 * Copyright (c) 2016 YCSB contributors. All rights reserved.                                                                                                                             
 *                                                                                                                                                                                 
 * Licensed under the Apache License, Version 2.0 (the "License"); you                                                                                                             
 * may not use this file except in compliance with the License. You                                                                                                                
 * may obtain a copy of the License at                                                                                                                                             
 *                                                                                                                                                                                 
 * http://www.apache.org/licenses/LICENSE-2.0                                                                                                                                      
 *                                                                                                                                                                                 
 * Unless required by applicable law or agreed to in writing, software                                                                                                             
 * distributed under the License is distributed on an "AS IS" BASIS,                                                                                                               
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or                                                                                                                 
 * implied. See the License for the specific language governing                                                                                                                    
 * permissions and limitations under the License. See accompanying                                                                                                                 
 * LICENSE file.                                                                                                                                                                   
 */
package com.yahoo.ycsb.workloads;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.testng.annotations.Test;

import com.yahoo.ycsb.generator.DiscreteGenerator;

public class TestCoreWorkload {

  @Test
  public void createOperationChooser() {
    final Properties p = new Properties();
    p.setProperty(CoreWorkload.READ_PROPORTION_PROPERTY, "0.20");
    p.setProperty(CoreWorkload.UPDATE_PROPORTION_PROPERTY, "0.20");
    p.setProperty(CoreWorkload.INSERT_PROPORTION_PROPERTY, "0.20");
    p.setProperty(CoreWorkload.SCAN_PROPORTION_PROPERTY, "0.20");
    p.setProperty(CoreWorkload.READMODIFYWRITE_PROPORTION_PROPERTY, "0.20");
    final DiscreteGenerator generator = CoreWorkload.createOperationGenerator(p);
    final int[] counts = new int[5];
    
    for (int i = 0; i < 100; ++i) {
      switch (generator.nextString()) {
      case "READ":
        ++counts[0];
        break;
      case "UPDATE":
        ++counts[1];
        break;
      case "INSERT": 
        ++counts[2];
        break;
      case "SCAN":
        ++counts[3];
        break;
      default:
        ++counts[4];
      } 
    }
    
    for (int i : counts) {
      // Doesn't do a wonderful job of equal distribution, but in a hundred, if we 
      // don't see at least one of each operation then the generator is really broke.
      assertTrue(i > 1);
    }
  }
  
  @Test (expectedExceptions = IllegalArgumentException.class)
  public void createOperationChooserNullProperties() {
    CoreWorkload.createOperationGenerator(null);
  }
  @Test
  public void testCassandraTwitterRYW()
  {
   List<String> list = new ArrayList<>();
    try {
       Class instance = Class.forName("com.yahoo.ycsb.workloads.dbinstance.MongodbTwitterMR");//Class.forName("CassandraTwitterRYW");
      Object obj = instance.newInstance();
      Method[] methods = instance.getMethods();
      System.out.println("getMethods获取的方法：");
      Field fieldRequest = instance.getField("request");
      Field fieldloopStartIndex = instance.getDeclaredField("loopStartIndex");
      Field fieldtweet = instance.getField("tweet");
      fieldtweet.setAccessible(true);
      fieldloopStartIndex.setAccessible(true);
      int loopStartIndex = (int)fieldloopStartIndex.get(obj);
   //   System.out.println(loopStartIndex);
      Method m = instance.getMethod("combine");
      m.invoke(obj);
      list = (ArrayList<String>) fieldRequest.get(obj);
      String tweet = (String) fieldtweet.get(obj);

      System.out.println("request = "+list);




    }catch (Exception e){
      e.printStackTrace();
    }
  }

}