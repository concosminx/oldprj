package callable.sample;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

  public static void main(String args[]) {
    ExecutorService executor = Executors.newFixedThreadPool(2);
    List<Future<String>> list = new ArrayList<>();

    //submit tasks to executor
    for (int i = 0; i < 10; i++) {
      Future<String> future = executor.submit(new MyCallable("Task " + i));
      list.add(future);
    }

    //display results
    list.forEach((fu) -> {
      try {
        System.out.println(new Date() + "::" + fu.get());
      } catch (InterruptedException | ExecutionException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      }
    });

    //shut downs executor
    executor.shutdown();
  }
}
