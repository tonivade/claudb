package com.github.tonivade.claudb;

public class Loop
{
  public static void main(String[] args) throws InterruptedException
  {
    ClauDB claudb = new ClauDB("localhost", 7081);
    
    for (int i = 0; i < 1000; i++)
    {
      claudb.start();
      Thread.sleep(2000);
      
      claudb.stop();
      Thread.sleep(2000);
    }
  }
}
