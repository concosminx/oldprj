package callable.sample;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class MyCallable implements Callable<String> {
  
  private final String taskName;

  public MyCallable(String taskName) {
    this.taskName = taskName;
  }
  
  @Override
  public String call() throws Exception {
    Random random = new Random();
    int i = random.nextInt(10);
    
    System.out.println(Thread.currentThread().getName() + "(" + taskName + ")" + " - wait: " + i + " second(s)");
    
    TimeUnit.SECONDS.sleep(i);
    return taskName;
  }

}
