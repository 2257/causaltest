package com.server;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import com.operation.CassandraOp;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebServlet(name = "CassandraService",urlPatterns = "/cassandra")
public class CassandraService extends HttpServlet
{

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request,response);
    }
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException
    {
        String tableName = request.getParameter("tableName");
        String CFName = request.getParameter("CFName");
        String type = request.getParameter("type");
        String msg = "-";
        CassandraOp cassandra = new CassandraOp();

       // System.out.println("type=  "+type);
        switch (type)
        {
            case "init":{
                  cassandra.init();
                break;
            }
            case "createtable": {
                Map<String, String> fields = new HashMap<>();
                String s1 = request.getParameter("fields");
                String[] s2 = s1.split(",");
             //   System.out.println(s2.length);
                int i =0;

                for( i = 0 ;i < s2.length; i++)
                {
                    if(i%2 == 0)
                    {
                        fields.put(s2[i],s2[i+1]);
                    }
                }
                Set<String> primarykey = new HashSet<>();
                String s3 = request.getParameter("pk");
                String[] s4 = s3.split(",");
                for(i = 0;i< s4.length;i++)
                {
                    primarykey.add(s4[i]);
                }
                cassandra.createTable(tableName, fields, primarykey);
              //  msg = "cassandra createTable complete(web)";
                break;
            }

            case "read": {
               String key = request.getParameter("key");
                Set<String> field = new HashSet<>();
                String s3 = request.getParameter("pk");
                String[] s4 = s3.split(",");
                int i= 0 ;
                for(i = 0;i< s4.length;i++)
                {
                    field.add(s4[i]);
                }
               msg = cassandra.read(tableName, key, field,1);
              //  System.out.println("msg==="+msg);
                break;
            }
            case "read2": {
                String key = request.getParameter("key");
                Set<String> field = new HashSet<>();
                String s3 = request.getParameter("pk");
                String[] s4 = s3.split(",");
                int i= 0 ;
                for(i = 0;i< s4.length;i++)
                {
                    field.add(s4[i]);
                }
                msg = cassandra.read(tableName, key, field,2);
                //  System.out.println("msg==="+msg);
                break;
            }
               case "insert": {
                String KEYROW = request.getParameter("keyrow");
                String key = request.getParameter("key");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                cassandra.insert(tableName, KEYROW, key, col, val,1);
             //   msg = "cassandra insert complete(web)";
                break;
            }
            case "insert2": {
                String KEYROW = request.getParameter("keyrow");
                String key = request.getParameter("key");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                cassandra.insert(tableName, KEYROW, key, col, val,2);
                //   msg = "cassandra insert complete(web)";
                break;
            }
            case "delete":
              {
                String KEYROW = request.getParameter("keyrow");
                String key = request.getParameter("key");
                cassandra.delete(tableName,  KEYROW, key);
                break;
            }
            case "cleanup":{
                cassandra.cleanup();
                break;
            }
            default:{
              break;
            }
        }

        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
       // response.getWriter();
        PrintWriter out = response.getWriter();
        out.println(msg);
        out.flush();
        out.close();
    }
}
