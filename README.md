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
