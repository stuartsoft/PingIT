#!/bin/bash

#Mirror the build status to dropbox for external viewing
cd /home/stuart/Dropbox
#give the build server a chance to save the new status image
rm -f pingitstatus.png
sleep 10
curl "http://localhost:8111/app/rest/builds/buildType:(id:PingIT_Build)/statusIcon" >> pingitstatus.png
