By:
- Ali Raza (razaal14)
- Jefferson Zhong (zhongjef)
- Shahmeer Shahid (shahid89)

## 0.1. Table of Contents
- [1. Overview](#1-overview)
- [2. Design philosophy](#2-design-philosophy)
- [3. Architecture](#3-architecture)
  - [3.1. Application](#31-application)
    - [Master Node](#master-node)
    - [Worker Node](#worker-node)
  - [3.2. Data](#32-data)
    - [Schema](#schema)
    - [Hash function](#hash-function)
- [4. Scalability](#4-scalability)
  - [4.1. Vertical scaling](#41-vertical-scaling)
  - [4.2. Horizontal scaling](#42-horizontal-scaling)
- [5. Consistency](#5-consistency)
- [6. Availability](#6-availability)
- [7. Orchestration](#7-orchestration)
- [8. Disaster recovery](#8-disaster-recovery)
- [9. Evaluation](#9-evaluation)
  - [9.1. Strengths](#91-strengths)
  - [9.2. Weaknesses](#92-weaknesses)
  - [9.3. Other considerations](#93-other-considerations)


# 1. Overview

In creating this project, we aimed to create a system which was highly specialized for the specific task of shortening URL in a distributed manner.

# 2. Design philosophy
Our primary inspriation for the design of this system came from **Kubernetes**, which a system for automating the deployment and scaling of Docker containers on distributed systems.

The typical architecture implemented when using Kubernetes is shown below:
![kubernetes-architecture](images/kubernetes-architecture.png)
Source: https://www.researchgate.net/figure/Kubernetes-architecture_fig1_320248964

As shown, in Kuberenetes typically there is one Master node which manages and scales a set of Worker nodes. **Each worker node is stateless and runs the exact same tasks**. This greatly simplifies scaling the system, and the deployment of the system on a variety of nodes. Therefore, we decided that Kubernetes would be a good starting point for our system.

However, although we liked the Kubernetes architecture, we recognized some key changes that we would have to make to accomplish our goal. 

For example, Kubernetes systems often rely on a seperate database layer, which would be a database that is deployed externally from the cluster. However, as we wanted to make maximum use of the resources given to us, we decided that **we would not reserve any nodes for a distributed database** (ie Cassandra).

Furthermore, we also decided that the **Worker nodes should not have to know about each other**. This is because such an architecture would not scale well. For example, if we had a scenario where we had 10 nodes and each node would need to be able to interact with every other node, then this would create $10*10 = 100$ possible connections. If we had 20 nodes, then this would create $20*20 = 400$ possible connections between nodes. Therefore, the complexity of such a system would be $O(n^2)$, which is not desirable.

The above two conclusions (no nodes reserved for database and worker nodes should not interact with each other) combined meant that we would have tight coupling between the worker tasks and the assosciated data. Therefore, we decied that **we would not have a seperate database layer** (ie another distributed system that acts as a database), and instead we would assign data to each node according to a predefined schema.

Finally, we decied that **no worker node should be a single point of failuer**. Note: the master node is and will always be a single point of failure for any system that does not have a dynamic routing/dynamic DNS system, as is the case with us.

TODO, reads 100x likely than writes

Therefore, our key design philosophies were:

1. All worker nodes should run the same tasks.
2. No resources will be reserved for a database.
3. No seperate database layer.
4. Workers should not need to know about each other.
5. No worker node should be a single point of failure.


# 3. Architecture

## 3.1. Application

Our architectural diagram is shown below. We split the general set of tasks into two "nodes", the master node and worker nodes:

### Master Node
The master node is responsible for proxying, caching, and load balancing requests across all worker nodes.

### Worker Node
The worker node runs a Java URL Shortner process that reads and writes from a local (`/virtual/`) SQLite database.

![architecture](images/architecture.png)

When the proxy on the Master node receives a request, the request is first checked against the cache. If there is a cache miss, then we must proxy the incoming request to one of the worker nodes. To do this, we first select a "shard" that the incoming request belongs to (explained further in the next section) by hashing the short. 

Then, if the request is a read/`GET` request, the load balancer selects one of the nodes in the shard to send the request to (using round robin). If the request is a write/`PUT` request, then the load balancer forwards the request to all nodes in the shard.

## 3.2. Data 

### Schema

The data is sharded and replicated across all worker nodes in the system. Our goal was to have a system where we maximize the scalability of the system (ie low replication factor), without having a single point of failure (ie replication factor `>=` 2). Naturally, we selected a replication factor of 2.

This means that if we have $n$ worker nodes, then the number of shards $S$ will be $n // 2$, and each shard will have minimum 2 and maximum 3 nodes. This means that every entry of data will be replicated across 2 or 3 nodes.

### Hash function

To map each request to a shard, we use a **dynamic hash function**:

```python
def hash_to_shard(short: int, num_shards: int):
    sum = 0
    for character in short:
        sum += ord(character) # sum up the ASCII value of each character in the short
    return sum % num_shards

```

This means that as we scale horizontally and add worker nodes, the hash/mapping of each data entry will change. This means that we will need to periodically redistribute data, which will be discussed in [Horizontal Scaling](#42-horizontal-scaling).



# 4. Scalability
## 4.1. Vertical scaling
Our system scales vertically quite easily. The primary way in which this is done is through the use of multithreading. In both our Proxy/Load Balancer and URL Shortner, we spin up a new thread for every incoming connection. Although conventionally we would limit the number of threads to the number of threads supported by the CPU, in our testing we found that we increase perforamce by starting a new thread for every incoming connection. This is because the 


## 4.2. Horizontal scaling
# 5. Consistency
# 6. Availability
# 7. Orchestration
# 8. Disaster recovery



# 9. Evaluation
## 9.1. Strengths
## 9.2. Weaknesses
## 9.3. Other considerations

