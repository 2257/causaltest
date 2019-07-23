#!/bin/bash

sed -i '/unpackWARs/a\<Context path=\"\" docBase=\"/home/zhaole/tomcat/webapps/causalwebserver\"\/>' $CATALINA_HOME/conf/server.xml
