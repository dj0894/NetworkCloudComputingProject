#!/bin/bash

mkdir -p /home/ubuntu/logs
touch /home/ubuntu/logs/logs.out
sudo chown ubuntu:ubuntu /home/ubuntu/logs/logs.out

sleep 30
source /etc/profile.d/envvars.sh
nohup java -jar /home/ubuntu/web-app-0.0.1-SNAPSHOT.jar 1> /home/ubuntu/logs/logs.out 2>&1 </dev/null &
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a stop
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:/home/ubuntu/cloudwatch_config.json
