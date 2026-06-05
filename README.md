# Java-Multithreading
Concurrency, Multithreading and parallelization

## Implementing Runnable Interfaces using lambda expressions

```java
Runnable r1=()->{
    for(int i=0;i<10;i++){
        System.out.println("Runner1: "+i);
    }
};
```

## Implementing Runnable interfaces using anonymous Inner Class

```java
Thread t1 = new Thread(new Runnable() {
    @Override
    public void run() {
        /// Your method
    }
});
```

## Instantiating Runner class by extending Thread class (exhibiting Interrupted Exception by using Thread.sleep class)

```java
class Runner1 extends Thread{
    
    @Override
    public void run(){
        ///Your method
        for(int i=0;i<10;i++){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Runner1: "+i);
        }
    }
}

public static void main(String[] args) {
    Thread t1=new Runner1();
    t1.start();
    
    // Waiting for the thread to complete its implementation we use join method in try-catch block
    try{
        t1.join();
    }catch(InterruptedException e){
        throw new RuntimeException(e);
    }
    System.out.println("Finished with the t1 runner");
}
```
#### Listing the threads in java
```java
for(Thread t: Thread.getAllStackTraces.keySet()){
    System.out.println("Thread name: " + t.getName() + ", State: "+t.getState());
        }
```

#### Daemon Threads and Worker Threads
**Worker Threads** are not terminated by JVM. It waits for it to Finish.

**Daemon Threads** are ***Background Threads*** that stop when worker/user threads cease to exist

##### To set Daemon Threads
```java
t2.setDaemon(true); // By default it is set to false for all the threads
```

#### Setting priority of threads
```java
Thread low = new Thread(new Task(),"Low Priority Thread");
Thread medium = new Thread(new Task(),"Medium Priority Thread");
Thread high = new Thread(new Task(),"High priority task");

low.setPriority(Thread.MIN_PRIORITY);
medium.setPriority(Thread.NORM_PRIORITY);
high.setPriority(Thread.MAX_PRIORITY);
```
### Stack Memory and Heap Memory
| Stack | Heap |
| ---   | --- |
| Stores Method Calls, local variables and reference variable | Stores Objects and instance variables |
| Smaller in size, faster to access | larger in size, slower to access |
| Exists for the lifetime of the method calls, and automatically managed | Objects exists until they are garbage collected, memory also dynamically allocated |
| Every thread has its own stack | Heap memory is shared among threads |

### Synchronized (To efficiently handle concurrent processes in Heap Memory)

To ensure that the thread access one resource at a time. The increment method will be accessed only by one thread at a time.
```java
public synchronized static void increment(){
    counter++;
}
```
Every Java object has an intrinsic lock(monitor lock) associated with it
Built-in mechanism for JVM provides for synchronization  
Synchronized keyword makes a thread acquire the monitor lock to access that particular code  
Since every Java Object has only one intrinsic lock associated with it, when we declare multiple synchronized on various methods, all of them compete for one single intrinsic lock, which makes the process really slow.

**For fine-grained control**,  
Instead of  
```java
public synchronized void method1(){}
```
We can do  
```java
public void method1(){
    /// We can apply the lock only to method where we need control
    synchronized (this){
        
    }
}
```

For multiple locks and multiple process:  
```java
private Object object1= new Object();
private Object object2 = new Object();

public void method1(){
    /// We can apply the lock only to method where we need control
    synchronized (object1){

    }
}

public void method2(){
    /// We can apply the lock only to method where we need control
    synchronized (object2){

    }
}
```

Class-based locking and Object-based locking
```java
private static int counter1=0;

synchronized(ThreadExample.Class){
    counter1++;
}
```

Or

```java
class ClassLocking {
    private static void synchronized Method1() {

    }
}

Runnable task1= ClassLocking::instanceMethod;
new Thread(task1, "First Thread").start();
```

***Re-entrant Locks***
```java
public class ReentrantExample {
 
    public synchronized void outerMethod() {
        System.out.println("Entered outerMethod");
        innerMethod(); // Calling another synchronized method
        System.out.println("Exiting outerMethod");
    }
 
    public synchronized void innerMethod() {
        System.out.println("Entered innerMethod");
        // Do something
        System.out.println("Exiting innerMethod");
    }
 
    public static void main(String[] args) {
        ReentrantExample example = new ReentrantExample();
 
        Thread thread = new Thread(() -> {
            example.outerMethod();
        });
 
        thread.start();
    }
}
```
In this example, we should see that when a thread enters _outerMethod_ it calls _innerMethod_ and it enters without any issue because of re-entrant locks  

#### Producer-Consumer problem
```java
class Process{
    public void produce() throws InterruptedException{
        synchronized (this){
            System.out.println("In the prodcer method");
            wait(); // Release the lock
            System.out.println("Going to exit produce method");
        }
        
    }
    
    public void consume() throws InterruptedException{
        Thread.sleep(1000);
        
        synchronized (this){
            System.out.println("Running the same method...");
            notify();
            System.out.println("After the notify() method call in the consume method");
        }
        
    }
}
```

```java
public static void main(String[] args){
    var process=new Process();
    Thread t1=new Thread(()->{
        try{
            process.produce();
        }catch(InterruptedException e) (
                throw new RuntimeException(e);
        )
    });

    Thread t2=new Thread(()->{
        try{
            process.consume();
        }catch(InterruptedException e) (
        throw new RuntimeException(e);
        )
    });
    
    t1.start();
    t2.start();
    
}
```

