TinyDB
======

TinyDB is a REDIS implementation in Java. At this moment is in the first stage of development
and only implements a small subset of commands. The objetive is implement a full functional
one-to-one replacement for REDIS.

You probably will wonder why I do this, the answer is Just For Fun.

Implemented commands
--------------------

- SET
- GET
- DEL
- GETSET
- EXISTS
- MGET
- MSET
- INCR
- DECR
- INCRBY
- DECRBY
- HGET
- HSET
- HGETALL
- PING
- ECHO
- TIME
- FLUSHDB
- STRLEN

Design
------

TinyDB is implemented using asynchronous IO with netty, and at this moment 
with no persistence, only works like an on-memory cache.

Concurrency is managed with StampedLocks and optimistic reads.

Performance
-----------

Performance is quite good, not as good as REDIS, but it's good enough for Java.

This is TinyDB

    $ redis-benchmark -t set,get -h localhost -p 7081 -n 100000 -q
    SET: 67704.80 requests per second
    GET: 70821.53 requests per second
    
And this is REDIS

    $ redis-benchmark -t set,get -h localhost -p 6379 -n 100000 -q
    SET: 97751.71 requests per second
    GET: 100000.00 requests per second

