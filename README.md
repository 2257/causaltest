CausalTest is a  test framework for automatic testing causality semantics of data replication in distributed databases, 
read/write services, etc.

# Introduction about class

In `causaltest/core/src/main/java/com/yahoo/ycsb/workloads/causalsemantic` there are four interfaces about causal semantic guarantee.

In `causaltest/core/src/main/java/com/yahoo/ycsb/workloads/testcase` there are twelve test cases abstract class extracted from online web services such as Twitter, Flickr, Amazon.
These class set the client behavior's operations order and simulation data in these services.
you can implements the semantic interface and writes new abstract class.

In `causaltest/core/src/main/java/com/yahoo/ycsb/workloads/dbinstance` there are some database instances extend the test cases, 
these instances implement the specific sentences that sent from client, one can implements new instance about
new storage or online service API. Each package contains a kind of distributed storage service.

`causaltest/core/src/main/java/com/yahoo/ycsb/workloads/ViolationChecker` is a class to check violations, if you implement new test 
instance, you should implements the new violation checker in this class, you can just the causal semantic 
violation by the result responded from  web server.


# How to use

1. Start up the database/web service which to be tested.

2. Run `./bin/deploy.sh` to deploy the  web server.
If you have changed some source code, remember run `./bin/redeploy.sh` to redeploy.

3. Set the config file `causaltest/causalwebserver/src/main/resources/conf.properties`, you must set `Database` (Cassandra, Hbase ...)
, `Website` (Twitter, Amazon ...) and `Consistency` (RYW, MR, MW, WFR), and if you need, you should set IP address, port et al.


4. Run `./runtest.sh` to start up a client test. The result are filed in `result.txt`.

5. If you would like to add new test instance, please create a new package, and then
add new `dbinstance`, new `ViolationChecker`, new database(service API)
operation driver in `causaltest/causalwebserver/src/main/java/com/operation` and new web server in 
`causaltest/causalwebserver/src/main/java/com/server`.

6. In directory `bin`, there are some scripts, you can execute them  to control the system kill a process to simulate node crash.
For example, you can use `cassandracoor` to kill a cassandra coordinator process, you should configure the path as yourself, and if
you need to recover the node or not, you can adjust the restart command.