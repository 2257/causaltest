#!/bin/bash
./bin/ycsb run rest -threads 1 -P causalwebserver/src/main/resources/conf.properties -s > result.txt
