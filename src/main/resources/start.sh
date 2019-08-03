#!/bin/bash

. /etc/profile
. /etc/init.d/functions
. $HOME/.bash_profile
echo `date`
echo $HOME
echo $JAVA_HOME
java -version
java -Dlog.dir=/home/app/cbg/ -jar /home/app/cbg/pachong.jar >> /home/app/cbg/logs/nohup.log 2>&1