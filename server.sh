#!/usr/bin/env bash
# AUTHOR:   LaoSi
# FILE:     server.sh
# 2013 @laosiaudi All rights reserved
# CREATED:  2014-01-03 17:02:05
# MODIFIED: 2014-01-03 17:06:19
cd src/Server
javac -d ../../bin Server.java
cd ../../bin
java Server
