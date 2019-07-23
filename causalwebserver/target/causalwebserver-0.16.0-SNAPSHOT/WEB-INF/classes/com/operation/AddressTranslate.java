package com.operation;
/**
 * @author: zhaole@act.buaa.edu.cn
 * @date: 12/3/18
 **/

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.policies.AddressTranslator;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class AddressTranslate implements AddressTranslator
{
  @Override
  public void init(Cluster cluster)
  {

  }
  final HashMap<InetSocketAddress, InetSocketAddress> privatePublicAddressMap = new HashMap<InetSocketAddress, InetSocketAddress>();
  {
    // privatePublicAddressMap.put(new InetSocketAddress("172.24.83.25", 9042), new InetSocketAddress("39.104.154.79", 9042));
    // privatePublicAddressMap.put(new InetSocketAddress("172.24.83.27", 9042), new InetSocketAddress("39.104.96.96", 9042));
    //  privatePublicAddressMap.put(new InetSocketAddress("172.24.83.26", 9042), new InetSocketAddress("39.104.153.41", 9042));

    privatePublicAddressMap.put( new InetSocketAddress("39.104.154.79", 9042),new InetSocketAddress("172.24.83.25", 9042));
    privatePublicAddressMap.put( new InetSocketAddress("39.104.96.96", 9042),new InetSocketAddress("172.24.83.27", 9042));
    privatePublicAddressMap.put(new InetSocketAddress("39.104.153.41", 9042),new InetSocketAddress("172.24.83.26", 9042));
  }
  @Override
  public InetSocketAddress translate(final InetSocketAddress inetSocketAddress)
  {
    return privatePublicAddressMap.get(inetSocketAddress);
  }
  @Override
  public void close()
  {

  }
}
