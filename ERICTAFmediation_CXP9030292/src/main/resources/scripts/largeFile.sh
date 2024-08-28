#!/bin/bash

nodeName="${1:-LTE01}"
erbsName="${2:-LTE01ERBS00002}"
netsimIpAdress=$3
echo "The value of netsimIpAdress in sameNodeDifferentDirectories is: $netsimIpAdress"

simName=$(./getSimulationName.sh $nodeName $netsimIpAdress)

simName=$simName"-"$nodeName
echo "The value of simName in sameNodeDifferentDirectories is: $simName"

restOfDirPath="/$erbsName/fs/ftp/"
startOfDirPath="/netsim/netsim_dbdir/simdir/netsim/netsimdir/"
dirPath=$startOfDirPath$simName$restOfDirPath
#echo $dirPath

dir="700MB"
command="dd if=/dev/zero of=$dirPath/700MB/$erbsName.txt  bs=10M  count=70"
fileSize="700M"

./createDirsOnNetsim.sh $netsimIpAdress $nodeName $erbsName $simName $dirPath $dir "$command" $fileSize
