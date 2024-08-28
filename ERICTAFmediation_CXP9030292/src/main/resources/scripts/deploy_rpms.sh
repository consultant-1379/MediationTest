#!/bin/bash

sdkDirectory="/opt/ericsson/sdk"
litpBase="/opt/ericsson/nms/litp"
softwareLocation="/var/tmp/mediation_software_location/"
rpmLocation="/var/tmp/mediation_rpm_location/"
earLocation="/var/tmp/mediation_ear_location/"
jbossBase="/opt/jboss-eap"
softwareCopyLocation="/opt/ericsson/nms/litp/mediationSW/"
deployLocation="/opt/jboss-eap/standalone/deployments"
jbossConfiguration="runJbossMediationSprint4.sh"
ssh_user="litp_jboss"
ssh_pw="jboss-eap"
jboss_user="admin"
jboss_pw="12shroot"
numOfNodesToKill=4
numOfNodesToStart=4
numOfMedNodes=2
numOfPmNodes=2
delay_between_deployments=15

######Params for running on SC-2#######################
jboss_ip_addresses=( $PMMed_0_ipaddress $PMServ_0_ipaddress $PMMed_1_ipaddress $PMServ_1_ipaddress $FMServ_0_ipaddress $FMServ_1_ipaddress )
dir_names=( PMMed_si_0_jee_instance PMServ_si_0_jee_instance PMMed_si_1_jee_instance PMServ_si_1_jee_instance FMServ_si_0_jee_instance FMServ_si_1_jee_instance )
jboss_names=( safSu=PMMed_App-SuType-0,safSg=PMMed,safApp=PMMed_App safSu=PMServ_App-SuType-0,safSg=PMServ,safApp=PMServ_App safSu=PMMed_App-SuType-1,safSg=PMMed,safApp=PMMed_App safSu=PMServ_App-SuType-1,safSg=PMServ,safApp=PMServ_App safSu=FMServ_App-SuType-0,safSg=FMServ,safApp=FMServ_App safSu=FMServ_App-SuType-1,safSg=FMServ,safApp=FMServ_App )
pm_jboss_names=( safSu=PMServ_App-SuType-0,safSg=PMServ,safApp=PMServ_App safSu=PMServ_App-SuType-1,safSg=PMServ,safApp=PMServ_App )
med_jboss_names=( safSu=PMMed_App-SuType-0,safSg=PMMed,safApp=PMMed_App safSu=PMMed_App-SuType-1,safSg=PMMed,safApp=PMMed_App )
pm_ip_addresses=( $PMServ_0_ipaddress $PMServ_1_ipaddress )
med_ip_addresses=( $PMMed_0_ipaddress $PMMed_1_ipaddress )

ip_addresses=( "SC-1" "SC-1" "SC-2" "SC-2" "SC-1" "SC-2" )
rpm_ipaddresses=( "SC-1" "SC-2" )
softwareLocations=( $softwareLocation $rpmLocation $earLocation )

shared_dir="${1:-/cluster/collect_files/}"