It is non-deterministic so it doesn't rewards the longest-waiting thread(_Thread Starvation and Fairness_)  
***Difference between Wait and Sleep***  
Sleep is called on the **Thread**, while wait is called on the **Object**.  
Wait is an interrupter (That's why we need InterruptedException)  
**Wait** and **Notify** must happen in a synchronized block on the monitor object whereas sleep does not.  
Sleep operation does not release the locks it holds while on the other hand Wait releases the lock on the object that wait() is called on.

#### Resolving Producer and Consumer Problem

```java
import java.util.LinkedList;

class SharedBuffer {
    private List<Integer> buffer = new LinkedList<>();
    private int capacity;
    
    // 1st Thread pushes task 1,2,3,4,5
    //2nd Thread pops 5,4,3,2,1
    public synchronized void produce() throws InterruptedException{
        if(buffer.size()==capacity){
            System.out.println("Buffer full, producer waiting...");
            wait();
        }
        System.out.println("Adding items with the producer...");
        
        for(int i=0;i<capacity;i++){
            buffer.add(i);
            System.out.println("Added value: "+i);
        }
        notify();
    }
    public synchronized void consume() throws InterruptedException{
        if(buffer.size()<capacity){
            System.out.println("Buffer not full yet, consumer waiting...");
            wait();
        }
        
        while(!buffer.isEmpty()){
            int item=buffer.remove(0);
            System.out.println("Consumer removes: "+item);
            Thread.sleep(300);
        }
        
        notify();
    }
}
class Consumer implements Runnable{
    private SharedBuffer sharedBuffer;
    
    public Consumer(SharedBuffer sharedBuffer){
        this.sharedBuffer=sharedBuffer;
    }
    
    @Override
    public void run(){
        try{
            while(true){
                this.sharedBuffer.consume();
                Thread.sleep(500);
            }
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }
}

class Producer implements Runnable{
    private SharedBuffer sharedBuffer;

    public Producer(SharedBuffer sharedBuffer){
        this.sharedBuffer=sharedBuffer;
    }

    @Override
    public void run(){
        try{
            while(true){
                this.sharedBuffer.produce();
                Thread.sleep(500);
            }
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }
}
public class App {
    public static void main(String[] args) {
        var sharedBuffer=new SharedBuffer();
        
        Thread t1=new Thread(new Producer(sharedBuffer));
        Thread t2=new Thread(new Consumer(sharedBuffer));
        
        t1.start();
        t2.start();
    }
}
```

#### Joshua's Bloch Approach - When a thread wakes up spuriously

```java
while(buffer.size()<capacity){
            System.out.println("Buffer not full yet, consumer waiting...");
            wait();
}
```
While protects from:  
1. Multiple threads waking up simultaneously( As they have to check condition again and again in while loop). With the if loop, they don't check the condition again and they proceed
2. The condition may not be valid when they proceed with the logic
3. JVM spuriosly wakes up Thread, when no new notification

***Locks and Reentrant Locks***

**Lock**: A Java Interface more flexible than synchronized
**Re-entrant Locks**: Concrete Implementation of Lock as the thread that holds the lock can acquire it again and again without getting blocked

__Re-entrant Locks__ example:

```java
import java.util.concurrent.locks.ReentrantLock;

ReentrantLock lock=new ReentrantLock(true); // When marked true, it is FIFO(First-come, first served)

```
Fair lock ensures fairness and order, although slightly more overhead becase of maintaining the queue.  
While unfair lock result in higher throughput because of less context switches(we use it when performance is more important).  

```java
private static Lock lock=new ReentrantLock(true);

public static void increment(){
    try{
        lock.lock();
    }finally {
        lock.unlock();
        //unlock() We can unlock in some other part of the code too, if we want in case of Re-entrant lock
    }
}
```

```java
Class Worker{
private Lock lock = new ReentrantLock();
private Condition condition = lock.newCondition();

public void produce() throws InterruptedException {
    lock.lock();
    System.out.println("Produce method...");
    // wait
    condition.await();
    System.out.println("Again the producer method...");
    lock.unlock();
}

public void consume() throws InterruptedException {
    Thread.sleep(2000);
    lock.lock();
    System.out.println("Consumer method...");
    Thread.sleep(3000);
    // notify
    condition.signal();
    lock.unlock();
}
}

public class App {
    public static void main(String[] args) {
        Worker worker = new Worker();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    worker.produce();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
            }
        });


        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    worker.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        t1.start();
        t2.start();
        
        try{
            t1.join();
            t2.join();
        }catch (InterruptedException e){
            e.printStackTrace();    
        }
        
    }
}
```
|                                        Re-entrant Lock | Synchronized |
|-------------------------------------------------------:|--------------|
| _tryLock()_: Let you acquire the lock without blocking | Not possible |
|_tryLock(timeout, unit)_: Allows waiting for a certain time | Not supported |
| _lockInterruptibly()_: Interrupts the thread while in waiting state | Thread is blocked until lock is acquired |
| FIFO | No order |
| Multiple condition support __Condition()__ via __newCondition()__ | only __wait()__ and __notify()__ |
| Manual control of locks through _lock()_ and _unlock()_ method | Automatically managed by JVM |

- [X] Checked Exceptions: Todo
- [ ] Unchecked Exceptions: Todo

## Multi-Threading Concepts

| Deadlock | Livelock |
| --- | --- |
| Blocked, do nothing | Running but not making any progress |
| Each waiting for a lock | Each thread keeps responding to others |
| Frozen | Active |

### Locks and Cyclic Dependency

**Locks**: Thread should not be blocked indefinitely so we use _tryLock()_ method.  
**Avoid Cyclic Depenedency**: Each thread acquires the lock in same order to avoid any cyclic dependency in lock acquisition.  
**Randomness**: Especially in livelock, threads should try to acquire the locks randomly.

### Volatile
Java Threads executed by two independent CPU may cache the variables locally within its scope (Cache->Stack->CPU (_comprised by a Thread_))

To ensure that the thread initiated updates are visible to other threads as well, we use volatile keyword. A light synchronization mechanism ensuring a particular thread updates are visible to other threads.

**Volatile** enables the variable to be read from CPU (Main memory) instead of Cache memory  
Sometimes, variable maybe written to the main memory or they share the same cache or scope of a thread, so they may read from the same source without facing any inconsistency.  


### Atomic Integer
- Supports atomic operations, as `counter++` is not an atomic operation, so to handle this concurrently, we should be using `synchronized` keyword on its function
- Atomic Integer supports atomic operations, `counter.getAndIncrement()` is one such example.
- It supports ATomic operation for a single operation.
- If we do:  
```java
if(counter.get<10){
    counter.getAndIncrement();    
}
```
It may throw inconsistent result.

```java
public class AtomicInteger {
    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                increment();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                increment();
            }
        });
        t1.start();
        t2.start();
        try{
            t1.join();
            t2.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

    }

    public static void increment() {
        for(int i=0;i<10000;i++){
            counter.getAndIncrement();
        }
    }
}
```

#### Semaphores
Introduced by Djikstra  
Before Semaphore, no method was there to manage critical sections or shared resources between Thread and Process.  
Simple variables which can safely control access to shared resources and critical sections in concurrent systems.  
**Binary Semaphore**: Mutual Exclusion, a thread can access one resource at a time. _0_ if it is acquired, _1_ if it's available.  
**Counting Semaphore**: Arbitrary number(until it gets to zero) as a resource count, used when multiple resources available (10 DB connections, 5 thread pools).  
---

- Only maintain count, not the specifics
- Can work as a trigger for some useful web actions to be initiated
- Producer-Consumer problem can be implemented using Djikstra.

#### Mutex (Mutual Exclusion)

A Mutex( Mutual Exclusion ): A programming construct ensuring Thread Safety, by ensuring that multiple threads can't access the critical section at the same time.  
Only one thread can hold Mutex, and execute the code, other threads would be blocked from accessing it.  

| Mutex | Semaphore |
| --- | --- |
| Mutual Exclusion(One thread at a time) | Signalling, coordination and resource counting |
| Only One | Can be any arbitrary non-negative number |
| Has a single owner | No concept of ownership |
| Protects critcal section | Controls access to limited number of resources |
| Blocks if already locked | Blocks if count is 0 |
| Must be released by the thread which acquired it | Anyone can release it |
| `synchronized`, `ReentrantLock` | `Semaphore` from `java.util.concurrent` |

#### Semaphore coding example

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

enum Downloader {
    INSTANCE;

    private Semaphore semaphore = new Semaphore(3, true);

    public void download() {
        try {
            semaphore.acquire();
            downloadData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    private void downloadData() {
        try {
            System.out.println("Downloading data from the web...");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class App {
    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();

        for (int i = 0; i < 12; i++) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    Downloader.INSTANCE.download();
                }
            });
        }
    }
}
```

## Importance of Thread Pools

- Each Thread required 512 KB to 1 MB
- With each thread having its own execution context and competing for CPU, context-switching becomes a costly operation
- Manually handling each threads, joining the threads if necessary and handling exception.

**Thread Pools**:A collection of Worker Thread being reused to execute many tasks.

**ExecutorService** provides with threads and its implementations.
If all threads are busy, tasks are submitted to **BlockingQueue** data structure.  

`newFixedThreadPool(n)`: Initiated lazily,created on demand based on `corePoolSize`.

`newCachedThreadPool()`: Creates new threads as needed and reuses previously constructed threads when available.  
Removes idle threads after short timeout (usually 60 seconds)
Useful for short-lived, asynchronous tasks.

`newSingleThreadExecutor()`: Have only one worker thread, used to execute tasks sequentially and uses unbounded queue to sequence tasks in the waiting.  

`newScheduledThreadPool(n)`: Schedules task to run after a delay or schedule tasks to run periodically.  

If thread pool size is small, and scheduled tasks are long-running, it can result into thread-starvation. New tasks may be delayed. Use it to schedule tasks at regular intervals.  
#### Advantages of ThreadPool

- Submit tasks that return values using Callable and result wrapped in Future objects  
- No need to manually manage start() and join()  
- Threads are created once and can be reused for various tasks. Reduces overhead of thread creation and destruction.  

---

#### SingleThread Executor

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Task implements Runnable {
    private int id;

    public Task(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Task with id: " + id + " is in work - thread id: " + Thread.currentThread().getName());
        long duration = (long) (Math.random() * 5);

        try {
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class SingleThreadExecutor {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        
        for(int i=0;i<5;i++){
            executorService.execute(new Task(i));
        }
        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(1000,TimeUnit.MILLISECONDS)){
                executorService.shutdownNow();// If we want to ensure that executor stops immediately, otherwise comment this code to proceed with remaining tasks
            }
        }catch (InterruptedException e){
            executorService.shutdownNow();
        }
        
    }
}
```
#### Advantages of ThreadPoolExecutor

- Decouples task submission from execution:Submission of tasks without blocking main thread
- Multiple tasks can be queued up and they will be executed one after another so the lifecycle management of threads become easier.
- Tasks are run sequentially, so critical section are managed to avoid concurrency bugs.  

#### Fixed Thread Pool Executor Service

```java
ExecutorService executorService = Executors.newFixedThreadPool(10); // A fixed thread pool of 10 threads handling tasks and being reused again and again to manage tasks
```

#### ScheduledExecutor

```java
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class StockMarketUpdater implements Runnable {

    @Override
    public void run() {
        System.out.println("Updating and downloading data from the stock Market..");
    }
}

public class App {
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new StockMarketUpdater(), 1000, 2000, TimeUnit.MILLISECONDS);
    }
}
```

### Difference between Runnable and Callable
| Runnable | Callable |
|--- | --- |
|A task doesn't return anything | It returns a generic type (Type V) |
| Can't throw a checked exception | It can throw a checked exception |
| Can be executed using both _execute()_ and _submit()_ method | Only be executed with _submit()_ method of an _ExecutorService()_ that returns with **Future** object containing the result. | 

![Callable task returning Future Object](img.png)

#### Virtual Threads consider `future.get()` as a non-blocking operation at OS level

Callable method:

```java
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Processor implements Callable<String> {
    private int id;

    public Processor(int id) {
        this.id = id;
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(2000);
        return "Id: " + id;
    }

}

public class App {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(2);
        List<Future<String>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Future<String> future = service.submit(new Processor(i + 1));
            list.add(future);
        }
        for (Future<String> f : list) {
            try {
                System.out.println(f.get());
            } catch (InterruptedException | ExecutionException e){
                e.printStackTrace();
            }
        }
    }
}
```

### TO ensure synchronization along Collections Iterable List etc.
**Note**: It is blocking since it uses Intrinsic Lock
```java
List<Integer> nums=Collections.synchronizedList(new ArrayList<>());
```

![Collections Framework](img_1.png)

![Collections HashMap Framework](img_2.png)    
**Only Vector, Stack and HashTable are Thread Safe(Although we dont use HashTable, we use ConcurrentHashMap)**

**To resolve synchronization and concurrency more effectively**  

`CountDownLatch`: Decouples wait/count logic more effectively.
`new CountDownLatch(4)`

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Worker implements Runnable {
    private int id;
    private CountDownLatch countDownLatch;

    public Worker(int id, CountDownLatch countDownLatch) {
        this.id = id;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        doWork();
        countDownLatch.countDown();
    }

    private void doWork() {
        try {
            System.out.println("Thread with ID: " + this.id + " starts working");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

public class Latch {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(5);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        
        for(int i=0;i<5;i++){
            executorService.execute(new Worker(i,latch));
        }
        try {
            countDownLatch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("All tasks have been finished...");
        executorService.shutdown();
    }
}
```

#### Cyclic Barrier:

A group of tasks performing concurrent tasks and wait until they are all finished.  
In case of `CountDownLatch`, a single thread wait for the tasks.  

`CountDownLatch`: One shot event, while `CyclicBarrier` can be resued again and again. It has a runnable, which will run when the count reaches 0.

```java
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class BarrierWorker implements Runnable {
    private int id;
    private Random random;
    private CyclicBarrier barrier;

    public BarrierWorker(int id, CyclicBarrier barrier) {
        this.id = id;
        this.random = new Random();
        this.barrier = barrier;
    }

    @Override
    public void run() {
        doWork();
    }

    private void doWork() {
        System.out.println("The thread with the Id: " + this.id + " starts the work...");
        try {
            Thread.sleep(random.nextInt(3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println("After the await()...");

    }
}

public class BarrierClassExample {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(5);
        CyclicBarrier barrier=new CyclicBarrier(5, new Runnable(){
           @Override
           public void run(){
               System.out.println("All tasks have been finished...");
           }
            
        });
        
        for(int i=0;i<5;i++){
            service.execute(new BarrierWorker(i+1,barrier));
        }
        
        service.shutdown();
        
    }
}
```

| CyclicBarrier | CountDownLatch |
|---|---|
| Reused | Can be used only once |
| Multiple threads wait for each other | Main thread wait for worker thread |
| Calls Runnable Action |  No Runnable Support |
| All threads call await | Only one main thread call await |

## BlockingQueue
A thread-safe queue, blocks when you try to remove from an empty queue, wor you try to insert into full queue.  
Uses a ReentrantLock (true then FIFO, false then random)

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueExample {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        
        FirstWorker firstWorker=new FirstWorker(queue);
        SecondWorker secondWorker=new SecondWorker(queue);
        
        new Thread(firstWorker).start();
        new Thread(secondWorker).start();
        
    }
}
```
**FirstWorker**: Puts the value into queue.  
```java
@Override
public void run() {
    int counter = 0;

    while (true) {
        try {
            queue.put(counter);
            System.out.println("Putting item into the queue ... " + counter);
            counter++;
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
**SecondWorker**:
```java
@Override
public void run() {
    

    while (true) {
        try {
            int counter=queue.take();
            System.out.println("Taking item from the queue ... " + counter);
            
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### LinkedBlockingQueue
Two separate locks, one for putting and another one for removing. A key improvement in concurrency optimization.  
Can be both bounder, or unbounded.  
Useful in supporting concurrent operations.  

### DelayQueue

Adds a delay to perform `put()` and `take()` operation. It will block if the queue is full or empty for the respective operations.  
Use <mark>Delayed<mark> interface.
```java
import java.util.concurrent.TimeUnit;

class DelayWorker implements Delayed {
    private long duration;
    private String message;

    public DelayWorker(String message, long duration) {
        this.duration = duration;
        this.message = message;
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }

    @Override
    public int getDelay(TimeUnit timeUnit){
        return 0;
    }
}
```


```java

BlockingQueue<DelayedWorker> queue=new DelayQueue<>();
try{
   queue.put(new DelayedWorker("This is the first message...", 2000)); 
}catch(InterruptedException e){
    e.printStackTrace();
}

while(!queue.isEmpty()){
    try{
        System.out.println(queue.take());
    }catch(InterruptedException e){
        e.printStackTrace();
    }    
}
```

### PriorityBlockingQueue

A thread-Safe priorityQueue with heap data structure. 

Has O(logN) for addition and removal it has O(N).  
Has single Reentrant Lock.Unbounded.  
Blocks when we try to `take()` from an empty Queue. No fairness.

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

class FirstWorker implements Runnable {
    private PriorityBlockingQueue<String> queue;

    public FirstWorker(PriorityBlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {

    }
}

public class MainExample {
    public static void main(String[] args) {
        BlockingQueue<String> queue=new PriorityBlockingQueue<>();
        
        FirstWorker firstWorker=new FirstWorker(queue);
        new Thread(firstWorker).start();
    }
}
```

## ConcurrentHashMap  

Every hashMap is divided into segments(by default 16 elements in 1D array).  
`Collections.synchronize(new HashMap<>())`: It has intrinsic lock on entire HashMap.  


After Java 8, it introduces bin-level locking:  
- Read operations in ConcurrentHashMap is thread-safe, even without synchronized keyword.  
- Volatile fields to ensure visibility, because the value instead of being stored into cache is flushed into main memory, **happens-before** relationship is ensured:  
  If Action A happens before Action B, then side-effects of A is visible to B.  
- That's why even without acquiring locks, you can easily read the data without acquiring any lock or getting corrupted data.  
- **Fine-Grain Locking System (FGLS)**: Instead of segments, we have a lock on a single bucket(Single-Array data slot)  
- Insertion of first-value happens through a lock-free mechanism, **Compare and Swap**  
### Compare and Swap
- Atomic operation being handled through **Unsafe** or **VarHandle** directly access memory with Atomic Operations.  
- A mechanism to read and change data without acquiring lock on a low-level mechanism  

## Using TreeBin on ConcurrentHashMap
- After the `TREEFY_THRESHOLD` is crossed, that particular bucket is converted into Red-Black Tree, which can undergo lots of rotation and nodes can interchange.  
- So, a thread processing a root node, the root node may change and another thread can enter synchronized block on another root Node.  
- That's why <mark>TreeBin</mark> is used, which only points towards the root node of the _Red-Black_ Tree. Read-Operations are thread-safe, first write operation happens under CAS.
- After that if on a bucket, there is a LinkedList, the synchronization happens on head of the LinkedList and if there is Red-Black Tree under the hood of _TreeBin_ then that TreeBin is synchronized.

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class MapFirstWorker implements Runnable {
    private ConcurrentMap<String, Integer> map;

    public MapFirstWorker(ConcurrentMap<String, Integer> map) {
        this.map = map;
    }

    @Override
    public void run() {

        try {
            map.put("B", 12);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class ConcurrentMapExample {
    public static void main(String[] args) {
        ConcurrentMap<String, Integer> map = new ConcurrentHashMap<String,Integer>();
        MapFirstWorker firstWorker=new MapFirstWorker(map);
        new Thread(firstWorker).start();
    }
}
```

#### Slipping Condition
When two threads read a value and makes decision based on stale data, and then write-back conflicting and outdated values.  

To avoid it we use a thread-safe method,
`map.putIfAbsent("key","value");`
instead of  
```java
if(!map.containsKey("Key")){
    map.put("Key","Value");    
}
```

### Exchanger

Two threads exchange objects between them.

```java
import java.util.concurrent.Exchanger;

class FirstThread implements Runnable {
    private int counter;
    private Exchanger<Integer> exchanger;
    public FirstThread(Exchanger<Integer> exchanger){
        
        this.exchanger=exchanger;
    }
    @Override
    public void run(){
        while(true) {
            counter++;
            System.out.println("First Thread incremented the counter...");
            try {
                counter=exchanger.exchange(counter);
                System.out.println("First Thread to get the counter: "+counter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            
        }
    }
}
class SecondThread implements Runnable {
    private int counter;
    private Exchanger<Integer> exchanger;
    public SecondThread(Exchanger<Integer> exchanger){

        this.exchanger=exchanger;
    }
    @Override
    public void run(){
        while(true) {
            counter--;
            System.out.println("Second thread decremented the counter.");
            try {
                counter=exchanger.exchange(counter);
                System.out.println("Second Thread to get the counter: "+counter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    }
}
public class ExchangerExample{
    public static void main(String[] args) {
        Exchanger<Integer> exchanger=new Exchanger<>();
        
        FirstThread t1=new FirstThread(exchanger);
        SecondThread t2=new SecondThread(exchanger);
        
        new Thread(t1).start();
        new Thread(t2).start();
        
    }
}
```

### CopyOnWriteArray

No need for locking while reading; update, set and delete is a O(n) operation because thread makes a copy of the list and thus they are atomic operations.  
Reading from `copyOnWriteArray()` is thread safe. For example:  
Thread 1 is reading while Thread 2 is updating.  
If another thread and wants to update or modify it has to wait for Thread 2.  
After update operation is complete, Thread 1 updates the reference.  



```java
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConcurrentArray {
  private List<Integer> list;

  public ConcurrentArray() {
    this.list = new CopyOnWriteArrayList<>();
    this.list.addAll(Arrays.asList(0,0,0,0,0,0,0,0));
  }
  
}
public class MainApp{
  public static void main(String[] args) {
    ConcurrentArray concurrentArray=new ConcurrentArray();
    concurrentArray.simulate();
  }
}
```

## Java Streams API

- Sequence of elements from a source that supports data processing.  
- Introduced Functional Programming in Java( i.e, where programs are constructed by applying and modifying functions)  
- Streams rely on Lambda Expressions
- Used in Parallel Programming

### Pipelining
Intermediate source of operations(filtering, sorting, querying) returning streams as well.  
Data Source -> Intermediate Ops(Filtering, Sorting, Querying) -> Terminal Operation (Collect, Reduce)  

For example:  
`IntStream.range(0,5).forEach(x ->System.out.println(x+" "));`  
`IntStream.range(0,10).filter(x -> x>4).forEach(x -> System.out.println(x+" "));`

```java
import java.util.Comparator;
import java.util.stream.Collectors;

public class MainApp {
  public static void main(String[] args) {
    String[] names = {};
    List<Book> result=Stream.of(names).sorted(Comparator.reverseOrder()).forEach(System.out::println).collect(Collectors.toList());
    result.stream().forEach(System.out::println);
  }
}
```

- Provides interface to data structure representing sequenced set of values.  
- Fixed data structures whose elements are computed on demand(Lazy loading): Can be used only once.  

```java
List<Book> result=books.stream().filter(b -> b.getType()==Type.NOVEL).sorted(Comparator.comparing(Book::getAuthor)).map(Book::getTitle).collect(Collectors.toList());
result.stream().forEach(System.out::println);
```


## Exercise - Filtering

```json
Book [title=The Trial, author=Franz Kafka, pages=240, type=NOVEL]
Book [title=Ancient Greece, author=Robert F., pages=435, type=HISTORY]
Book [title=Ancient Rome, author=Robert F., pages=860, type=HISTORY]
Book [title=The Stranger, author=Albert Camus, pages=560, type=NOVEL]
```
 Select all the books where the title is made up of exactly two words.  
 ```java
books.stream()
        .filter(b -> b.getTitle().split(" ").length ==2)
        .collect(Collectors.toList())
        .forEach(System.out::println);
```

### External Iteration

```java
List<String> titles=new ArrayList<>();
Iterator<Book> iterator=books.iterator();

while(iterator.hasNext()){
    titles.add(iterator.next().getTitle());
}
```
- Array has continuous items next to each other  
- No parallel programming support  

### Internal Iteration

```java
List<String> titles2=books.stream().map(Book::getTitle).collect(Collectors.toList());
titles2.forEach(e -> System.out.println(e));
```

**Loop Fusion**: Merging of different operations in the same pass.  
**Short Circuiting**: Java don't need to process whole stream to produce a result.  


```java
List<String> words=Arrays.asList("Adam","Ana","Daniel");
List<Integer> lengths=words.stream().map(String::length).collect(Collectors.toList());
lengths.stream().forEach(System.out::println);

List<Integer> nums=Arrays.asList(1,2,3,4);
nums.stream().map(x -> x*x).collect(Collectors.toList())
        .forEach(System.out::println);

```

**FlatMap**: Map each array not with a stream but with the contents of that stream.  
For example: `[[1,3,5],[5,13]]` becomes `[1,3,5,13]` but with only `map()`, it becomes `Stream<String[]>`.  
With `flatMap()`, we get `Stream<String>`.  

```java
String[] array={"hello","shell"};
List<String> unique=Arrays.stream(array).map(w->w.split("")).flatMap(Arrays::stream).distinct().collect(Collectors.toList());

unique.stream().forEach(System.out::println);
```

## Exercise-Mapping

```json
Your task is that given two lists of numbers ([1, 2, 3], [4, 5]). Generate all pairs of possible numbers!

So the result in the case should be: (1,4), (1,5), (2,4),(2,5),(3,4),(3,5)

Good luck!
```

```json
List<Integer> nums1=Arrays.asList(1,2,3);
List<Integer> nums2=Arrays.asList(4,5);
List<List<Integer>> pairs=nums1.stream()
.flatMap(i -> nums2.stream().map(j -> Arrays.asList(i,j))).collect(Collectors.toList());
System.out.println(Arrays.toString(pairs.toArray()));
```

#### GroupingBy function
```java
var result=people.stream().collect(Collectors.groupingBy(People::getDepartment(), Collectors.summingInt(Person::getAge)));
var result=people.stream().collect(Collectors.groupingBy(People::getDepartment,Collectors.mapping(Person::getName,Collectors.toList())));// Will only output the required value in the 'Value' of the 'Key,Value'. For example: HR - [Adam], IT- [Kevin, Johnson]

System.out.println(result);
```

If we want to sort the keys:

```java
import java.util.stream.Collectors;

Collectors.groupingBy(
        classifier,
        TreeMap::new,
        downstreamCollector
)
```

```java
var result=people.stream().flatMap(p -> p.getDepartments().stream()
        .map(d->new AbstractMap.SimpleEntry<>(d,p)))
        .collect(Collectors.toList());
```  

### Partioning By

```java
var result=people.stream().collect(Collectors.partitioningBy(p->p.getName().length()<5,
        Collectors.mapping(p->p.getName(), Collectors.toList())));
```

## Optionals
```java
Optional<Integer> result=books.stream().reduce(INTEGER:: MAX);
result.ifPresent(System.out::println);
```
We avoid NullPointerException using this.  
Similarly, we have _OptionalInt_, _OptionalFloat_.  
So, essentially when we do `result.orElse(0)`, if the result doesn't exist instead of throwing exception it will throw __0__.  

Other methods: __allMatch()__, __noneMatch()__, __findFirst()__, and __findAny()__ .

## Parallelization 

```java
private static long sum(long n){
    return LongStream.rangeClosed(1,n).reduce(0L, Long::Sum);
    
}

private static long parallelSum(long n){
    return LongStream.rangeClosed(1,n).parallel().reduce(0L, Long::Sum);
}
```

```java

public static void main(String[] args){
    long currentTime=System.currentTimeMillis();
    
   int numOfPrimes= IntStream.rangeClosed(2, Integer.MAX_VALUE/100).filter(App::isPrime).count();
  System.out.println("Time Take: "+(System.currentTimeMillis()-currentTime));


  long currentTime=System.currentTimeMillis();

  int numOfPrimes= IntStream.rangeClosed(2, Integer.MAX_VALUE/100).parallel().filter(App::isPrime).count();
  System.out.println("Time Take: "+(System.currentTimeMillis()-currentTime));
}
public static boolean isPrime(long num){
    if(num<=1) return false;
    if(num==2) return true;
    if(num%2==0) return false;
    
    long maxDivisor=(long) Math.sqrt(num);
    
    for(int i=3;i<=maxDivisor;i+=2){
        if(num%i==0){
            return false;
        }
    }
    return true;
}
```

```java
package com.globalsoftwaresupport;

import java.io.Serializable;

public class Person implements Serializable {

	private static final long serialVersionUID = 1L;
	private int personId;

	public Person(int personId) {
		this.personId = personId;
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}
}
/// To convert Java Object into stream of bytes we use serializable.  
package com.globalsoftwaresupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.xml.internal.txw2.output.StreamSerializer;

public class ParallelSaveOperation {

  public static final String DIRECTORY = System.getProperty("user.dir") + "/test/";

  public static void main(String[] args) throws IOException {

    // create the directory
    Files.createDirectories(Paths.get(DIRECTORY));

    ParallelSaveOperation app = new ParallelSaveOperation();

    // generate a large number of Person objects
    List<Person> people = app.generatePeople(100000);

    // sequential algorithm
    long start = System.currentTimeMillis();
    people.stream().forEach(ParallelSaveOperation::save);
    System.out.println("Time taken sequential: " + (System.currentTimeMillis() - start));

    // parallel algorithm
    start = System.currentTimeMillis();
    people.parallelStream().forEach(ParallelSaveOperation::save);
    System.out.println("Time taken parallel: " + (System.currentTimeMillis() - start));
  }

  private static void save(Person person) {
    try (FileOutputStream fos =
                 new FileOutputStream(new File(DIRECTORY + person.getPersonId() + ".txt"))){
    } catch(IOException exception) {
      exception.printStackTrace();
    }
  }

  private List<Person> generatePeople(int num) {
    return Stream.iterate(0, n -> n + 1)
            .limit(num)
            .map(x -> {
              return new Person(x);
            })
            .collect(Collectors.toList());
  }
}

```
## Parallel Programming
Multi-Threading uses Time-splicing algorithm. In case of a single processor core, two threads uses time-splicing algorithm to access single processor core.  
**Drawbacks**:
- Load Balancing
- Communication factor between two threads.(Parallel Slowdown)
## Virtual Threads
Platform Threads are blocked by I/O operations.  

Virtual Threads executed by Platform Threads, or Carrier Threads.  
Virtual Threads blocked by I/O operations and Platform Thread will do the other kinds of works.  

Platform Threads are always related to underlying OS Threads.  

Platform Threads and Virtual Thread lie in JVM.  
- When a virtual Thread is blocked, JVM saves the state on heap memory and platform thread uses another Virtual Thread. But, platform thread remain blocked as it is bounded to the OS level thread.  
- No need to reuse it really lightweight, used and disposed
- Cheap to block, no OS level thread is blocked as JVM used carrier Thread.  
- Use very limited resources as it is used in HTTP operation  

```java
public class VirtualTask{
    public static void run(){
      
        try{
          Thread.sleep(2000);
        }catch(InterruptedException e){
           e.printStackTrace(); 
        }
        
    }
}
public class VirtualThread{
    public static void main(String[] args) throws InterruptedException{
        var factory=Thread.ofVirtual().name("virtual-",0).factory();
        
        var t1=factory.newThread(VirtualTask::run);
        var t2=factory.newThread(VirtualTask::run);
        
        t1.start();
        t2.start();
        
        // Daemon Threads: Virtual Threads are Daemon Threads
        t1.join();
        t2.join();
    }
}
```

Virtual Threads are daemon Threads, and JVM exits when non-daemon threads have completed their tasks. That's the reason we wait for these two threads.

```java
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ComparisonPlatformVirtual {
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    for (int i = 0; i < 1000; i++) {
      Thread.ofPlatform().start(() -> {
        try {
          Thread.sleep(Duration.ofSeconds(5));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }
    // Virtual Threads
    var service = Executors.newVirtualThreadPerTaskExecutor();
    for(int i=0;i<10000;i++){
        service.submit(()->{
            try{
                System.out.println("Thread "+Thread.currentThread());
                Thread.sleep(Duration.ofSeconds(10));
            }catch(InterruptedException e){
                e.printStackTrace();            
            }
        })
    }
  }
}
```

Sometimes, Virtual Threads can't be unmounted from the carrier:  
We have two reasons for this:  

- Synchronized method
```java
public void example{

    synchronized(App.this){
        
    }
}
```
- Reentrant Lock
```java
public void example{
    lock.lock();
    lock.unlock();
}// Better approach
```
- When a Virtual thread runs a native or foreign method.  

## Futures

While we do `.get()` method in Futures, it blocks the main thread.  

**CompletableFutures** is non-blocking and asynchronous. No need to call `.get()` method as it has in-built callback mechanism enabled on the main thread.

```java
import java.util.concurrent.ExecutorService;

ExecutorService cpuExecutor=Executors.newFixedThreadPool(5);
ExecutorService ioExecutor=Executors.newCachedThreadPool();

CompletableFuture.supplyAsync(()->"Hello World! ",cpuExecutor)
        .thenApplyAsync(s->s.toUpperCase(), ioExecutor)
        .thenApply(s-> s+ " something")
        .thenAccept(System.out::println);
```

**CompletableFuture** can be combined too.  

## Structured Concurrency

**Asynchronous and Non-Blocking operation** is good with _CompletableFuture_ . But submitted tasks may be running in background and thence hard to debug too.  
Well-defined blocks and we wait for all threads to complete execution.  

**Unstructured concurrency**: Large numbers of threads difficult to track.  
Asynchronous, Non-blocking threads.  
**Orphaned Threads**: Parent-Child relationships not present in case of Threads.  

**StructuredTaskScope** and **Subtask** classes can handle these parent-child relationships.  
- Waiting for all threads to finish execution before shutdown
- Shutdown when given thread fails
- Shutdown when first task succeeds

```java
import java.util.concurrent.StructuredTaskScope;

public class TaskMain {
  static void main(String[] args) {
    try (var scope = new StructuredTaskScope<String>()){
        var process1=new LongProcess(3, "result 1");
        var process2=new LongProcess(7, "result 2");
        
        Subtask<String> res1=scope.fork(process1);
        Subtask<String> res2=scope.fork(process2);
        
        scope.join();
        
        if(res1.state()==State.SUCCESS){
          System.out.println(res1.get());
        }
        if(res2.state()==State.SUCCESS){
          System.out.println(res2.get());
        }
        // Combine the results
        // get() will not block because the join() waits for the threads to get finished
        System.out.println(res1.get()+ " - "+res2.get());
      
      //Shutdown the scope after all child threads terminate
      
      
    }
  }
}



```


`try(var scope=new StructuredTaskScope.ShutdownOnFailure())` throws illegalStateException when any of the thread fails(Child Thread) it notifies Parent Thread(to shutDown).  
Similarly for shutdownOnSuccess:  
`try(var scope= new StructuredTaskScope.ShutdownOnSuccess<String>())`

### Subroutines,Coroutines and Continuation  
**Subroutines:** Single entry point, Subroutine/method returns some value. In Java, we cannot return any value, so we return back to the caller method.  
With the help of Continuation, we can proceed with that VirtualThread position which was saved on Heap Memory.  
The VT is reaccessed again with the help of Coroutines(Multiple entry points).  
Freeze the running of the coroutine using `yield()` method.  

Coroutine run() calls -> Goes into Coroutine(), calls `yield()` -> Then coroutine returns -> When Thread unblocks, coroutine run() gets called again -> and it continues from the same point

We need to store the stack frames on the heap memory as well.  
**Continuation**: Keeps the stack and code pointer so the stack can be recreated again when we call `run()` method again.  


### Scaling 
**Vertical Scaling**: Adding more resources to the single underlying resources in the form of CPU or memory.  

**Horizontal Scaling**: Dealing with multiple instances of same microservices.  
These instances share information with each other.  
 We can use Structured Concurrency and Virtual threads.  
Virtual threads can access way more requests while dealing several requests.  

