package com.server;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import com.operation.AtlasOp;
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
@WebServlet(name = "AtlasService",urlPatterns = "/Atlas")
public class AtlasService extends HttpServlet
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
        AtlasOp atlasOp = new AtlasOp();
        String tableName = request.getParameter("tableName");
        String type = request.getParameter("type");
        String msg = "-";
        // cassandraCQL.init();
        // System.out.println("type=  "+type);
        switch (type)
        {
            case "init":{
                atlasOp.init();
                break;
            }
            case "createtable": {
                String id = request.getParameter("id");
                atlasOp.createTable(tableName);
                //  msg = "cassandra createTable complete(web)";
                break;
            }
            case "read": {
                String key = request.getParameter("key");
                String col = request.getParameter("col");
                Set<String> field = new HashSet<>();
                field.add(col);
                msg = atlasOp.read(tableName, key, field,1);
                //  System.out.println("msg==="+msg);
                break;
            }
            case "read2": {
                String key = request.getParameter("key");
                String col = request.getParameter("col");
                Set<String> field = new HashSet<>();
                field.add(col);
                msg = atlasOp.read(tableName, key, field,2);
                //  System.out.println("msg==="+msg);
                break;
            }
            case "insert": {
                String id  = request.getParameter("id");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                atlasOp.insert(tableName, id, col, val,"nul",1);
                //   msg = "cassandra insert complete(web)";
                break;
            }
            case "insert2": {
                String id  = request.getParameter("id");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                atlasOp.insert(tableName, id, col, val,"nul",2);
                //   msg = "cassandra insert complete(web)";
                break;
            }
            case "update": {
                String id  = request.getParameter("id");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                atlasOp.update(tableName, id, col, val);
                //   msg = "cassandra insert complete(web)";
                break;
            }
            case "update2": {
                String id  = request.getParameter("id");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                atlasOp.update2(tableName, id, col, val);
                //   msg = "cassandra insert complete(web)";
                break;
            }
            case "delete": {
                // System.out.println("servlet cass delete");
                String id = request.getParameter("id");
                atlasOp.delete(tableName,id,"nul");
                //   msg = "cassandra delete complete(web)";
                break;
            }
            case "cleanup":{
                atlasOp.cleanup();
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
