#! /bin/sh
cd /home/www/uc/task
nowdate=$(date -d "now" +%Y-%m-%d)
echo "nowdate:"$nowdate
rm -rf uc-0.0.1-SNAPSHOT.jar
wget http://xstock4uc-fjly.oss-us-east-1-internal.aliyuncs.com/uc-0.0.1-SNAPSHOT.jar
pid=$(ps -ef | grep uc-0.0.1-SNAPSHOT.jar | grep -v grep | grep task | awk '{print $2}')
if [ -n "$pid" ];then
        echo "killing pid:$pid"
        kill -9 $pid
fi
nohup java -Xms3072M -Xmx3072M -Xss256k -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:MaxDirectMemorySize=1024m -XX:+UseG1GC -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -XX:+PrintCommandLineFlags -jar -DENABLE_TASK=true ./uc-0.0.1-SNAPSHOT.jar  --spring.profiles.active=task >> /home/work/logs/uc/$nowdate_task.log 2>&1 &