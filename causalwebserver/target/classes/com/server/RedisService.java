package com.server;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import com.operation.RedisOp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

@WebServlet(name = "RedisService",urlPatterns = "/Redis")
public class RedisService extends HttpServlet
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
        String portString = request.getParameter("port");
        String type = request.getParameter("type");
        //System.out.println(type);
        String msg = "-";
        RedisOp redis = new RedisOp();

        switch (type)
        {
            case "init":{
                redis.init();
                break;
            }
            case "read": {
                String key = request.getParameter("key");
                Set<String> field = new HashSet<>();
                //String s3 = request.getParameter("pk");
               // String[] s4 = s3.split(",");
                int i= 0 ;
               // for(i = 0;i< s4.length-1;i++)
               // {
               //     field.add(s4[i]);
               // }
                msg = redis.read(tableName, key, field,1);
                break;
            }
            case "read2": {
                String key = request.getParameter("key");
                Set<String> field = new HashSet<>();
               /* String s3 = request.getParameter("pk");
                String[] s4 = s3.split(",");
                int i= 0 ;
                for(i = 0;i< s4.length-1;i++)
                {
                    field.add(s4[i]);
                }*/
                msg = redis.read2(tableName, key, field,2);
                break;
            }
            case "insert": {
               // System.out.println("@@@@@@");
                //String KEYROW = request.getParameter("keyrow");
                String key = request.getParameter("key");
              //  String colFamily = request.getParameter("cf");
              //  String col = request.getParameter("col");
                String val = request.getParameter("val");
                redis.insert(key, val,"1","2","3",1);
               // msg = "redis insert complete(web)";
                break;
            }
            case "insert2": {
                // System.out.println("@@@@@@");
               // String KEYROW = request.getParameter("keyrow");
                String key = request.getParameter("key");
               // String colFamily = request.getParameter("cf");
               // String col = request.getParameter("col");
                String val = request.getParameter("val");
                redis.insert(key, val,"1","2","3",2);
              //  msg = "redis insert complete(web)";
                break;
            }
            case "delete": {
               // String KEYROW = request.getParameter("keyrow");
                String key = request.getParameter("key");
               // String colFamily = request.getParameter("cf");
              //  String col = request.getParameter("col");
              //  String val = request.getParameter("val");
                redis.delete( key,"1","2");
                break;
            }
            case "cleanup":{
               redis.cleanup();
            }
            default:break;
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        response.getWriter();
        PrintWriter out = response.getWriter();
        out.println(msg);
        out.flush();
        out.close();
    }
}
