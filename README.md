# TinyDB

TinyDB is a REDIS implementation in Java. At the moment is in development and only implements a small 
subset of commands and features.  The objetive is implement a full functional one-to-one replacement 
for REDIS (2.8 branch).

You will probably wonder why I do this, the answer is I do it Just For Fun.

## Implemented commands

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
    - EXPIRE
    - PERSIST
    - TTL
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
    - SETEX
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
- Transactions
    - MULTI
    - EXEC

## Design

TinyDB is implemented using Java8. Is single thread, like REDIS. It uses asynchronous IO 
(netty) and reactive programing paradigm (rxjava).

Requests come from IO threads and enqueues to rxjava single thread scheduler. Then IO thread is free
to process another request. When request is done, the response is sended to client asyncronously. Then,
every request is managed one by one, in a single thread, so there's no concurrency issues to care
about.

## Features

Now only implements a subset of REDIS commands, but is usable.

TinyDB also supports persistence compatible with REDIS, RDB dumps and AOF journal. It can create
compatible RDB files you can load in a REDIS server.

Now TinyDB support master/slave replication, a master can have multiple slaves, but at the moment
slaves can have slaves.

Also implements partially the Pub/Sub subsystem.


## Performance

Performance is quite good, not as good as REDIS, but it's good enough for Java.

This is TinyDB

    $ redis-benchmark -t set,get -h localhost -p 7081 -n 100000 -q
    SET: 47664.44 requests per second
    GET: 50226.02 requests per second
    
And this is REDIS

    $ redis-benchmark -t set,get -h localhost -p 6379 -n 100000 -q
    SET: 97656.24 requests per second
    GET: 98716.68 requests per second
    
In my laptop (intel core i5, with 4G of RAM)

## BUILD

You need to clon the repo:

    $ git clone https://github.com/tonivade/tiny-db.git

TinyDB uses Gradle as building tool, but you don't need Gradle installed, just type:

    $ ./gradlew build

This scripts automatically download Gradle and then runs the tasks. 

Or if you have Gradle installed, just type

    $ gradle build

## DOWNLOADS

- Without deps [tiny-db.jar](https://drone.io/github.com/tonivade/tiny-db/files/build/libs/tiny-db-0.6.0-SNAPSHOT.jar)
- With all deps included [tiny-db-all.jar](https://drone.io/github.com/tonivade/tiny-db/files/build/libs/tiny-db-all-0.6.0-SNAPSHOT.jar)

## USAGE

You can start a new server listening in default port 7081.

    $ java -jar tiny-db-all.jar
    
Parameters:

    Option        Description
    ------        -----------
    --help        print help
    -P            enable with persistence
    -h            host (default: localhost)
    -p <Integer>  port (default: 7081) 

## TODO

- Namespace notifications.
- Ziplist and Maplist encoding not implemented yet.
- PSUBSCRIBE and PUNSUBSCRIBE commands not implemented yet.
- Master/Slave replication improvements. Slave with Slaves
- Scripting with Javascript/Lua.
- Partitioning?
- Clustering?

## Continuous Integration

[![Build Status](https://drone.io/github.com/tonivade/tiny-db/status.png)](https://drone.io/github.com/tonivade/tiny-db/latest)

[![Coverity Scan Build Status](https://scan.coverity.com/projects/5353/badge.svg)](https://scan.coverity.com/projects/5353)

[![Coverage Status](https://coveralls.io/repos/tonivade/tiny-db/badge.svg?branch=master)](https://coveralls.io/r/tonivade/tiny-db?branch=develop)

## LICENSE

TinyDB is released under MIT License