for(( j=0; j<${#softwareLocations[*]}; j++))
do
  currentLocation=${softwareLocations[$j]}
  if [[ `test -d $currentLocation && echo true` != "true" ]]; then
        echo "$currentLocation does not exist, this will be created now!"
          mkdir $currentLocation
  else
        echo "$currentLocation exists, does not need to be created"
  fi
done

rm -rf $softwareLocation/*
rm -rf $rpmLocation/*
rm -rf $earLocation/*
cp $softwareCopyLocation/model-service* $rpmLocation/
cp $softwareCopyLocation/ERICpmmedcore* $rpmLocation/
cp $softwareCopyLocation/ERICpmmedcom* $rpmLocation/
cp $softwareCopyLocation/ERICpmservice* $rpmLocation/
cp $softwareCopyLocation/ERICdatapersist* $rpmLocation/
cp $softwareCopyLocation/ERICtopsync* $rpmLocation/
cp $softwareCopyLocation/UpgradeManager* $rpmLocation/
cp $softwareCopyLocation/jcat-accessor* $rpmLocation/
cp $softwareCopyLocation/ERICtss-service* $rpmLocation/
cp $softwareCopyLocation/ERICcamel* $rpmLocation/
cp $softwareCopyLocation/ERICdatapath* $rpmLocation/
cp $softwareCopyLocation/ERICpib* $rpmLocation/

#rm -rf ./opt
#rpm2cpio $softwareCopyLocation/ERICcamel* | cpio -idmv
#cp ./opt/ericsson/com.ericsson.oss.mediation.camel-engine/camel-engine-jca-rar* $softwareLocation/
#cp /var/tmp/*_needed_channel-resources-jms.xml $softwareLocation/

# Please change these values if you want to run a different baseline installation
. baseline.txt

echo "PMService        " $PMServiceRelease
echo "Mediation Service" $MediationServiceRelease
echo "Mediation Core   " $MediationCoreRelease
echo "Data Persistence " $DataPeristenceRelease
echo "Topology Sync    " $TopologySyncRelease
echo "Tss Service      " $TssRelease
echo "Model Service    " $ModelServiceRelease
echo "Camel            " $CamelRelease
echo "Datapath         " $DatapathRelease
echo "PIB	       " $PlatformIntegrationBridgeRelease

DATAPATH="ERICdatapath_CXP9030305"
DATAPERSIST="ERICdatapersist_CXP9030106"
TOPSYNC="ERICtopsync_CXP9030160"
TSSSERVICE="ERICtss-service_CXP9023169"
MEDCORE="ERICpmmedcore_CXP9030102"
MEDSERV="ERICpmmedcom_CXP9030103"
PMSERV="ERICpmservice_CXP9030101"
CAMEL="ERICcamel_CXP9030311"
MODELSERVICE="ERICModelService_CXP9030157"
PIB="ERICpib_CXP9030194"

# List the RPMs available for installation
echo "List the RPMs available for installation"
ls -l $rpmLocation/*.rpm

rpm_names=( $DATAPATH $DATAPERSIST $TOPSYNC $TSSSERVICE $MEDCORE $MEDSERV $PMSERV $CAMEL $PIB )
rpm_versions=( $DatapathRelease $DataPeristenceRelease $TopologySyncRelease $TssRelease $MediationCoreRelease $MediationServiceRelease $PMServiceRelease $CamelRelease $PlatformIntegrationBridgeRelease )

# =========================
# Uninstall previous RPMs
# =========================
echo "Uninstall previous RPMs: ${#rpm_ipaddresses[*]}"
for(( j=0; j<${#rpm_ipaddresses[*]}; j++))
do
  echo "Entered uninstall loop: ${#rpm_names[*]}"
  sc_node=${rpm_ipaddresses[$j]}
  for(( i=0; i<${#rpm_names[*]}; i++))
  do
    echo "ssh root@$sc_node rpm -ev ${rpm_names[$i]}"
    ssh root@$sc_node "rpm -ev ${rpm_names[$i]}"
  done
  echo "ssh root@$sc_node rm -rf /opt/ericsson/oss_channel /opt/ericsson/oss_event /opt/ericsson/testDatapaths.xml"
  ssh root@$sc_node "rm -rf /opt/ericsson/oss_channel /opt/ericsson/oss_event /opt/ericsson/testDatapaths.xml"
done
# =====================
# Install all RPMs
# =====================
echo "Install all RPMs: ${#rpm_ipaddresses[*]}"
for(( j=0; j<${#rpm_ipaddresses[*]}; j++))
do
  echo "Entered install loop: ${#rpm_names[*]}"
  sc_node=${rpm_ipaddresses[$j]}
  echo "ssh root@$sc_node rm -rf $rpmLocation/*"
  ssh root@$sc_node "rm -rf $rpmLocation/*"
  ssh root@$sc_node "mkdir $rpmLocation"
  scp $rpmLocation/* root@$sc_node:$rpmLocation

  for(( i=0; i<${#rpm_names[*]}; i++))
  do
    echo "ssh root@$sc_node rpm -ivh $rpmLocation${rpm_names[$i]}-${rpm_versions[$i]}.rpm"
    ssh root@$sc_node "rpm -ivh $rpmLocation${rpm_names[$i]}-${rpm_versions[$i]}.rpm"
  done
done
# =============================
# Query Installation Status
# =============================
echo "Query RPM Installation Status: ${#rpm_ipaddresses[*]} "
for(( j=0; j<${#rpm_ipaddresses[*]}; j++))
do
  echo "Entered query loop: ${#rpm_names[*]} "
  sc_node=${rpm_ipaddresses[$j]}
  for(( i=0; i<${#rpm_names[*]}; i++))
  do
    echo "ssh root@$sc_node rpm -qi ${rpm_names[$i]}"
    ssh root@$sc_node "rpm -qi ${rpm_names[$i]}"
  done
done

# =================================
# Check result of RPM Installation
# =================================
echo "Check result of RPM Installation"
for(( j=0; j<${#rpm_ipaddresses[*]}; j++))
do
  ssh root@${rpm_ipaddresses[$j]} "ls -l /opt/ericsson/"
done
# ==========================================
# Copy Ears for deployment to MS1 from deployed rpm directory on SC node
# ==========================================
echo "Copy Ears for deployment to MS1 from deployed rpm directory on SC node"
#modelServiceFileName="model-service-ear-$ModelServiceRelease.ear"
mediationCoreFileName="mediationcore-$MediationCoreRelease.ear"
mediationServiceFileName="mediationservice-$MediationServiceRelease.ear"
pmServiceFileName="pmservice-ear-$PMServiceRelease.ear"
dataPersistanceFileName="DataPersistence-ear-$DataPeristenceRelease.ear"
topSyncFileName="TopologySync-ear-$TopologySyncRelease.ear"
tssFileName="tss-service-ear-$TssRelease.ear"
camelFileName="camel-engine-jca-rar-$CamelRelease.rar"
pibFileName="PlatformIntegrationBridge-ear-$PlatformIntegrationBridgeRelease.ear"


scp root@${rpm_ipaddresses[0]}:/opt/ericsson/com.ericsson.oss.itpf.datalayer.datapersist/$dataPersistanceFileName $earLocation
scp root@${rpm_ipaddresses[0]}:/opt/ericsson/com.ericsson.oss.services.topsync/$topSyncFileName $earLocation
scp root@${rpm_ipaddresses[0]}:/opt/ericsson/com.ericsson.nms.services.PMService/$pmServiceFileName $earLocation
scp root@${rpm_ipaddresses[0]}:/opt/ericsson/com.ericsson.oss.mediation.MediationCore/$mediationCoreFileName $earLocation
scp root@${rpm_ipaddresses[0]}:/opt/ericsson/com.ericsson.oss.mediation.MediationService/$mediationServiceFileName $earLocation
scp root@${rpm_ipaddresses[0]}:/opt/ericsson/com.ericsson.nms.mediation.TssService/$tssFileName $earLocation
scp root@${rpm_ipaddresses[0]}:/opt/ericsson/com.ericsson.oss.mediation.camel-engine/$camelFileName $earLocation
scp root@${rpm_ipaddresses[0]}:/opt/ericsson/com.ericsson.oss.itpf.common.PlatformIntegrationBridge/$pibFileName $earLocation
#cp $softwareCopyLocation/$modelServiceFileName $earLocation

