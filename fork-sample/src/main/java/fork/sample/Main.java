package fork.sample;

import java.util.concurrent.ForkJoinPool;

public class Main {

  public static void main(String[] args) {
    
    Problem test = new Problem();
    int nThreads = Runtime.getRuntime().availableProcessors();
    System.out.println(nThreads);
    
    Solver mfj = new Solver(test.getList());
    ForkJoinPool pool = new ForkJoinPool(nThreads);
    pool.invoke(mfj);
    long result = mfj.result;
    System.out.println("Done. Fork.Result: " + result);
    
    long sum = 0;
    for (int i = 0; i < test.getList().length; i++) {
      sum += test.getList()[i];
    }
    System.out.println("Done. Result: " + sum);
    
    
    
  }
  
}
