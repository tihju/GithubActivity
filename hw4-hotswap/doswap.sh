#/bin/bash

function killOld {
     echo "killing older version of $1"
     docker rm -f `docker ps -a | grep $1  | sed -e 's: .*$::'`
}


if [ $# -eq 0 ]
  then
    echo "No argument passed"
    exit 1
fi

# get the last num of the input2, which call swap#.sh
imageName=$1

docker ps -a  > /tmp/yy_xx$$
if grep --quiet "web1" /tmp/yy_xx$$
  then
    docker run --name "web2" --network ecs189_default -d -P $imageName
    docker exec ecs189_proxy_1 /bin/bash /bin/"swap2".sh
    killOld "web1"
elif grep --quiet "web2" /tmp/yy_xx$$
  then
    docker run --name  "web1" --network ecs189_default -d -P $imageName
    docker exec ecs189_proxy_1 /bin/bash /bin/"swap1".sh
    killOld "web2"
else
   echo "Neither web1 or web2 is running"
fi

# Initially the reverse proxy points at engineering URL
# WE first make it point at the right url, using the init.sh script

echo "redirecting to the service"
echo "...nginx restarted, should be ready to go!"

#cleanup zombie files if any
zombie=$(docker ps -qa --no-trunc --filter "status=exited")
if [ -n "$zombie" ];then
    docker rm $(docker ps -qa --no-trunc --filter "status=exited")
    docker network rm $(docker network ls | grep "bridge" | awk '/ / { print $1 }')
    echo "finsihed cleaning up"
fi
