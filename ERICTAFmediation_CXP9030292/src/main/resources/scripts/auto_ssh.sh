#!/bin/bash
while [ $# -ne 0 ];
do
sshhost=$1
user=$2
password=$3
command=$4
shift 4
/usr/bin/expect <<EOF
spawn -noecho ssh -l $user $sshhost $command
set timeout 60
expect "*assword*"
send "$password\r"
expect eof
EOF
done
