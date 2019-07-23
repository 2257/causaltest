package com.yahoo.ycsb.workloads;

import java.text.SimpleDateFormat;
import java.util.List;

public class ViolationChecker
{
    int typid;
    int loop;
    List<String> result;
    //int violation;
    //int RestWorkload.violation_sizeerror;
    String descri;
    String descri2;
    String content;
    String tweet;
    int loopStartIndex;
    
    
    public ViolationChecker(String descri,String descri2,String content,String tweet)
    {
        
        this.descri2 = descri2;
        this.content = content;
        this.tweet = tweet;
        this.descri = descri;
    }
    public void printViolationInfo(int i,int num,int loop)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(i==0)
        {
            //violation_size error
            System.out.println("[violate_size] num:"+num+",proportion:"+(float)num/loop+"---"+df.format(System.currentTimeMillis()));
        }else{
            System.out.println("[violate] num:"+num+",proportion:"+(float)num/loop+"---"+df.format(System.currentTimeMillis()));

        }
    }
    public void check(List<String> result,int typid,int loop,int loopStartIndex)
    {
        switch (typid)
        {
            case 1:case 13:case 25: case 37: case 49:case 61:
        {
            String s ="";
            try
            {
                if(result.size()<1)
                {
                    RestWorkload.violation_sizeerror++;
                    // System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                    printViolationInfo(0,RestWorkload.violation_sizeerror,loop);
                    RestWorkload.urlIndex = loopStartIndex;
                }
                else
                {
                    s = result.get(0);
                    if((s.indexOf("default") !=-1)||(s.indexOf("error") !=-1))
                    {
                        RestWorkload.violation_sizeerror++;
                        //System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                        printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                        RestWorkload.urlIndex = loopStartIndex;
                    }
                    else if (!s.equals(tweet)) {
                        RestWorkload.violation++;
                        // System.out.println("[violate] num:" + violation + ", proportion:" + (float) violation / loop);
                        printViolationInfo(1,RestWorkload.violation,loop);
                        RestWorkload.urlIndex = loopStartIndex;
                    }
                }
            }catch(Exception e)
            {
                System.out.println("arraylist null exception "+ result);
            }
            break;
        }
            case 33: case 17:
        {
            String s ="";
            if(result.size()<1)
            {
                RestWorkload.violation_sizeerror++;
                //System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+",proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                RestWorkload.urlIndex =loopStartIndex;
            }
            else
            {
                s=result.get(0);
                if((s.indexOf("default") !=-1)||(s.indexOf("error") !=-1))
                {
                    RestWorkload.violation_sizeerror++;
                    // System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                    printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                    RestWorkload.urlIndex = loopStartIndex;
                }
                else if(!s.equals(descri))
                {
                    RestWorkload.violation++;
                    //System.out.println("[violate] num:"+violation+",proportion:"+(float)violation/loop);
                    printViolationInfo(1,RestWorkload.violation,loop);
                    RestWorkload.urlIndex =loopStartIndex;
                }
            }

            break;
        }
            case 2:case 26:/*case 30:*/case 34: case 38: case 50://case 62:
        {
            try
            {
                if(result.size()<2)
                {
                    RestWorkload.violation_sizeerror ++;
                    // System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                    printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                    RestWorkload.urlIndex = loopStartIndex;
                }
                else
                {

                    String s1 = result.get(0);
                    String s2 = result.get(1);
                    if((s1.indexOf("default") !=-1)||(s1.indexOf("error") !=-1) ||
                            (s2.indexOf("default") !=-1)||(s2.indexOf("error") !=-1))
                    {
                        RestWorkload.violation_sizeerror++;
                        // System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                        printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                        RestWorkload.urlIndex = loopStartIndex;
                    }
                    else if((s2.equals(s1)))
                    {
                        RestWorkload.violation++;
                        //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                        printViolationInfo(1,RestWorkload.violation,loop);
                        RestWorkload.urlIndex = loopStartIndex;
                    }

                }
            }catch (Exception e){e.printStackTrace();}
            break;

        }
     /* case 26:case 30:case 34: case 38: case 50:case 62://read the [write],but did not read the [read] which this [write] dependent
      {
        try
        {
            if(result.size()<2)
            {
                RestWorkload.violation_sizeerror ++;
                System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                RestWorkload.urlIndex = loopStartIndex;
            }
            else
            {

              String s1 = result.get(0);
              String s2 = result.get(1);
              if((s1.indexOf("default") !=-1)||(s1.indexOf("error") !=-1) ||
                  (s2.indexOf("default") !=-1)||(s2.indexOf("error") !=-1))
              {
                  RestWorkload.violation_sizeerror++;
                  System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                  RestWorkload.urlIndex = loopStartIndex;
              }
              else if((s2.equals(tweet) && !s1.equals(tweet)))
              {
                  violation ++;
                  System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                  RestWorkload.urlIndex = loopStartIndex;
              }

            }
        }catch (Exception e){e.printStackTrace();}
        break;
      }*/
            case 3:case 7:case 11:case 15:case 19:case 23:case 27:case 31:case 35: case 39: case 43: case 47: case 51:case 63: case 55:case 67: case 59: case 71:
        {
            try
            {
                if(result.size()<3)
                {
                    RestWorkload.violation_sizeerror ++;
                    //System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                    printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                    RestWorkload.urlIndex = loopStartIndex;
                }
                else
                {
                    String s1 = result.get(0);
                    String s2 = result.get(1);
                    String s3 = result.get(2);
                    if((s1.indexOf("default") !=-1&& s1.indexOf("error")!=-1)
                            ||(s2.indexOf("default") !=-1&& s2.indexOf("error")!=-1)
                            ||(s3.indexOf("default") !=-1&& s3.indexOf("error")!=-1))
                    {
                        RestWorkload.violation_sizeerror ++;
                        // System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                        printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                        RestWorkload.urlIndex = loopStartIndex;
                    }
                    else if(s3.equals(s1) && !s3.equals(s2))
                    {
                        RestWorkload.violation++;
                        // System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                        printViolationInfo(1,RestWorkload.violation,loop);
                        RestWorkload.urlIndex = loopStartIndex;
                    }
                }
            }catch (Exception e){}
            break;
        }
            case 4:case 8:case 12:case 16:case 20:case 24:case 28:case 32:case 36: case 40: case 44: case 48: case 52: case 64: case 56:case 68: case 60: case 72:
        {
            try{
                if(result.size()<2)
                {
                    RestWorkload.violation_sizeerror ++;
                    //System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                    printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                    RestWorkload.urlIndex = loopStartIndex;
                }
                else
                {
                    String s1 = result.get(0);
                    String s2 = result.get(1);
                    if((s1.indexOf("default") !=-1&& s1.indexOf("error")!=-1)
                            ||(s2.indexOf("default") !=-1&& s2.indexOf("error")!=-1))
                    {
                        RestWorkload.violation_sizeerror ++;
                        //System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                        printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                        RestWorkload.urlIndex = loopStartIndex;
                    }
                    else if(!s1.equals(s2))
                    {
                        RestWorkload.violation++;
                        //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                        printViolationInfo(1,RestWorkload.violation,loop);
                        RestWorkload.urlIndex = loopStartIndex;
                    }
                }
            }catch (Exception e){}
            break;
        }
            case 5: case 45: case 53: case 65:
        {
            if(result.size()<1)
            {
                RestWorkload.violation_sizeerror ++;
                // System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                RestWorkload.urlIndex = loopStartIndex;
            }
            else
            {
                String s = result.get(0);
                if(!s.equals("true"))
                {
                    RestWorkload.violation++;
                    //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                    printViolationInfo(1,RestWorkload.violation,loop);
                    RestWorkload.urlIndex = loopStartIndex;
                }
            }
            break;
        }
            case 54:case 30://2018-10-28
        {
            if(result.size()<3)
            {
                RestWorkload.violation_sizeerror ++;
                // System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                RestWorkload.urlIndex = loopStartIndex;
            }
            else
            {
                String s1 = result.get(0);
                String s2 = result.get(1);
                String s3 = result.get(2);
                if((s1.indexOf("default") !=-1&& s1.indexOf("error")!=-1)
                        ||(s2.indexOf("default") !=-1&& s2.indexOf("error")!=-1)
                        ||(s3.indexOf("default") !=-1&& s3.indexOf("error")!=-1))
                {
                    RestWorkload.violation_sizeerror ++;
                    //System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                    printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                    RestWorkload.urlIndex = loopStartIndex;
                }
                else if(s2.equals(descri2) && !s3.equals(descri))
                {
                    RestWorkload.violation++;
                    // System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                    printViolationInfo(1,RestWorkload.violation,loop);
                    RestWorkload.urlIndex = loopStartIndex;
                }
            }
            break;
        }
            case 6:case 10: case 14:case 18:case 22:case 42: case 46: /*case 54:*/case 62:case 66: case 58: case 70://read the commodity in cart but then didnt read the descri
        {
            if(result.size()<2)
            {
                RestWorkload.violation_sizeerror ++;
                //System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                RestWorkload.urlIndex = loopStartIndex;
            }
            else
            {
                String s1 = result.get(0);
                String s2 = result.get(1);
                if((s1.indexOf("default") !=-1&& s1.indexOf("error")!=-1)
                        ||(s2.indexOf("default") !=-1&& s2.indexOf("error")!=-1))
                {
                    RestWorkload.violation_sizeerror ++;
                    // System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                    printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                    RestWorkload.urlIndex = loopStartIndex;
                }
                else if(s2.equals(s1))
                {
                    RestWorkload.violation++;
                    //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                    printViolationInfo(1,RestWorkload.violation,loop);
                    //printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                    RestWorkload.urlIndex = loopStartIndex;
                }
            }
            break;
        }
            case 9: case 41: case 57:case 69:
        {
            if(result.size()<1)
            {
                RestWorkload.violation_sizeerror ++;
                //System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                RestWorkload.urlIndex = loopStartIndex;
            }
            else
            {
                String s = result.get(0);
                if(!s.equals(content))
                {
                    RestWorkload.violation++;
                    //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                    printViolationInfo(1,RestWorkload.violation,loop);
                    RestWorkload.urlIndex = loopStartIndex;
                }
            }
            break;
        }
     
     /* case 14:case 18:
      {
          if(result.size()<2)
          {
              RestWorkload.violation_sizeerror ++;
              System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
              RestWorkload.urlIndex = loopStartIndex;
          }
          else
          {
              String s1 = result.get(0);
              String s2 = result.get(1);
              if((s1.indexOf("default") !=-1&& s1.indexOf("error")!=-1)
                  ||(s2.indexOf("default") !=-1&& s2.indexOf("error")!=-1))
              {
                RestWorkload.violation_sizeerror ++;
                System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                RestWorkload.urlIndex = loopStartIndex;
              }
              else if((s2.equals(comment) && !s1.equals(content)))
              {
                violation ++;
                System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                RestWorkload.urlIndex = loopStartIndex;
              }
          }
          break;
      }*/
            case 21:
            {
                if(result.size()<1)
                {
                    RestWorkload.violation_sizeerror ++;
                    //System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                    printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                    RestWorkload.urlIndex = loopStartIndex;
                }
                else
                {
                    String s = result.get(0);
                    if(!s.equals(content))
                    {
                        RestWorkload.violation++;
                        //System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                        printViolationInfo(1,RestWorkload.violation,loop);

                        RestWorkload.urlIndex = loopStartIndex;
                    }
                }
                break;
            }


            case 29:
            {
                if(result.size()<1)
                {
                    RestWorkload.violation_sizeerror ++;
                    //System.out.println("[violate_size] num:"+RestWorkload.violation_sizeerror+", proportion:"+(float)RestWorkload.violation_sizeerror/loop);
                    printViolationInfo(0,RestWorkload.violation_sizeerror,loop);

                    RestWorkload.urlIndex = loopStartIndex;
                }
                else
                {
                    String s = result.get(0);
                    if(!s.equals(descri))
                    {
                        RestWorkload.violation++;
                        // System.out.println("[violate] num:"+violation+", proportion:"+(float)violation/loop);
                        printViolationInfo(1,RestWorkload.violation,loop);
                        RestWorkload.urlIndex = loopStartIndex;
                    }
                }
                break;
            }
        }
        //System.out.println("violate");
        
    }
    
}