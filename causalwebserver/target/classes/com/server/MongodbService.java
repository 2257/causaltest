package com.server;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import com.operation.MongodbOp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
@WebServlet(name = "MongodbService",urlPatterns = "/Mongodb")
public class MongodbService extends HttpServlet
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
        MongodbOp mongo = new MongodbOp();
        String tableName = request.getParameter("tableName");
        String type = request.getParameter("type");
        String msg = "-";
        // cassandraCQL.init();
        // System.out.println("type=  "+type);
        switch (type)
        {
            case "init":{
               mongo.init();
                break;
            }
            case "createtable": {
                String id = request.getParameter("id");
                mongo.createTable(tableName);
                //  msg = "cassandra createTable complete(web)";
                break;
            }
            case "read": {
                String key = request.getParameter("key");
                String col = request.getParameter("col");
                 Set<String> field = new HashSet<>();
                 field.add(col);
                msg = mongo.read(tableName, key, field,1);
                //  System.out.println("msg==="+msg);
                break;
            }
            case "read2": {
                String key = request.getParameter("key");
                String col = request.getParameter("col");
                Set<String> field = new HashSet<>();
                field.add(col);
                msg = mongo.read(tableName, key, field,2);
                //  System.out.println("msg==="+msg);
                break;
            }
            case "insert": {
                String id  = request.getParameter("id");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                mongo.insert(tableName, id, col, val,"nul",1);
                //   msg = "cassandra insert complete(web)";
                break;
            }
            case "insert2": {
                String id  = request.getParameter("id");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                mongo.insert(tableName, id, col, val,"nul",2);
                //   msg = "cassandra insert complete(web)";
                break;
            }
            case "update": {
                String id  = request.getParameter("id");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                mongo.update(tableName, id, col, val);
                //   msg = "cassandra insert complete(web)";
                break;
            }
            case "update2": {
                String id  = request.getParameter("id");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                mongo.update2(tableName, id, col, val);
                //   msg = "cassandra insert complete(web)";
                break;
            }
            case "delete": {
                // System.out.println("servlet cass delete");
                String id = request.getParameter("id");
                mongo.delete(tableName,id,"nul");
                //   msg = "cassandra delete complete(web)";
                break;
            }
            case "cleanup":{
                mongo.cleanup();
                break;
            }
            default:break;
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
