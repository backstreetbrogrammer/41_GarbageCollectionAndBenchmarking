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
2. Heap monitoring and analysis
3. Using profiler for performance analysis
4. Performance benchmarking
5. Summary of most important JVM parameters

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
java -XX:+UseSerialGC Application.java
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
java -XX:+UseParallelGC Application.java
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
java -XX:+UseConcMarkSweepGC Application.java
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
java -XX:+UseG1GC Application.java
```

**Pros:**

- It's very efficient with gigantic datasets.
- It takes full advantage of multiprocessor machines.
- It's the most efficient in achieving pause time goals.

**Cons:**

- It's not the best when there are strict throughput goals.
- It requires the application to share resources with GC during concurrent collections.

#### ZGC

