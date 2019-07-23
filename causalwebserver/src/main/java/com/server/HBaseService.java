package com.server;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import com.operation.HbaseOp;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

@WebServlet(name = "HbaseService",urlPatterns = "/Hbase")
public class HBaseService extends HttpServlet
{
  @Override
    protected void doPost(javax.servlet.http.HttpServletRequest request,
                          javax.servlet.http.HttpServletResponse response)
            throws javax.servlet.ServletException, IOException
    {
        doGet(request,response);
    }
@Override
    protected void doGet(javax.servlet.http.HttpServletRequest request,
                         javax.servlet.http.HttpServletResponse response)
            throws javax.servlet.ServletException, IOException
    {
        String type = request.getParameter("type");
        String msg = "-";
        HbaseOp hbase = new HbaseOp();
        switch (type) {
            case "init":{
                System.out.println(hbase.init());
                break;
            }
            case "createtable": {
                String tableName = request.getParameter("tableName");
                String CFName = request.getParameter("CFName");
                hbase.createTable(tableName, CFName);
               // msg = "hbase createTable complete(web)";
                break;
            }
            case "read": {
                String tableName = request.getParameter("tableName");
                String key = request.getParameter("key");
                Set<String> fields = new HashSet<>();
                String s = request.getParameter("fields");
                String[] s4 = s.split(",");
                int i = 0;
                for (i = 0; i < s4.length ; i++) {
                    fields.add(s4[i]);
                }

                msg =  hbase.read(tableName, key, fields,1);
                break;
            }
            case "read2": {
                String tableName = request.getParameter("tableName");
                String key = request.getParameter("key");
                Set<String> fields = new HashSet<>();
                String s = request.getParameter("fields");
                String[] s4 = s.split(",");
                int i = 0;
                for (i = 0; i < s4.length ; i++) {
                    fields.add(s4[i]);
                }

                msg =  hbase.read(tableName, key, fields,2);
                break;
            }
            case "insert": {
                String tableName = request.getParameter("tableName");
                String key = request.getParameter("key");
                String KEYROW = request.getParameter("keyrow");
                String colFamily = request.getParameter("cf");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                hbase.insert(tableName, KEYROW, key, col, val,1);
                //msg = "hbase insert complete(web)";
                break;
            }
            case "insert2": {
                String tableName = request.getParameter("tableName");
                String key = request.getParameter("key");
                String KEYROW = request.getParameter("keyrow");
                String colFamily = request.getParameter("cf");
                String col = request.getParameter("col");
                String val = request.getParameter("val");
                hbase.insert(tableName, KEYROW, key, col, val,2);
                //msg = "hbase insert complete(web)";
                break;
            }
            case "delete":
            {
                String tableName = request.getParameter("tableName");
                String key = request.getParameter("key");
                String KEYROW = request.getParameter("keyrow");
                String colFamily = request.getParameter("cf");
                String col = request.getParameter("col");
                hbase.delete(tableName, KEYROW, key);
              //  msg = "hbase delete complete(web)";
                break;
            }
            case "clear":
            {
                String tableName = request.getParameter("clear");
                hbase.clearTable(tableName);
              //  msg = "hbase clear complete(web)";
                break;
            }
            case "cleanup":
            {
              hbase.cleanup();
              break;
            }
            default:{
               // msg="wrong type";
                break;
            }
        }

        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.println(msg);
        out.flush();
        out.close();
    }
}
