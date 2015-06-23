TinyDB
======

TinyDB is a REDIS implementation in Java. At this moment is in the first stage of development
and only implements a small subset of commands. The objetive is implement a full functional
one-to-one replacement for REDIS.

You probably will wonder why I do this, the answer is Just For Fun.

Implemented commands
--------------------

- Server
    - FLUSHDB
    - INFO
    - TIME
    - SYNC
    - SLAVEOF
- Connection
    - ECHO
    - PING
    - QUIT
    - SELECT
- Key
    - DEL
    - EXISTS
    - KEYS
    - RENAME
    - TYPE
- String
    - APPEND
    - DECRBY
    - DECR
    - GET
    - GETSET
    - INCRBY
    - INCR
    - MGET
    - MGET
    - SET
    - STRLEN
- Hash
    - HDEL
    - HEXISTS
    - HGETALL
    - HGET
    - HKEYS
    - HLEN
    - HSET
    - HVALS
- List
    - LPOP
    - LPUSH
    - LINDEX
    - LLEN
    - LRANGE
    - LSET
    - RPOP
    - RPUSH
- Set
    - SADD
    - SCARD
    - SDIFF
    - SINTER
    - SISMEMBER
    - SMEMBERS
    - SPOP
    - SRANDMEMBER
    - SREM
    - SUNION
- Sorted Set
    - ZADD
    - ZCARD
    - ZRANGEBYSCORE
    - ZRANGE
    - ZREM
    - ZREVRANGE
- Pub/Sub
    - SUBSCRIBE
    - UNSUBSCRIBE
    - PUBLISH

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

TODO
----

- ~~Pipelining.~~ **Done!**
- Key expiration.
- Transactions (MULTI & EXEC).
- Persistence to disk. _(Working on it!)_
    - 80% RDB file format implemented (strings, lists, sets, sorted sets and hashes).
    - Ziplist and Maplist encoding not implemented yet.
- Publish/Subscribe. _(Partially implemented)_
    - PSUBSCRIBE and PUNSUBSCRIBE commands not implemented yet.
- Master - Slave Replication. _(Working on it!)_
    - Initial implementation finished, with only one slave per master
- Scripting with Javascript/Lua.
- Partitioning?
- Clustering?

CI
--

[![Build Status](https://drone.io/github.com/tonivade/tiny-db/status.png)](https://drone.io/github.com/tonivade/tiny-db/latest)

[![Coverity Scan Build Status](https://scan.coverity.com/projects/5353/badge.svg)](https://scan.coverity.com/projects/5353)

[![Coverage Status](https://coveralls.io/repos/tonivade/tiny-db/badge.svg?branch=master)](https://coveralls.io/r/tonivade/tiny-db?branch=master)

LICENSE
-------

TinyDB is released under MIT License
