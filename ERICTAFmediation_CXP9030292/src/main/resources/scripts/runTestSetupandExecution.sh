#!/bin/bash
# THIS IS MASTER TESTSCRIPT COMBINING VARIOUS INDIVIDUAL TEST SCRIPTS 
# USED BY MEDIATION TEST TEAM 
# IF RUN IT WILL DO THE FOLLOWING TASKS   
# IT SHOULD BE LOCATED IN THE FOLLOWING DIRECTORY IN CASE OF ISSUES WITH 
# RELATIVE PATH NAMES 
# /opt/ericsson/nms/litpi/PmMediationTest/target/scripts 

PMMed_0_ipaddress=$1
PMMed_1_ipaddress=$2
PMServ_0_ipaddress=$3
PMServ_1_ipaddress=$4
FMServ_0_ipaddress=$5
FMServ_1_ipaddress=$6

# 
# 1) create_shared_space.sh
# CHECK YOUR SHARED STORAGE AREAS, IF SHARED STORAGE AREA /collect_files does not exist 
# IT WILL BE CREATED ON THE NFS AND USING LITP INVENTORY ROLLED OUT TO THE 
# NODES IN YOUR SYSTEM
shared_dir="${1:-/cluster/collect_files/}"
ssh SC-1 "mkdir -p /cluster/collect_files/"
ssh SC-1 "chown litp_jboss:litp_jboss /cluster/collect_files/"
#
# 2) getLatestSoftware.sh
# GET LATEST SOFTWARE FROM NEXUS , CURRENTLY THIS JOBS PULLS THE LATEST CODE FOR THE 
# FOLLOWING PRODUCTS 
# MEDIATIONSERVICE , MEDIATIONCORE, PMSERVICE, TOPOLOGYSYNC, DATAPERSISTENCE, UPGRADEMANAGER 
# ALL EAR FILES ARE FETCHED AND STORED IN THE FOLLOWING DIRECTORY 
# /opt/ericsson/nms/litp/mediationSW 
./getRpmBaselineSoftware.sh
#
# 3) refreshTestEnvironment.sh ( very similar to  end_to_end_setupTestEnv.sh )
# MAIN SETUP SCRIPT 
# a) locates ip addresses of all nodes in your Tor system  
# b) undeploys all software on both SC1 and SC2 blades based on defined  
#    list of software i ( in script currently ) 
# c) removes tmp and data directories from jboss configuration  
# d) stop running jboss instance or kill the process if required 
# e) start Jboss Instance ( SC1 and then sc2 )  
# f) deploy all required software based on defined list of software i ( in script currently )
./deploy_rpms.sh
./deploy_ears.sh $PMMed_0_ipaddress $PMMed_1_ipaddress $PMServ_0_ipaddress $PMServ_1_ipaddress $FMServ_0_ipaddress $FMServ_1_ipaddress
#
#
# 4) jcatng_start.sh 
# RUN MEDIATION JCAT TEST SUITE BASED ON THE CURRENT CONTENTS OF
# /opt/ericsson/nms/litpi/PmMediationTest/target/suites/suite.xml 
#./jcatng_start.sh
exit 0 
#
# You can browse the results of this test case 
# by opening web browser wit hhttp address 
# http://<MS1 IP ADDRESS>/jcatKGM 
