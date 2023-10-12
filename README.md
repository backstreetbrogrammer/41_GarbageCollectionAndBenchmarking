# Garbage Collection and Benchmarking

> This is a Java course to understand garbage collection, JVM tuning techniques and performance benchmarking.

Tools used:

- JDK 11
- Maven
- JUnit 5, Mockito
- IntelliJ IDE

## Table of contents

1. [Garbage Collection](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#chapter-01-garbage-collection)
    - [Introduction to Java Garbage Collector](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#introduction-to-java-garbage-collector)
    - [Generational Heaps](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#generational-heaps)
    - [Types of Garbage Collectors](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#types-of-garbage-collectors)
        - [Serial GC](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#serial-gc)
        - [Parallel/Throughput GC](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#parallelthroughput-gc)
        - [Concurrent Mark Sweep (CMS) GC](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#concurrent-mark-sweep-cms-gc)
        - [G1 (Garbage First)](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#g1-garbage-first)
        - [ZGC](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#zgc)
    - [Which Garbage Collector to use?](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#which-garbage-collector-to-use)
2. [Heap monitoring and analysis](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#chapter-02-heap-monitoring-and-analysis)
    - [Interview Problem 1 (Bullish - cryptocurrency exchange) - How to capture heap dumps for analysis?](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#interview-problem-1-bullish---cryptocurrency-exchange---how-to-capture-heap-dumps-for-analysis)
    - [Interview Problem 2 (Goldman Sachs) - What is memory leak in Java and how to identify and prevent it?](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#interview-problem-2-goldman-sachs---what-is-memory-leak-in-java-and-how-to-identify-and-prevent-it)
3. [Summary of most important JVM parameters](https://github.com/backstreetbrogrammer/41_GarbageCollectionAndBenchmarking#chapter-03-Summary-of-most-important-jvm-parameters)

---

## Chapter 01. Garbage Collection

### Introduction to Java Garbage Collector

In many other programming languages, programmers manage the creation and removal of objects to moderate memory usage.

In **Java**, programmers focus on application functionality while the **Garbage Collector (GC)** controls how memory is
used.

The GC automatically frees the memory space consumed by objects which are no longer used. When objects stored in memory
have become **unreachable** for the program, the GC detects them and cleans them up.

GC is at the center of **memory management** for Java applications which helps to avoid the accidental **memory leaks**
and **fragmentation** issues.

**_What is memory management in Java?_**

Memory management is an **automated** process that is executed by the Java Virtual Machine (JVM).

One responsibility of the JVM is overseeing the **heap**, which is the space where all the objects created by a Java
program are held. The heap is important because it stores information that the application can use to run more
efficiently.

The **Just-In-time (JIT)** Compiler, traditionally located in the Java Virtual Machine (JVM), is responsible for memory
disposal.

When memory is not relevant or useful to the program, the JIT is responsible for disposing of it. In the JIT, the
garbage collector locates unused objects to clear memory space.

When the garbage collector categorizes objects, objects are placed at different locations in the heap. There are
different storage locations in the heap, where objects are situated based on how long they've remained in the heap.

Objects can be moved into the tenured location after surviving many processes and can eventually become permanent in the
memory.

**_How Does a Java Garbage Collector Work?_**

The whole idea of a managed language – like Java – is that the complexity of such an essential and extensive
implementation as a GC is actually **hidden** for us.

Which means we don't need to care! Because a lot of developers are working hard on the runtime and the language itself,
the vast user base of developers can benefit from all the work that is happening **under the hoods.**

The initial GC in the original JVM was very limited and not working seamlessly, but after all the evolutions, the
current overhead of the GC is way less noticeable.

A GC process can follow different approaches and, in all cases, contains one or more of the following steps:

- **_Mark (= Trace)_**: starting from the application's root, all linked memory blocks that can be reached are
  **Painted**. Imagine this as a tree with branches, where all leaves are colored. When all endpoints of the branches
  are reached, the painted blocks can be considered as **Live,** while the remaining memory blocks that are not painted
  can be considered as **Non-Live.**
- **_Sweep_**: all the **Non-Live** objects are **cleared** from the heap memory.
- **_Compact_**: **Live** memory objects are brought closer together (`defragmentation`, `relocation`) to ensure big
  free memory blocks are available for new objects. Some collectors will have a **second pass** to update the references
  in the application to memory objects to make sure they are pointing to the correct locations in the memory.
- **_Copy_**: this is another method to improve how the memory is used. In this process, all the **Live** objects are
  moved to a **To** space, while the remaining objects in the **From** space can be considered as **Non-Live.**

There are multiple types of GCs depending on which of these approaches they use:

- Mark/Sweep/Compact
- Copy
- Mark/Compact

A few other terms related to how the GC is implemented are essential when we want to understand the GC process better:

- Single versus multiple passes:
    - **Single-pass**: multiple steps are handled in a single run.
    - **Multi-pass**: in a multi-pass, the steps are handled in different passes, one after the other.
- Serial versus Parallel:
    - **Serial**: one GC thread
    - **Parallel**: multiple GC threads
- Stop-The-World versus Concurrent:
    - **Stop-The-World**: the application is stopped while the GC cycle is running.
    - **Concurrent**: the GC is running **next to** the application and has no impact on the application execution.

**_Live Set and Allocation Rate_**

As described in the different stages, the **live set**, which contains all the objects still in use, is an essential
factor in the behavior of the GC.

If an application has a constant load and behavior, and objects are added and removed from the **live set** steadily,
its size will remain stable.

A growing **live set** can be caused by a **memory leak**.

The `-Xmx` flag defines an application's maximum heap size.

If the size of the **live set** approaches the `-Xmx` size, the JVM lacks free memory to store new objects and perform
the GC. This will decrease the performance.

To keep the size of our server well-dimensioned to run our application, we need to balance the amount of installed
memory and the `-Xmx` value with the actual size of the **live set**.

Over-dimensioning our server is just a waste of money. But to correctly define this dimension, the **allocation rate**
must be considered.

This **allocation rate** is a value based on the amount of memory allocated per time unit, for instance, `MB/sec`.

A high value can indicate that a lot of objects are being created, resulting in the fact that a lot of cleanups will be
needed. This will impact the frequency and/or duration of the GC pauses.

A good guideline for the heap size (`-Xmx`) is `2.5` to `5` times the size of the average **live set**. The higher the
**allocation rate**, the bigger the heap must be for optimal GC.

### Generational Heaps

Another technique used in GC is **generational heaps,** keeping **young** versus **old** objects in different areas of
the heap because:

- Most objects die young
- Few references from older to younger objects exist

Using this hypothesis, the Java heap is separated into two physical areas:

- **Young generation**: this is where new objects are allocated and where objects are stored which are not old enough
  to get promoted. This is typically a smaller set with a lot of garbage objects that is handled quickly by the GC.
  Typically, `Young Generation Stop-The-World` GCs are **single** passes. The young generation is further divided into
  sections known as **Eden** and the **Survivor spaces** to move young objects if they are used for a longer time.
- **Old generation**: objects that live longer are eventually promoted to the old generation. This set is handled less
  frequently by the GC but takes a longer time.

In many cases, the **Old generation** is larger than the **Young generation**, but not always. This depends on the
static working **live set** of the application and how elastic the boundary between the Young and Old generations is.

In region-based generational collectors (`C4` and `G1`), the size of the generations is **fluid** and **elastic**. Most
of the regions could be the **Young generation**, or most could be the **Old generation**.

In collectors like `CMS`, `Parallel`, and `Serial`, the boundary between the two generations was **fixed**, and the
ratio between new and old generation sizes may have to be tuned.

![GenerationalHeap](GenerationalHeap.PNG)

New objects get allocated into the **Eden** space until it fills up. During the GC, **live** objects (reachable objects)
in the **Eden** and **Survivor** space are copied to the other **Survivor** space.

If any objects become **old enough,** they are copied to the **Old generation** (i.e. they are **tenured**).

We can take advantage of the young generation system by focusing on local variables within methods that have a short
lifetime, so the GC can focus on a subset of the heap that can quickly be handled.

**String pool optimization**

**Java 8u20** has introduced one JVM parameter for reducing the unnecessary use of memory by creating too many instances
of the same String. This optimizes the heap memory by removing duplicate String values to a global single `char[]`
array.

We can enable this parameter by adding `-XX:+UseStringDeduplication` as a JVM parameter.

### Types of Garbage Collectors

Just like Java-the-language has evolved, the runtime and tools have evolved a lot, and different GCs have been part of
the JRE.

| Name                                              | isParallel | isConcurrent | isGenerational                  | JVM Option                                                                                                                                                | Java version                                     |
|---------------------------------------------------|------------|--------------|---------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------|
| Serial                                            | No         | No           | Yes                             | -XX:+UseSerialGC                                                                                                                                          |                                                  |
| Parallel / Throughput                             | Yes        | No           | Yes                             | -XX:+UseParallelGC                                                                                                                                        |                                                  |
| Concurrent Mark and Sweep (CMS)                   | Yes        | Partially    | Yes                             | -XX:+UseParNewGC <br/> or as of Java 9: <br/> -XX:+UseConcMarkSweepGC <br/> and if we want to define the number of threads <br/> -XX:ParallelCMSThreads=n | Deprecated since Java 9 and removed from Java 14 |
| G1 (Garbage First)                                | Yes        | Partially    | Yes                             | -XX:+UseG1GC                                                                                                                                              | JDK 7 (update 4)                                 |
| Shenandoah GC                                     | Yes        | Fully        | No (upto JDK 17, may be later?) | -XX:+UseShenandoahGC                                                                                                                                      | JDK 15                                           |
| ZGC                                               | Yes        | Fully        | No (upto JDK 17, may be later?) | -XX:+UseZGC                                                                                                                                               | JDK 15                                           |
| C4 - Continuously Concurrent Compacting Collector | Yes        | Fully        | Yes                             | None, this is the default in Azul Zulu Prime                                                                                                              | Only in Azul Zulu Prime                          |

#### Serial GC

The serial collector uses a **single thread** to perform all the garbage collection work and uses **stop-the-world**,
meaning it freezes all application threads when it runs.

It's selected by default on certain small hardware and operating system configurations, or it can be explicitly enabled
with the following argument:

```
java -XX:+UseSerialGC Application
```

**Pros**:

- Without inter-thread communication overhead, it's relatively efficient.
- It's suitable for client-class machines and embedded systems.
- It's suitable for applications with small datasets.
- Even on multiprocessor hardware, if data sets are small (up to `100 MB`), it can still be the most efficient.

**Cons**:

- It's not efficient for applications with large datasets.
- It can't take advantage of multiprocessor hardware.

#### Parallel/Throughput GC

It's the default GC of the JVM from **Java 5** until **Java 8**, or it can be explicitly enabled as:

```
java -XX:+UseParallelGC Application
```

Unlike Serial Garbage Collector, it uses **multiple threads** for managing heap space, but it also freezes other
application threads while performing GC.

If we use this GC, we can specify maximum garbage collection threads and pause time, throughput, and footprint (heap
size):

- The **number of garbage collector threads** can be controlled with the command-line option:
  `-XX:ParallelGCThreads=<N>`
- The **maximum pause time goal** (a hint to the garbage collector that pause time of `<N> milliseconds` or less is
  desired) is specified with the command-line option: `-XX:MaxGCPauseMillis=<N>`
- The time spent doing garbage collection versus the time spent outside garbage collection is called the **maximum
  throughput target** and can be specified by the command-line option: `-XX:GCTimeRatio=<N>`
- The **maximum heap footprint** (the amount of heap memory that a program requires while running) is specified using
  the option: `-Xmx<N>`

**Pros**:

- It can take advantage of multiprocessor hardware.
- It's more efficient for larger data sets than serial GC.
- It provides high overall **throughput**.
- It attempts to minimize the **memory footprint**.

**Cons**:

- Applications incur long pause times during stop-the-world operations.
- It doesn't scale well with heap size.

#### Concurrent Mark Sweep (CMS) GC

CMS is mostly concurrent collector, meaning, it performs some expensive work **concurrently** with the application.

It's designed for low latency by eliminating the long pause associated with the full GC of parallel and serial
collectors.

CMS can be explicitly enabled as:

```
java -XX:+UseConcMarkSweepGC Application
```

The core Java team deprecated it as of **Java 9** and completely removed it in **Java 14**.

**Pros**:

- It's great for low latency applications as it minimizes pause time.
- It scales relatively well with heap size.
- It can take advantage of multiprocessor machines.

**Cons**:

- It's deprecated as of Java 9 and removed in Java 14.
- It becomes relatively inefficient when data sets reach gigantic sizes or when collecting humongous heaps.
- It requires the application to share resources with GC during concurrent phases.
- There may be throughput issues as there's more time spent overall in GC operations.
- Overall, it uses more CPU time due to its mostly concurrent nature.

#### G1 (Garbage First)

G1 (Garbage First) Garbage Collector is designed for applications running on multi-processor machines with large memory
space. It's available from the **JDK7 Update 4** and in later releases.

Unlike other collectors, the G1 collector partitions the heap into a set of **equal-sized** heap regions, each a
contiguous range of virtual memory.

When performing garbage collections, G1 shows a concurrent global marking phase (i.e. phase 1, known as **Marking**) to
determine the liveliness of objects throughout the heap.

After the mark phase is complete, G1 knows which regions are mostly empty. It collects in these areas first, which
usually yields a significant amount of free space (i.e. phase 2, known as **Sweeping**).

G1 uses multiple background GC threads to scan and clear the heap just like **CMS**. Actually, the core Java team
designed G1 as an improvement over CMS, patching some of its weaknesses with additional strategies.

In addition to the incremental and concurrent collection, it tracks previous application behavior and GC pauses to
achieve **predictability**.

It then focuses on reclaiming space in the most efficient areas first — those mostly filled with garbage. We call it
**Garbage-First** for this reason.

Since Java 9, G1 is the default collector for server-class machines.

G1 works best for applications with very strict pause-time goals and a modest overall throughput, such as real-time
applications like trading platforms or interactive graphics programs.

To enable the G1 Garbage Collector, we can use the following argument:

```
java -XX:+UseG1GC Application
```

**Pros:**

- It's very efficient with gigantic datasets.
- It takes full advantage of multiprocessor machines.
- It's the most efficient in achieving pause time goals.

**Cons:**

- It's not the best when there are strict throughput goals.
- It requires the application to share resources with GC during concurrent collections.

#### ZGC

ZGC (Z Garbage Collector) is a scalable low-latency garbage collector that debuted in **Java 11** as an experimental
option for _Linux_. **JDK 14** introduced ZGC under the _Windows_ and _macOS_ operating systems. ZGC has obtained the
production status from **Java 15** onwards.

ZGC performs all expensive work concurrently, without stopping the execution of application threads for more than
`10 ms`, which makes it suitable for applications that require low latency.

It uses load barriers with colored pointers to perform concurrent operations when the threads are running, and they're
used to keep track of heap usage.

Reference coloring (colored pointers) is the core concept of ZGC. It means that ZGC uses some bits (metadata bits) of
reference to mark the state of the object.

It also handles heaps ranging from `8MB` to `16TB` in size. Furthermore, pause times don’t increase with the heap,
live-set, or root-set size.

Similar to **G1**, **Z Garbage Collector** partitions the heap, except that heap regions can have **different** sizes.

To enable the Z Garbage Collector, we can use the following argument in JDK versions **lower than 15**:

```
java -XX:+UnlockExperimentalVMOptions -XX:+UseZGC Application
```

**From version 15** on, we don’t need experimental mode on:

```
java -XX:+UseZGC Application
```

**_Use Case 1_**

GCs have evolved a lot in recent decades. As a Java developer, we need to be aware that some tips for older GC
generations are not applicable anymore.

**C4**, **ZGC**, and **Shenandoah** are truly **concurrent**.

The pause times with these modern GCs are tiny, often units of milliseconds or even lower.

The size of the live set (objects that cannot be collected because they have references that may still be used in the
future) still determines the duration of the GC cycles, but the application is not paused while they are running.

The pause times do not scale with an increase in live set or heap size.

Traditionally, there has been a conscious attempt to design applications in such a way as to avoid needing larger Java
heaps.

One thing that developers still need to be careful about is avoiding leaks in the Java heap that can lead to **high live
sets** for GC.

The duration of, and CPU consumption by, most modern GCs is proportional to the size of live set. The Java ecosystem has
several tools that can help analyze live sets and identify problems.

**_Use Case 2_**

The GC has a significant impact on how your application behaves. Still, as a developer, we should also be aware that
certain coding practices can have an impact on how Java uses memory, and some problems can also get fixed with a
code change!

One of the examples where we have seen such wins is in **statistics** and **parser applications**, where a lot of
data is copied and only used once.

Creating and using **short-lived small** objects or `ArrayLists` is not a problem.

But when **large** data structures are used in a **"create to discard"** mode,the memory allocation rate can get out of
hand, and re-use of data structures can be beneficial.

An example would be single-use large buffers or arrays containing millions of objects of the same size.

A generational GC that is optimized to make the difference between young and old objects works best when there are two
**stereotypes** of data:

- Transactional data: objects that are created during a transaction or event and die within seconds or milliseconds.
- Reference data: data loaded once and referenced (read) but not modified by a transaction.

On the other hand, the **"worst"** kind of memory for a GC is a **rolling buffer (FIFO)**, where data lives for minutes
or hours. This is not a programming issue but has a **"business"** reason – for example, when a rolling transaction log,
session buffer, or similar must be used.

When an application is constantly modifying its **"old"** long-lived data at a high rate, then non-concurrent GCs sooner
or later run into trouble and need a full GC.

**_Use Case 3_**

For specific projects, it is clear that the heap size is causing long pauses in the application execution caused by the
Garbage Collector.

For example, projects where a `100GB` heap is used can expect pause times of over `10 seconds` when the GC is cleaning
up the memory.

In other cases, for example, financial and gaming applications, a smaller heap of `10GB` size which stops for hundreds
of **milliseconds** can already be a big problem.

Anyhow, having a Garbage Collector that doesn't stop our application completely for an unpredictable time is essential
for every project that expects consistent short response times; low latency in other words.

**Clusters** are another example where we have seen problems caused by the GC.

When one node with a big heap is considered dead because it is not responding during a GC cycle, a process is started to
spin up a new node and redistribute the data. But suddenly, the node that is considered to be dead re-appears after the
GC cycle, causing a chain of undesired events in the cluster.

### Which Garbage Collector to use?

For many applications, the choice of the collector is never an issue, as the **JVM default** usually suffices.

That means the application can perform well in the presence of garbage collection with pauses of acceptable frequency
and duration.

However, this isn't the case for a large class of applications, especially those with humongous datasets, many threads,
and high transaction rates.

In IT project management, there is a famous rule: "You need to choose between speed, quality, and cost. But you can only
have 2 out of these 3."

There seems to be a consensus that the same applies in regard to running an application. We need to pick two of the
following:

- Very low latency
- Very high throughput
- Lowest resource usage (CPU and memory)

But there is actually a fourth element we should add here: **"Good Engineering!"**

**_Variables to Consider_**

**Heap Size**

This is the total amount of working memory allocated by the OS to the JVM. Theoretically, the larger the memory, the
more objects can be kept before collection, leading to longer GC times.

The minimum and maximum heap sizes can be set using `-Xms=<n>` and `-Xmx=<m>` command-line options.

**Application Data Set Size**

This is the total size of objects an application needs to keep in memory to work effectively. Since all new objects are
loaded in the young generation space, this will definitely affect the maximum heap size and, hence, the GC time.

**Number of CPUs**

This is the number of cores the machine has available. This variable directly affects which algorithm we choose. Some
are only efficient when there are multiple cores available, and the reverse is true for other algorithms.

**Pause Time**

The pause time is the duration during which the garbage collector stops the application to reclaim memory. This variable
directly affects latency, so the goal is to limit the longest of these pauses.

**Throughput**

By this, we mean the time processes spend actually doing application work. The higher the application time vs. overhead
time spent in doing GC work, the higher the throughput of the application.

**Memory Footprint**

This is the working memory used by a GC process. When a setup has limited memory or many processes, this variable may
dictate scalability.

**Promptness**

This is the time between when an object becomes dead and when the memory it occupies is reclaimed. It's related to the
heap size. In theory, the larger the heap size, the lower the promptness as it will take longer to trigger collection.

**Latency**

This is the responsiveness of an application. GC pauses affect this variable directly.

**Java Version**

As new Java versions emerge, there are usually changes in the supported GC algorithms and also the default collector. We
recommend starting off with the default collector as well as its default arguments. Tweaking each argument has varying
effects depending on the chosen collector.

**Concurrent Garbage Collectors**

When the GC is concurrent, it shares the resources with application threads running concurrently.

Thus, the duration of the GC cycle can be impacted by the level of CPU load on the system or inside a container.

A **Stop-The-World** GC does not face this issue since it stops all the Java threads when it runs.

Thus, if the system is highly saturated, a concurrent GC can take significant time and introduce allocation pauses.

To reap full benefit from concurrent GC, it is advisable to keep the CPU load average below the number of cores
available.

Of course, the eventual GC behavior will depend on a combination of factors – live set, allocation rate, and CPU load
average.

---

## Chapter 02. Heap monitoring and analysis

One of the core benefits of Java is the automated memory management with the help of the built-in Garbage Collector (or
GC for short). The GC implicitly takes care of allocating and freeing up memory, and thus is capable of handling the
majority of memory leak issues.

While the GC effectively handles a good portion of memory, it doesn't guarantee a foolproof solution to memory leaking.
The GC is pretty smart, but not flawless. Memory leaks can still sneak up, even in the applications of a conscientious
developer.

### Interview Problem 1 (Bullish - cryptocurrency exchange) - How to capture heap dumps for analysis?

A heap dump is a snapshot of all the objects that are in memory in the JVM at a certain moment. They are very useful to
troubleshoot memory-leak problems and optimize memory usage in Java applications.

Heap dumps are usually stored in binary format `hprof` files. We can open and analyze these files using tools like
`jhat` or `JVisualVM`.

For Eclipse users: [MAT](https://www.vogella.com/tutorials/EclipseMemoryAnalyzer/article.html).

**_JDK Tools_**

The JDK comes with several tools to capture heap dumps in different ways. All these tools are located under the `bin`
folder inside the `JAVA_HOME` directory. Therefore, we can start them from the command line as long as this directory is
included in the system path.

**_jmap_**

`jmap` is a tool to print statistics about memory in a running JVM. We can use it for local or remote processes.

```
jmap -dump:[live],format=b,file=<file-path> <pid>

// example
jmap -dump:live,format=b,file=/tmp/dump.hprof 13647
```

Arguments used:

- **live**: if set, it only prints objects which have active references and discards the ones that are ready to be
  garbage collected. This parameter is optional.
- **format=b**: specifies that the dump file will be in binary format. By default, its binary only.
- **file**: the file where the dump will be written to
- **pid**: id of the Java process

We can get the `pid` of a Java process by using the `jps` command.

**_jcmd_**

`jcmd` tool works by sending command requests to the JVM. We have to use it in the same machine where the Java process
is running.

One of its many commands is the `GC.heap_dump`. We can use it to get a heap dump just by specifying the `pid` of the
process and the output file path:

```
jcmd <pid> GC.heap_dump <file-path>

// example
jcmd 13647 GC.heap_dump /tmp/dump.hprof
```

As with `jmap`, the dump generated is in **binary** format.

**_JVisualVM_**

`JVisualVM` is a tool with a graphical user interface that lets us monitor, troubleshoot, and profile Java applications.

It can be dowloaded as open source from: [VisualVM](https://visualvm.github.io/)

One of its many options allows us to capture a **heap dump**. If we right-click on a Java process and select the
**"Heap Dump"** option, the tool will create a heap dump and open it in a new tab:

![HeapDump](HeapDump.PNG)

**_Capture a Heap Dump using JVM options_**

Above JDK tools methods help to capture heap dumps **manually** at a specific time. In some cases, we want to get a heap
dump when a `java.lang.OutOfMemoryError` occurs to help us investigate the error.

For these cases, Java provides the `HeapDumpOnOutOfMemoryError` and `HeapDumpPath` command-line option, which
generates a heap dump when a `java.lang.OutOfMemoryError` is thrown:

```
java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=<file-or-dir-path> Application
```

There’s no overhead when running an application with this option. Therefore, it’s highly recommended to always use
this option, especially in production.

### Interview Problem 2 (Goldman Sachs) - What is memory leak in Java and how to identify and prevent it?

A Memory Leak is a situation where there are objects present in the heap that are no longer used, but the garbage
collector is unable to remove them from memory, and therefore, they're unnecessarily maintained.

A memory leak is bad because it blocks memory resources and degrades system performance over time. If not dealt with,
the application will eventually exhaust its resources, finally terminating with a fatal `java.lang.OutOfMemoryError`.

There are two different types of objects that reside in Heap memory, **referenced** and **unreferenced**.

Referenced objects are those that still have active references within the application, whereas unreferenced objects
don't have any active references.

The garbage collector removes unreferenced objects periodically, but it never collects the objects that are still being
referenced. This is where memory leaks can occur:

![MemoryLeak](MemoryLeak.PNG)

**_Memory Leak through `static` fields_**

In Java, `static` fields have a life that usually matches the entire lifetime of the running application (unless
`ClassLoader` becomes eligible for garbage collection).

Suppose we have a class like this:

```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class StaticMemoryLeakDemo {

    public static List<Double> list = new ArrayList<>();

    public void populateList() {
        for (int i = 0; i < 10_000_000; i++) {
            list.add(ThreadLocalRandom.current().nextDouble());
        }
        System.out.println("Debug Point 2");
    }

    public static void main(final String[] args) {
        System.out.println("Debug Point 1");
        new StaticMemoryLeakDemo().populateList();
        System.out.println("Debug Point 3");
    }
}
```

If we analyze the heap memory during this program execution, then we’ll see that between **Debug points 1 and 2**, the
heap memory increased as expected.

But when we leave the `populateList()` method at the **debug point 3**, the heap memory isn't yet garbage collected.

However, if we just drop the keyword `static` and make `List` as non-static, then it'll bring a drastic change to the
memory usage as all the memory of the list is garbage collected because we don't have any reference to it.

Thus, ff collections or large objects are declared as `static`, then they remain in the memory throughout the lifetime
of the application, thus blocking vital memory that could otherwise be used elsewhere.

To prevent it:

- Minimize the use of `static` variables.
- When using `singletons`, rely upon an implementation that lazily loads the object, instead of eagerly loading.

**_Memory Leak through unclosed resources_**

Whenever we make a new connection or open a stream like database connections, input streams, and session objects, the
JVM allocates memory for these resources.

Forgetting to close these resources can block the memory, thus keeping them out of the reach of the GC. This can even
happen in case of an exception that prevents the program execution from reaching the statement that's handling the code
to close these resources.

In either case, the open connection left from the resources consumes memory, and if we don't deal with them, they can
deteriorate performance, and even result in an `OutOfMemoryError`.

To prevent it:

- Always use `finally` block to close resources.
- The code (even in the `finally` block) that closes the resources shouldn't have any exceptions itself.
- When using `Java 7+`, we can make use of the `try-with-resources` block.

**_Memory Leak through improper `equals()` and `hashCode()` implementations_**

When defining new classes, a prevalent oversight is not writing proper overridden methods for the `equals()` and
`hashCode()` methods.

`HashSet` and `HashMap` use these methods in many operations, and if they're not overridden correctly, they can become a
source for potential memory leak problems.

Suppose we have a `Student` class as:

```java
public class Student {

    private final String name;

    public Student(final String name) {
        this.name = name;
    }
}
```

Now we'll insert duplicate `Student` objects into a `Map` that uses this key.

Remember that a `Map` can't contain duplicate keys:

```java
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class StudentTest {

    @Test
    void testMemoryLeakWhenNoEqualsOrHashCodeMethodImplemented() {
        final Map<Student, Integer> map = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            map.put(new Student("John"), 1);
        }
        assertNotEquals(1, map.size());
    }
}
```

In fact, the `map` size is `100` instead of `1`!

Since, we haven't defined the proper `equals()` and `hashCode()` method, the duplicate objects pile up and increase the
memory, which is why we see more than one object in the memory.

To prevent it:

- As a rule of thumb, when defining new entities, always override the `equals()` and `hashCode()` methods.
- It's not enough to just override, these methods must be overridden in an optimal way as well.

**_Memory Leak through inner classes that reference outer classes_**

This happens in the case of **non-static** inner classes (anonymous classes). For initialization, these inner classes
always require an instance of the enclosing class.

Every non-static inner class has, by default, an implicit reference to its containing class. If we use this inner class’
object in our application, then even after our containing class' object goes out of scope, it won't be garbage
collected.

This can be an issue if the outer class holds the reference to lots of bulky objects and has a non-static inner class.
When we create an object of just the inner class, the implicit reference to heavy outer class will not be eligible
for garbage collection.

To prevent it:

- Migrating to the latest version of Java that uses modern Garbage Collectors such as `ZGC` that uses root references
  to find unreachable objects. Since the references are found from the **root**, this will solve the cyclic problem,
  like the anonymous class holding reference to the container class.
- If the inner class doesn't need access to the containing class members, consider turning it into a `static` class.

**_Memory Leak through `finalize()` method_**

Use of finalizers is yet another source of potential memory leak issues. Whenever a class' `finalize()` method is
overridden, then objects of that class aren't instantly garbage collected.

Instead, the GC **queues** them for finalization, which occurs at a later point in time.

Additionally, if the code written in the `finalize()` method isn't optimal, and if the finalizer queue can't keep up
with the Java garbage collector, then sooner or later our application is destined to meet an `OutOfMemoryError`.

To prevent it:

- We should always avoid finalizers.

**_Memory Leak through `ThreadLocal`_**

(asked in Goldman Sachs interview in details)

`ThreadLocal` is a construct that gives us the ability to isolate state to a particular thread, and thus allows us to
achieve thread safety.

When using this construct, each thread will hold an implicit reference to its copy of a `ThreadLocal` variable and will
maintain its own copy, instead of sharing the resource across multiple threads, as long as the thread is alive.

Despite its advantages, the use of `ThreadLocal` variables is controversial, as they're infamous for introducing memory
leaks if not used properly.

Sloppy use of **thread pools** in combination with sloppy use of **thread locals** can cause unintended object
retention, as has been noted in many places. But placing the blame on thread locals is unwarranted.

`ThreadLocals` are supposed to be garbage collected once the holding thread is no longer alive. But the problem arises
when we use `ThreadLocals` along with modern application servers.

Modern application servers use a pool of threads to process requests, instead of creating new ones (for example, the
`ExecutorService`). Moreover, they also use a separate `Classloader`.

Since **Thread Pools** in application servers work on the concept of thread reuse, they're never garbage collected;
instead, they're reused to serve another request.

If any class creates a `ThreadLocal` variable, but doesn't explicitly remove it, then a copy of that object will remain
with the worker `Thread` even after the application is stopped, thus preventing the object from being garbage collected.

To prevent it:

- It's good practice to **clean-up** `ThreadLocals` when we're no longer using them. `ThreadLocals` provide the
  `remove()` method, which removes the current thread's value for this variable.
- Don't use `ThreadLocal.set(null)` to clear the value. It doesn't actually clear the value, but will instead look up
  the `Map` associated with the current thread and set the key-value pair as the current thread and `null`,
  respectively.
- It's best to consider `ThreadLocal` a resource that we need to close in a `finally()` block, even in the case of an
  exception:

```
try {
    threadLocal.set(threadId);
    //... further processing
}
finally {
    threadLocal.remove();
}
```

OR, another option is to create our custom `ThreadPoolExecutor` like this:

```
public class ThreadLocalAwareThreadPool extends ThreadPoolExecutor {

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        // Call remove on each ThreadLocal
    }
}
```

`ThreadPoolExecutor` class provides a custom hook implementation for the `beforeExecute()` and `afterExecute()` methods:

- The thread pool will call the `beforeExecute()` method before running anything using the borrowed thread.
- The thread pool will call the `afterExecute()` method after executing our logic and returning the thread to the pool.

**_Other ways to deal with memory leaks_**

**Enable Profiling**

Java profilers are tools that monitor and diagnose the memory leaks through the application. They analyze what's going
on internally in our application, like how we allocate memory.

Using profilers, we can compare different approaches and find areas where we can optimally use our resources.

Most popular Java profilers available:

- [JProfiler](https://www.ej-technologies.com/products/jprofiler/overview.html)
- [YourKit](https://www.yourkit.com/java/profiler/)
- [VisualVM](https://visualvm.github.io/)
- [NetBeans Profiler](https://netbeans.apache.org/kb/docs/java/profiler-intro.html)
- [IntelliJ Profiler](https://lp.jetbrains.com/intellij-idea-profiler/)
- [Java Mission Control](https://www.oracle.com/java/technologies/jdk-mission-control.html)
- [New Relic](https://newrelic.com/)
- [Prefix](https://stackify.com/prefix/)

**Verbose Garbage Collection**

By enabling verbose garbage collection, we can track the detailed trace of the GC.

To enable this, we need to add the following to our JVM configuration:

```
java -verbose:gc Application
```

By adding this parameter, we can see the details of what’s happening inside the GC:

![verboseGC](verboseGC.PNG)

**Use Reference Objects**

We can resort to reference objects in Java that come built-in with the `java.lang.ref` package to deal with memory
leaks. Using the `java.lang.ref` package, instead of directly referencing objects, we use special references to objects
that allow them to be easily garbage collected.

Reference queues make us aware of the actions the Garbage Collector performs.

A soft reference object (or a softly reachable object) can be cleared by the Garbage Collector in response to a memory
demand. A softly reachable object has no strong references pointing to it.

When a Garbage Collector gets called, it starts iterating over all elements in the heap. GC stores reference type
objects in a special queue.

After all objects in the heap get checked, GC determines which instances should be removed by removing objects from that
queue mentioned above.

These rules vary from one JVM implementation to another, but the documentation states that all soft references to
softly reachable objects are guaranteed to be cleared before a JVM throws an `OutOfMemoryError`.

---

## Chapter 03. Summary of most important JVM parameters

We'll explore the most well-known options that we can use to configure the Java Virtual Machine.

**_Explicit Heap Memory – `Xms` and `Xmx` Options_**

The most common performance-related practice is to initialize the heap memory as per the application requirements.

That's why we should specify minimal and maximal heap size. We can use the below parameters to achieve this:

```
-Xms<heap size>[unit] 
-Xmx<heap size>[unit]
```

Here, `unit` denotes the unit in which we'll initialize the memory (indicated by heap size). We can mark units as
`g` for `GB`, `m` for `MB`, and `k` for `KB`.

For example, if we want to assign minimum 2 GB and maximum 5 GB to JVM, we need to write:

```
java -Xms2G -Xmx5G Application
```

Starting with **Java 8**, the size of `Metaspace` isn't defined. Once it reaches the global limit, JVM automatically
increases it. However, to overcome any unnecessary instability, we can set `Metaspace` size with:

```
-XX:MaxMetaspaceSize=<metaspace size>[unit]
```

As per the **Oracle guidelines**, after total available memory, the second most influential factor is the proportion of
the heap reserved for the **Young Generation**. By default, the **minimum** size of the YG is `1310 MB`, and **maximum**
size is `unlimited`.

We can assign them explicitly:

```
-XX:NewSize=<young size>[unit] 
-XX:MaxNewSize=<young size>[unit]
```

**Garbage Collection Algorithm**

For better stability of the application, choosing the right Garbage Collection algorithm is critical.

```
-XX:+UseSerialGC
-XX:+UseParallelGC
-XX:+USeParNewGC
-XX:+UseConcMarkSweepGC
-XX:+UseG1GC
-XX:+UseShenandoahGC                                                                                                                                      | JDK 15                                           |
-XX:+UseZGC
```

**Garbage Collection Logging**

To strictly monitor the application health, we should always check the JVM’s Garbage Collection performance. The easiest
way to do this is to log the GC activity in human-readable format.

```
-verbose:gc
-XX:+UseGCLogFileRotation 
-XX:NumberOfGCLogFiles=< number of log files > 
-XX:GCLogFileSize=< file size >[ unit ]
-Xloggc:/path/to/gc.log
-XX:+PrintGCTimeStamps
-XX:+PrintGCDateStamps
```

- `UseGCLogFileRotation` specifies the log file rolling policy, much like `log4j`, `s4lj`, etc.
- `NumberOfGCLogFiles` denotes the max number of log files we can write for a single application life cycle.
- `GCLogFileSize` specifies the max size of the file.
- `loggc` denotes its location.
- `PrintGCTimeStamps` and `PrintGCDateStamps` can be used to print date-wise timestamps in the GC log.

For example, if we want to assign a maximum of `10` GC log files, each having a maximum size of `30 MB`, and we want to
store them in the `/home/application/log/` location, we can use the below syntax:

```
-XX:+UseGCLogFileRotation  
-XX:NumberOfGCLogFiles=10
-XX:GCLogFileSize=30M 
-Xloggc:/home/application/log/gc.log
```

However, the problem is that one additional daemon thread is always used for monitoring system time in the background.
This behavior may create some performance bottleneck, which is why it's better to not play with this parameter in
production.

**Handling OOM**

It’s very common for a large application to face an **out of memory** error, which in turn results in an application
crash. It's a very critical scenario, and tough to replicate to troubleshoot the issue.

That's why JVM comes with some parameters to dump heap memory into a physical file that we can use later to find leaks:

```
-XX:+HeapDumpOnOutOfMemoryError 
-XX:HeapDumpPath=./java_pid<pid>.hprof
-XX:OnOutOfMemoryError="< cmd args >;< cmd args >" 
-XX:+UseGCOverheadLimit
```

- `HeapDumpOnOutOfMemoryError` instructs the JVM to dump heap into a physical file in case of `OutOfMemoryError`.
- `HeapDumpPath` denotes the path where the file will be written. Any filename can be given; however, if JVM finds
  a `<pid>` tag in the name, the process id of the current process causing the out of memory error will be appended to
  the file name with `.hprof` format.
- `OnOutOfMemoryError` is used to issue emergency commands that will be executed in case of an out of memory error. We
  should use proper commands in the space of `cmd` args. For example, if we want to restart the server as soon as an out
  of memory occurs, we can set the parameter: `-XX:OnOutOfMemoryError="shutdown -r"`
- `UseGCOverheadLimit` is a policy that limits the proportion of the VM's time that's spent in GC before an out of
  memory error is thrown.

**Other JVM arguments**

- `-server`: enables **"Server Hotspot VM."** We use this parameter by default in `64-bit JVM`.
- `-XX:+UseStringDeduplication`: Java **8u20** has introduced this JVM parameter for reducing the unnecessary use of
  memory by creating too many instances of the same `String`. This optimizes the heap memory by reducing duplicate
  `String` values to a single global `char[]` array.
- `-XX:+UseLWPSynchronization`: sets a LWP (Light Weight Process) based synchronization policy instead of thread-based
  synchronization.
- `-XX:LargePageSizeInBytes`: sets the large page size used for the Java heap. It takes the argument in `GB/MB/KB`. With
  larger page sizes, we can make better use of virtual memory hardware resources; however, this may cause larger space
  sizes for the `PermGen`, which in turn can force us to reduce the size of the Java heap space.
- `-XX:MaxHeapFreeRatio`: sets the maximum percentage of heap free after GC to avoid shrinking
- `-XX:MinHeapFreeRatio`: sets the minimum percentage of heap free after GC to avoid expansion. To monitor the heap
  usage, we can use `VisualVM` shipped with JDK.
- `-XX:SurvivorRatio`: Ratio of `eden/survivor` space size. For example, `-XX:SurvivorRatio=6` sets the ratio between
  each survivor space and eden space to be `1:6`.
- `-XX:+UseLargePages`: use large page memory if the system supports it. Please note that `OpenJDK 7` tends to crash if
  using this JVM parameter.
- `-XX:+UseStringCache`: enables caching of commonly allocated strings available in the **String pool**
- `-XX:+UseCompressedStrings`: use a `byte[]` type for `String` objects which can be represented in pure ASCII format
- `-XX:+OptimizeStringConcat`: it optimizes `String` concatenation operations where possible
