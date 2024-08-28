#!/bin/bash

nodeName="${1:-LTE01}"
erbsName="${2:-LTE01ERBS00002}"
netsimIpAdress=$3

echo "Value for nodeName is $nodeName"
echo "Value for erbsName is $erbsName"


echo "The value of netsimIpAdress in sameNodeDifferentDirectories is: $netsimIpAdress"

simName=$(./getSimulationName.sh $nodeName $netsimIpAdress)

simName=$simName"-"$nodeName
echo "The value of simName in sameNodeDifferentDirectories is: $simName"

restOfDirPath="/$erbsName/fs/ftp/"
startOfDirPath="/netsim/netsim_dbdir/simdir/netsim/netsimdir/"
dirPath=$startOfDirPath$simName$restOfDirPath
echo $dirPath
dirs=( "5MB" "10MB" "100MB" "700MB" )
commands=( "dd if=/dev/zero of=$dirPath/5MB/$erbsName.txt  bs=5M  count=1" "dd if=/dev/zero of=$dirPath/10MB/$erbsName.txt  bs=1M  count=10" "dd if=/dev/zero of=$dirPath/100MB/$erbsName.txt  bs=10M  count=10" "dd if=/dev/zero of=$dirPath/700MB/$erbsName.txt  bs=10M  count=70" )
fileSizes=( "5.0M" "10M" "100M" "700M" )

for(( j=0; j<${#dirs[*]}; j++))
do
	dir=${dirs[$j]}
	command="${commands[$j]}"
	fileSize=${fileSizes[$j]}

	./createDirsOnNetsim.sh $netsimIpAdress $nodeName $erbsName $simName $dirPath $dir "$command" $fileSize
done
