#!/bin/bash
  
user="root"
pw="shroot"
numOfNodes=2
sizeLimitDir=/var/virtual_disks/directory_with_size_limit
sizeLimitFile=/var/virtual_disks/directory_with_size_limit.ext3
fsSize=100000

full_dir="${1:-/fullFileSystem/}"
echo "Directory to be used: $full_dir"

cat /etc/hosts | grep SC-2 | awk '{print $1}' > myfile3.txt
cat /etc/hosts | grep SC-1 | awk '{print $1}' >> myfile3.txt
cat /etc/hosts | grep PL-3 | awk '{print $1}' >> myfile3.txt
cat /etc/hosts | grep PL-4 | awk '{print $1}' >> myfile3.txt

while read line
do
        nodes[$count]=$line
        count=`expr $count + 1`
done < myfile3.txt


rm -rf myfile3.txt

for(( i=0; i<2; i++))
do
	ipaddress=${nodes[$i]}
	if [[ `ssh $user@$ipaddress "test -d $full_dir && echo true"` == "true"  ]]; then
		echo "$full_dir already exists, no action will be taken"
	else
		echo "$full_dir does not exist, directory will be created."
		ssh $user@$ipaddress "mkdir -p $sizeLimitDir"
		ssh $user@$ipaddress "mkdir -p $full_dir"
		ssh $user@$ipaddress "touch $sizeLimitFile"
		ssh $user@$ipaddress "dd if=/dev/zero of=$sizeLimitFile bs=$fsSize count=1"
		./auto_proceed.sh $ipaddress root "mkfs.ext3 $sizeLimitFile"
		ssh $user@$ipaddress "mount -o loop,rw,usrquota,grpquota $sizeLimitFile $full_dir"
		scp ./* $user@$ipaddress:$full_dir
	fi
done
exit 0
