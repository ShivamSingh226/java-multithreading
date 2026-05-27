# java-multithreading
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

### MultiThreading Concepts

