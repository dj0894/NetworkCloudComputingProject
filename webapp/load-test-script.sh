#!/bin/bash


call_api () {
  for i in {1..150}
  do
    curl -w "\n" --user "deepika@gmail.com:Deepika@123" http://prod.deepikajha.me:80/v1/user/self &
  done
}


for i in {1..40}
do  
  call_api
  sleep 20 
done
