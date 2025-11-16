/* Disciplina: Programacao Concorrente */
/* Prof.: Silvana Rossetto */
/* Laboratório: 11 */
/* Codigo: Exemplo de uso de futures */
/* -------------------------------------------------------------------*/

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static java.lang.Math.sqrt;  

import java.util.ArrayList;
import java.util.List;


//classe runnable
//class MyCallable implements Callable<Long> {
  //construtor
//  MyCallable() {}
 
  //método para execução
//  public Long call() throws Exception {
//    long s = 0;
//    for (long i=1; i<=100; i++) {
//      s++;
//    }
//    return s;
//  }
//}

class MyCallable implements Callable<Long> {
  private String string_id;
  private long start,end;
  public MyCallable(long Start,long End,long id){
    start=Start;
    end=End;
    string_id=String.valueOf(id);
  }

  long ehPrimo(long n) {
    long i;
    if(n<=1) return 0;
    if(n==2) return 1;
    if(n%2==0) return 0;
    for(i=3; i< sqrt(n)+1; i+=2) {
      if(n%i==0) return 0;
    }
    return 1;
  }

   //--metodo executado pela thread
   public Long call() throws Exception{
    long n;
    long count;
    String num;
    count=0;
    for (long i=start; i<end; i++) {
      n=ehPrimo(i);
      count+=n;
      num=String.valueOf(i);
      if(n==1) System.out.println("E primo o numero "+ num+" na thread" + string_id);
    }
    return count;
   }
}

//classe do método main
public class FutureHello  {
  private static final int N = 8;
  private static final int NTHREADS = 10;

  public static void main(String[] args) {
    //cria um pool de threads (NTHREADS)
    ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
    //cria uma lista para armazenar referencias de chamadas assincronas
    List<Future<Long>> list = new ArrayList<Future<Long>>();

    for (long i = 1; i < NTHREADS+1 & i< N+1; i++) {
      long start,end;
      int len;
      len=(int)(N/NTHREADS);
      if (N<NTHREADS) {
        start=i;
        end=i+1;
      }
      else{
        start=(i-1)*len;
        end=(i)*len;
        if(i==N) end=N;
      }
      Callable<Long> worker = new MyCallable(start,end,i);
      /*submit() permite enviar tarefas Callable ou Runnable e obter um objeto Future para acompanhar o progresso e recuperar o resultado da tarefa
       */
      Future<Long> submit = executor.submit(worker);
      list.add(submit);
    }

    //System.out.println(list.size());
    //pode fazer outras tarefas...

    //recupera os resultados e faz o somatório final
    long sum = 0;
    for (Future<Long> future : list) {
      try {
        sum += future.get(); //bloqueia se a computação nao tiver terminado
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }
    System.out.println("Numero de primos " + sum);
    executor.shutdown();
  }
}
