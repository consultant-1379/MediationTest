#!/bin/bash

nodeName="${1:-LTE01}"
netsimIpAdress=$2

simName=`./auto_ssh.sh $netsimIpAdress netsim netsim "ls -ltrh /netsim/netsim_dbdir/simdir/netsim/netsimdir/|grep $nodeName"|grep -vi "Password:"|awk '{print $8}'|tr "-" " " | awk '{print $1}'`
simName="$simName-"`./auto_ssh.sh $netsimIpAdress netsim netsim "ls -ltrh /netsim/netsim_dbdir/simdir/netsim/netsimdir/|grep $nodeName"|grep -vi "Password:"|awk '{print $8}'|tr "-" " " | awk '{print $2}'`
simName="$simName-"`./auto_ssh.sh $netsimIpAdress netsim netsim "ls -ltrh /netsim/netsim_dbdir/simdir/netsim/netsimdir/|grep $nodeName"|grep -vi "Password:"|awk '{print $8}'|tr "-" " " | awk '{print $3}'`
simName="$simName-"`./auto_ssh.sh $netsimIpAdress netsim netsim "ls -ltrh /netsim/netsim_dbdir/simdir/netsim/netsimdir/|grep $nodeName"|grep -vi "Password:"|awk '{print $8}'|tr "-" " " | awk '{print $4}'`
simName="$simName"
echo "$simName"

