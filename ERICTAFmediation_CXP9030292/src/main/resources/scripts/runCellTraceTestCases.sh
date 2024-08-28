#!/bin/bash

netsimIpAddress=10.20.64.5
scp createUEandCellTraceFiles.pl netsim@$netsimIpAddress:/netsim/netsim_dbdir/simdir/netsim/netsimdir/
scp ueAndCellTrace_pmData.tar netsim@$netsimIpAddress:/netsim/netsim_dbdir/simdir/netsim/netsimdir/

echo "Logging onto netsim at " $netsimIpAddress
ssh netsim@$netsimIpAddress "/usr/bin/perl /netsim/netsim_dbdir/simdir/netsim/netsimdir/createUEandCellTraceFiles.pl"

# RUN MEDIATION JCAT TEST SUITE BASED ON THE CURRENT CONTENTS OF 
# /opt/ericsson/nms/litpi/PmMediationTest/target/suites/suite.xml 
./jcatng_start.sh
exit 0 