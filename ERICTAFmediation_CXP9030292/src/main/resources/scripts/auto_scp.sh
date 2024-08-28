#!/bin/bash
while [ $# -ne 0 ];
do
sshhost=$1
user=$2
password=$3
source_file=$4
dest_file=$5
shift 5
/usr/bin/expect <<EOF
spawn -noecho scp $user@$sshhost:$source_file $dest_file
set timeout 60 
expect "*assword*"
send "$password\r"
expect eof
EOF
done
