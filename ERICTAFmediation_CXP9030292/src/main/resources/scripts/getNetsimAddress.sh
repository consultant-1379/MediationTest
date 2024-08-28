#!/bin/bash

netsimIpAdress=`cat ../taf_properties/mediationHost.properties | grep host.Netsim.ip | tr "=" " " | awk '{print $2}'`
echo $netsimIpAdress
