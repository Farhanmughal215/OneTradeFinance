#! /bin/sh
# shellcheck disable=SC2164
mvn -B clean package -DskipTests -P test
mv target/uc-0.0.1-SNAPSHOT.jar ../uc-server
pid=$(ps -ef | grep uc-0.0.1-SNAPSHOT.jar | grep -v grep | awk '{print $2}')
if [ -n "$pid" ];then
        echo "killing pid:$pid"
        kill -9 $pid
fi
nowdate=$(date -d "now" +%Y-%m-%d)
echo "nowdate:"$nowdate
cd /home/ubuntu/uc-server/
nohup java -Xms3072M -Xmx3072M -Xss256k -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:MaxDirectMemorySize=1024m -XX:+UseG1GC -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -XX:+PrintCommandLineFlags -jar ./*-SNAPSHOT.jar --spring.profiles.active=test >> /home/ubuntu/uc-server/logs/$nowdate.log 2>&1 &
