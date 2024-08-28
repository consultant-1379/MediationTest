#!/bin/bash

netsimIpAdress=$1
nodeName=$2
erbsName=$3
simName=$4
dirPath=$5
dir=$6
command=$7
fileSize=$8

#echo "NetsimIpAddress is $netsimIpAdress"
#echo "nodeName is $nodeName"
#echo "erbsName is $erbsName"
#echo "simName is $simName"
#echo "dirPath is $dirPath"
#echo "dir is $dir"
#echo "command is $command"
#echo "fileSize is $"fileSize

isDirAvailable=`./auto_ssh.sh $netsimIpAdress netsim netsim "ls -ltrh $dirPath | grep $dir " | grep -vi "Password:"` 

echo $isDirAvailable

if [ "$isDirAvailable" == "" ]
then
	echo "Directory does not contain $dir directory"
	./auto_ssh.sh $netsimIpAdress netsim netsim "mkdir $dirPath/$dir/"
else
	echo "Directory contains $dir directory"
fi

#echo "./auto_ssh.sh $netsimIpAdress netsim netsim ls -ltrh $dirPath/$dir/ | grep $fileSize  | grep -vi Password:"
isFileAvailable=`./auto_ssh.sh $netsimIpAdress netsim netsim "ls -ltrh $dirPath/$dir/ | grep $fileSize " | grep -vi "Password:"`
echo $isFileAvailable


if [ "$isFileAvailable" == "" ]
then
       	echo "Directory does not contain $dir file"
	echo "./auto_ssh.sh $netsimIpAdress netsim netsim \"$command\""
	./auto_ssh.sh $netsimIpAdress netsim netsim "$command"
else
       	echo "Directory contains $dir file"
fi

