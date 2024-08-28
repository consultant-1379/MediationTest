#!/bin/bash

netsimIpAddress=10.20.64.5

scp templateFile.gz netsim@$netsimIpAddress:/netsim/netsim_dbdir/simdir/netsim/netsimdir/
scp createDeltaFiles.pl netsim@$netsimIpAddress:/netsim/netsim_dbdir/simdir/netsim/netsimdir/

echo "Logging onto netsim at " $netsimIpAddress
ssh netsim@$netsimIpAddress "/usr/bin/perl /netsim/netsim_dbdir/simdir/netsim/netsimdir/createDeltaFiles.pl"

# RUN MEDIATION JCAT TEST SUITE BASED ON THE CURRENT CONTENTS OF 
# /opt/ericsson/nms/litpi/PmMediationTest/target/suites/suite.xml 
./jcatng_start.sh
exit 0 