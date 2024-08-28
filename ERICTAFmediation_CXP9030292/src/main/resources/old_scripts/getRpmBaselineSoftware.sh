#!/bin/bash

mavenListingFile="/maven-metadata.xml"
mediationSW="/opt/ericsson/nms/litp/mediationSW"
nexus_prefix="https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content"
nexus_model=$nexus_prefix"/groups/public/com/ericsson/oss/itpf/datalayer/"
nexus_sfwk=$nexus_prefix"/groups/public/com/ericsson/oss/itpf/common"
nexus_top=$nexus_prefix"/groups/public/com/ericsson/oss/services/"
nexus_groupPublic=$nexus_prefix"/groups/public/com/ericsson/nms/"

JumpOSSRC=10.45.206.83
OSSRCUser="eferbar"
OSSRCpw="eferbar01"
# Http Address of OSSRC used to access Nexus through back door
#ssh_command="ssh -q -l $OSSRCUser $JumpOSSRC"
ssh_command="./auto_ssh.sh $JumpOSSRC $OSSRCUser $OSSRCpw"
wget_command="/usr/sfw/bin/wget"
# Http Addres of nexus repository server and default location of mediation repositories
nexus_release="https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/repositories/releases/com/ericsson/nms"
nexus_snapshot="https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/repositories/snapshots/com/ericsson/nms"

nexus_sfwk="https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/repositories/releases/com/ericsson/oss/itpf/common"

#$ssh_command "rm -rf *.xml"
echo "**********************************************************************"
echo "I M P O R T A N T    N O T E    F R O M     N E N A D    T O    Y O U "
echo "**********************************************************************"
echo "This script fetches all information from Nexus which can be slow "
echo "No human interaction is required, no user or password information"
echo "needs to be provided , please be patient " 

. baseline.txt

echo "Releases "
echo "---------"
echo "PMService        " $PMServiceRelease
echo "Mediation Service" $MediationServiceRelease
echo "Mediation Core   " $MediationCoreRelease
echo "Upgrade Manager  " $UpgradeManagerRelease
echo "Data Persistence " $DataPeristenceRelease
echo "Topology Sync    " $TopologySyncRelease
echo "Tss Service      " $TssRelease
echo "Model Service    " $ModelServiceRelease
echo "Camel            " $CamelRelease
echo "Datapath         " $DatapathRelease
echo "PIB	       " $PlatformIntegrationBridgeRelease
echo "Jcat Accessor Utility 1.0.3"

# Software Paths
PMService="/services/ERICpmservice_CXP9030101/$PMServiceRelease/ERICpmservice_CXP9030101-$PMServiceRelease.rpm"
MediationService="/mediation/ERICpmmedcom_CXP9030103/$MediationServiceRelease/ERICpmmedcom_CXP9030103-$MediationServiceRelease.rpm"
MediationCore="/mediation/ERICpmmedcore_CXP9030102/$MediationCoreRelease/ERICpmmedcore_CXP9030102-$MediationCoreRelease.rpm"
UpgradeManager="/UpgradeManager-ear/$UpgradeManagerRelease/UpgradeManager-ear-$UpgradeManagerRelease.ear"
DataPersistence="/ERICdatapersist_CXP9030106/$DataPeristenceRelease/ERICdatapersist_CXP9030106-$DataPeristenceRelease.rpm"
TopologySync="/ERICtopsync_CXP9030160/$TopologySyncRelease/ERICtopsync_CXP9030160-$TopologySyncRelease.rpm"
TssService="/mediation/ERICtss-service_CXP9023169/$TssRelease/ERICtss-service_CXP9023169-$TssRelease.rpm"
ModelService="/model-service-ear/$ModelServiceRelease/model-service-ear-$ModelServiceRelease.ear"
Camel="/mediation/ERICcamel_CXP9030311/$CamelRelease/ERICcamel_CXP9030311-$CamelRelease.rpm"
Datapath="/datapath/ERICdatapath_CXP9030305/$DatapathRelease/ERICdatapath_CXP9030305-$DatapathRelease.rpm"
PlatformIntegrationBridge=/ERICpib_CXP9030194/$PlatformIntegrationBridgeRelease/ERICpib_CXP9030194-$PlatformIntegrationBridgeRelease.rpm

echo $nexus_release$PMService
echo $nexus_release$MediationService
echo $nexus_release$MediationCore
echo $nexus_model$DataPersistence
echo $nexus_top$TopologySync
echo $nexus_release$TssService
echo $nexus_model$ModelService
echo $nexus_release$Camel
echo $nexus_top$Datapath
echo $nexus_sfwk$PlatformIntegrationBridge

rm -rf $mediationSW
mkdir -p $mediationSW 
$ssh_command "rm -rf *.ear* *.war *.rpm"
echo ""
echo "Retreiving RPMs/EARs/WARs from Nexus" 
echo "------------------------------"
echo "$PMService"
$ssh_command "$wget_command -q $nexus_release$PMService"
echo "$MediationService"
$ssh_command "$wget_command -q $nexus_release$MediationService"
echo "$MediationCore"
$ssh_command "$wget_command -q $nexus_release$MediationCore"
echo "$DataPersistence"
$ssh_command "$wget_command -q $nexus_model$DataPersistence"
echo "$TopologySync"
$ssh_command "$wget_command -q $nexus_top$TopologySync"
echo "$UpgradeManager" 
$ssh_command "$wget_command -q $nexus_sfwk$UpgradeManager"
echo "$TssService"  
$ssh_command "$wget_command -q $nexus_release$TssService"
echo "Jcat-Accessor"
$ssh_command "$wget_command -q $nexus_snapshot$jcatAccessor"
echo "$ModelService"
$ssh_command "$wget_command -q $nexus_model$ModelService"
echo "$Camel"
$ssh_command "$wget_command -q $nexus_release$Camel"
echo "$Datapath"
$ssh_command "$wget_command -q $nexus_top$Datapath"
echo "$nexus_sfwk$PlatformIntegrationBridge"
$ssh_command "$wget_command -q $nexus_sfwk$PlatformIntegrationBridge"



# DEBUG LINE 
#$ssh_command "ls -l | egrep 'ear|war|jar'"   

./auto_scp.sh $JumpOSSRC $OSSRCUser $OSSRCpw "/home/$OSSRCUser/*.ear" $mediationSW
./auto_scp.sh $JumpOSSRC $OSSRCUser $OSSRCpw "/home/$OSSRCUser/*.war" $mediationSW
./auto_scp.sh $JumpOSSRC $OSSRCUser $OSSRCpw "/home/$OSSRCUser/*.rpm" $mediationSW
