package com.github.tonivade.claudb;

import redis.clients.jedis.Jedis;

public class Loop
{
  public static void main(String[] args) throws InterruptedException
  {
    ClauDB claudb = new ClauDB("localhost", 7081);

    
    for (int i = 0; i < 1000; i++)
    {
      claudb.start();
      Thread.sleep(2000);

      Jedis jedis = new Jedis("localhost", 7081);
      System.out.println(jedis.select(4));
      jedis.close();
      
      claudb.stop();
      Thread.sleep(2000);
    }
    
  }
}
