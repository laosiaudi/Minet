#!/usr/bin/env bash
# AUTHOR:   LaoSi
# FILE:     rebuild.sh
# 2013 @laosiaudi All rights reserved
# CREATED:  2014-01-03 17:16:38
# MODIFIED: 2014-01-03 17:35:13
cd src/GuiClient/src/
javac -d ../bin/gui/ -cp .:../bin/gui/ClientProtocol/   gui/*.java

