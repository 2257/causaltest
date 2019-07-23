package com.operation;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import redis.clients.jedis.JedisCommands;

public class RedisOp implements Operation
{
    private static JedisCommands jedis;
    private static JedisCommands jedis2;
    private static RedissonClient redisson;
    private static RedissonClient redisson2;
    private static Properties properties = new Properties();
    private static String publicIP1 = "publicIP1";
    private static String publicIP2 = "publicIP2";
    private static String publicIP3 = "publicIP3";
    private static String privateIP1 = "privateIP1";
    private static String privateIP2 = "privateIP2";
    private static String privateIP3 = "privateIP3";
    private static String redis1_port1 = "redis1_port1";
    private static String redis1_port2 = "redis1_port2";
    private static String redis1_port3 = "redis1_port3";
    private static String redis2_port1 = "redis2_port1";
    private static String redis2_port2 = "redis2_port2";
    private static String redis2_port3 = "redis2_port3";
    private static String redis3_port1 = "redis3_port1";
    private static String redis3_port2 = "redis3_port2";
    private static String redis3_port3 = "redis3_port3";
    private static String readMode = "redis_readmode";


    @Override
    public  String init() throws IOException
    {
	properties.load(new FileInputStream("src/main/resources/conf.properties"));
        Config config = new Config();
        config.setCodec(new org.redisson.client.codec.StringCodec());
		String addr1 = "redis://"+properties.getProperty(privateIP1)+":"+properties.getProperty(redis1_port1);
		String addr2 = "redis://"+properties.getProperty(privateIP1)+":"+properties.getProperty(redis1_port2);
		String addr3 = "redis://"+properties.getProperty(privateIP1)+":"+properties.getProperty(redis1_port3);
		String addr4 = "redis://"+properties.getProperty(privateIP2)+":"+properties.getProperty(redis2_port1);
		String addr5 = "redis://"+properties.getProperty(privateIP2)+":"+properties.getProperty(redis2_port2);
        String addr6 = "redis://"+properties.getProperty(privateIP2)+":"+properties.getProperty(redis2_port3);
		String addr7 = "redis://"+properties.getProperty(privateIP3)+":"+properties.getProperty(redis3_port1);
		String addr8 = "redis://"+properties.getProperty(privateIP3)+":"+properties.getProperty(redis3_port2);
		String addr9 = "redis://"+properties.getProperty(privateIP3)+":"+properties.getProperty(redis3_port3);
		//System.out.println(addr1+"......"+addr9);
		config.useClusterServers().setScanInterval(2000)
        	.addNodeAddress(addr1)
			.addNodeAddress(addr2)
			.addNodeAddress(addr3)
			.addNodeAddress(addr4)
			.addNodeAddress(addr5)
			.addNodeAddress(addr6)
			.addNodeAddress(addr7)
			.addNodeAddress(addr8)
			.addNodeAddress(addr9);
		
/*		config.useClusterServers().setScanInterval(2000)
			.addNodeAddress("redis://172.24.83.25:7001")
.addNodeAddress("redis://172.24.83.25:7002")
.addNodeAddress("redis://172.24.83.27:7003")
.addNodeAddress("redis://172.24.83.27:7004")
.addNodeAddress("redis://172.24.83.26:7005")
.addNodeAddress("redis://172.24.83.26:7006")
.addNodeAddress("redis://172.24.83.25:7007")
.addNodeAddress("redis://172.24.83.27:7008")
.addNodeAddress("redis://172.24.83.26:7009");
*/
		ClusterServersConfig clusterConfig = config.useClusterServers();
        ReadMode rm = ReadMode.MASTER;
        if(properties.getProperty(readMode).equals("master"))
        {
          rm = ReadMode.MASTER;
        }else if(properties.getProperty(readMode).equals("slave"))
        {
          rm = ReadMode.SLAVE;
        }else if(properties.getProperty(readMode).equals("master_slave"))
        {
          rm = ReadMode.MASTER_SLAVE;
        }
        clusterConfig.setReadMode(rm);
        clusterConfig.setLoadBalancer(new org.redisson.connection.balancer.RoundRobinLoadBalancer());


         redisson = Redisson.create(config);
        redisson2 = Redisson.create(config);


        return "redis init complete";
    }

    @Override
    public String read(String tableName, String rowkey, Set<String> fields,int clientFlag)
    {
        String s ="-";
        RBucket<String> bucket = redisson.getBucket(rowkey);
        if(clientFlag == 1)
        {
          bucket= redisson.getBucket(rowkey);
        }else if(clientFlag == 2)
        {
          bucket= redisson2.getBucket(rowkey);
        }


        try{
           // s = jedis2.get(rowkey);
            s=bucket.get();
        }

        catch (Exception e)
        {
            return "readerror";
        }
        return s;

        //System.out.println(s);;

    }
    public  String read2(String tableName, String rowkey, Set<String> fields,int clientFlag)throws IOException
    {
        String s ="-";
        RBucket<String> bucket = redisson2.getBucket(rowkey);

        try{
            // s = jedis2.get(rowkey);
            s=bucket.get();
        }

        catch (Exception e)
        {
            return "readerror";
        }
        return s;

    }

    @Override
    public  String insert( String key, String val, String nul1,String nul2,String nul3,int clientFlag) throws IOException
    {
        RBucket<Object> bucket = redisson.getBucket(key);;
        if(clientFlag == 1)
        {
          bucket = redisson.getBucket(key);
        }else if(clientFlag == 2)
        {
          bucket = redisson2.getBucket(key);

        }

        bucket.set(val);
       // jedis.set(key,val);
        return "redis insert complete";
    }
    public  String insert2( String key,  String val) throws IOException
    {

        RBucket<Object> bucket = redisson2.getBucket(key);
        bucket.set(val);
        // jedis.set(key,val);
        return "redis insert complete";
    }

    @Override
    public  String delete(String key,String nul1,String nul2) throws IOException
    {
        //jedis.del(key);
        RBucket<Object> bucket = redisson.getBucket(key);
        bucket.delete();
        //jedis.close();
        return "redis delete complete";
    }

    @Override
    public  void cleanup()
    {
        redisson.shutdown();
        redisson2.shutdown();
    }
}
