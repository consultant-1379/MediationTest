#!/bin/sh

targetDir="/opt/ericsson/nms/litp/PmMediationTest/target/"
# Classpath
CP="`find $targetDir/*.jar`"
for i in `find $targetDir/lib/*`; do
	CP=$CP:$i
done

# User props
suite="/$targetDir/suites/suites.xml"

logdir="/var/www/html/jcatKGM"
name="PM_Mediation_Acceptance_Test"

# General props
jarfile=""
props=""
els=""

# Clear out collect_files 
ssh SC-1 "rm -rf /cluster/collect_files/*"


# Clear out html results 
rm -rf $logdir/* $logdir/*.*

# Place property files in Target Directory 
jar -xvf ../jcat-mediation-testcases-1.0.2-SNAPSHOT.jar logdb.properties log4j.properties 

#Database logging
logdb="/$targetDir/logdb.properties"
logwriters="se.ericsson.jcat.fw.ng.logging.writers.DbLogWriterRIPNG"

# test stats workaround
vmargs=" -Dname=$name -Dlegacylogging="" -Dlogdir=$logdir -Dprops=$props -Dels=$els -Dhttp.proxyHost=159.107.173.253 -Dhttp.proxyPort=3128 $@"

java $vmargs -cp $CP org.testng.TestNG $suite
exit 0
