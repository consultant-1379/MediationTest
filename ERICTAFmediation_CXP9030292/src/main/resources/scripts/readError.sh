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
dirs=( "file_no_perms" "empty_file" "dir_no_perms" "empty_dir" )
commands=( "dd if=/dev/zero of=$dirPath/${dirs[0]}/$erbsName.txt  bs=1M  count=1" "chmod 000 $dirPath/${dirs[0]}/$erbsName.txt" "touch $dirPath/${dirs[1]}/$erbsName.txt" "dd if=/dev/zero of=$dirPath/${dirs[2]}/$erbsName.txt  bs=1M  count=1" "chmod 000 $dirPath/${dirs[2]}" )
fileSizes=( "1M" "0" "1M" "" )


#for(( j=0; j<${#dirs[*]}; j++))
#do
#        dir=${dirs[$j]}
#        command="${commands[$j]}"
#        fileSize=${fileSizes[$j]}

#        ./createDirsOnNetsim.sh $netsimIpAdress $nodeName $erbsName $simName $dirPath $dir "$command" $fileSize
#done


for(( j=0; j<${#dirs[*]}; j++))
do
	dir=${dirs[$j]}
	fileSize=${fileSizes[$j]}

	isDirAvailable=`./auto_ssh.sh $netsimIpAdress netsim netsim "ls -ltrh $dirPath | grep $dir " | grep -vi "Password:"`

	echo $isDirAvailable

	if [ "$isDirAvailable" == "" ]
	then
        	echo "Directory does not contain $dir directory"
        	./auto_ssh.sh $netsimIpAdress netsim netsim "mkdir $dirPath/$dir/"
	else
        	echo "Directory contains $dir directory"
	fi
done

for(( j=0; j<${#commands[*]}; j++))
do
	command=${commands[$j]}
	echo "./auto_ssh.sh $netsimIpAdress netsim netsim \"$command\""
	./auto_ssh.sh $netsimIpAdress netsim netsim "$command"	
done
