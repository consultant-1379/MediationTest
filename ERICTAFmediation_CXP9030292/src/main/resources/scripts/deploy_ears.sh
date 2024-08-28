#!/bin/bash


# Receive ip addresses of jboss instances from parent classes
PMMed_0_ipaddress=$1
PMMed_1_ipaddress=$2
PMServ_0_ipaddress=$3
PMServ_1_ipaddress=$4
FMServ_0_ipaddress=$5
FMServ_1_ipaddress=$6

# variables needed for this script
softwareLocation="/var/tmp/mediation_software_location/"
rpmLocation="/var/tmp/mediation_rpm_location/"
earLocation="/var/tmp/mediation_ear_location/"
jboss_user="admin"
jboss_pw="12shroot"
numOfNodesToKill=4
numOfNodesToStart=4
numOfMedNodes=2
numOfPmNodes=2
delay_between_deployments=15

pmMed0DirName=`ssh root@SC-1 "ls /home/jboss/ | grep PMMed"`
pmMed1DirName=`ssh root@SC-2 "ls /home/jboss/ | grep PMMed"`
pmServ0DirName=`ssh root@SC-1 "ls /home/jboss/ | grep PMServ"`
pmServ1DirName=`ssh root@SC-2 "ls /home/jboss/ | grep PMServ"`
fmServ0DirName=`ssh root@SC-1 "ls /home/jboss/ | grep FMServ"`
fmServ1DirName=`ssh root@SC-2 "ls /home/jboss/ | grep FMServ"`

echo "$pmMed0DirName $pmMed1DirName $pmServ0DirName $pmServ1DirName"

######Params for running on SC-2#######################
jboss_ip_addresses=( $PMMed_0_ipaddress $PMServ_0_ipaddress $PMMed_1_ipaddress $PMServ_1_ipaddress $FMServ_0_ipaddress $FMServ_1_ipaddress )
dir_names=( $pmMed0DirName PMServ_si_0_jee_instance PMMed_si_1_jee_instance PMServ_si_1_jee_instance FMServ_si_0_jee_instance FMServ_si_1_jee_instance )
jboss_names=( safSu=PMMed_App-SuType-0,safSg=PMMed,safApp=PMMed_App safSu=PMServ_App-SuType-0,safSg=PMServ,safApp=PMServ_App safSu=PMMed_App-SuType-1,safSg=PMMed,safApp=PMMed_App safSu=PMServ_App-SuType-1,safSg=PMServ,safApp=PMServ_App safSu=FMServ_App-SuType-0,safSg=FMServ,safApp=FMServ_App safSu=FMServ_App-SuType-1,safSg=FMServ,safApp=FMServ_App )
pm_jboss_names=( safSu=PMServ_App-SuType-0,safSg=PMServ,safApp=PMServ_App safSu=PMServ_App-SuType-1,safSg=PMServ,safApp=PMServ_App )
med_jboss_names=( safSu=PMMed_App-SuType-0,safSg=PMMed,safApp=PMMed_App safSu=PMMed_App-SuType-1,safSg=PMMed,safApp=PMMed_App )
pm_ip_addresses=( $PMServ_0_ipaddress $PMServ_1_ipaddress )
med_ip_addresses=( $PMMed_0_ipaddress $PMMed_1_ipaddress )

ip_addresses=( "SC-1" "SC-1" "SC-2" "SC-2" "SC-1" "SC-2" )
rpm_ipaddresses=( "SC-1" "SC-2" )

# Make sure necessary directories are available
echo "Testing to see if jboss directory is available"
if [[ `test -f /opt/jboss-eap/bin/jboss-cli.sh && test -f /opt/jboss-eap/jboss-modules.jar && test -d /opt/jboss-eap/modules && echo true` != "true"  ]]; then
        echo "File /opt/jboss-eap/bin/jboss-cli.sh does not exist so do something"
        dir_name=${dir_names[0]}
        ipaddress=${ip_addresses[0]}
        mkdir -p /opt/jboss-eap/bin/
        scp root@$ipaddress:/home/jboss/$dir_name/bin/jboss-cli.sh /opt/jboss-eap/bin/
        scp root@$ipaddress:/home/jboss/$dir_name/jboss-modules.jar /opt/jboss-eap/
        scp root@$ipaddress:/home/jboss/$dir_name/bin/jboss-cli-logging.properties /opt/jboss-eap/bin/jboss-cli-logging.properties
        mkdir -p /opt/jboss-eap/modules
        scp -r root@$ipaddress:/home/jboss/$dir_name/modules/* /opt/jboss-eap/modules/
        chmod +x /opt/jboss-eap/bin/jboss-cli.sh /opt/jboss-eap/jboss-modules.jar
else
        echo "File /opt/jboss-eap/bin/jboss-cli.sh exists so do nothing"
fi


for(( i=0; i<$numOfNodesToKill; i++))
do
        ipaddress=${ip_addresses[$i]}
        jboss_name=${jboss_names[$i]}
        dir_name=${dir_names[$i]}
        jboss_dir=/home/jboss/$dir_name
        jboss_ipaddress=${jboss_ip_addresses[$i]}

        echo "Stop JBoss instance $jboss_name with IP address $jboss_ipaddress"
        echo "  \"ssh root@$ipaddress \"amf-adm lock $jboss_name\""
        ssh root@$ipaddress "amf-adm lock $jboss_name"
        echo "  Destroy tmp and data directories "
#       ssh root@$ipaddress "rm -rf $jboss_dir/standalone/tmp"
#	ssh root@$ipaddress "rm -rf $jboss_dir/standalone/data"
        echo "  ssh root@$ipaddress rm -rf $jboss_dir/*"
        ssh root@$ipaddress "rm -rf $jboss_dir/*"
        ssh root@$ipaddress "truncate /var/log/jboss/$dir_name/server.log --size=0"

done


# Restart and clean down the jboss instances
echo "Sleeping for $delay_between_deployments seconds to allow JBoss time to stop"
sleep $delay_between_deployments

echo "Cleaning out sfwk cache"
ssh root@SC-1 "rm -rf /tmp/sdk_configuration_parameters/*"
ssh root@SC-2 "rm -rf /tmp/sdk_configuration_parameters/*"

echo "Cleaning out data persistance cache"
ssh root@SC-1 "rm -rf /ericsson/tor/no_rollback/*"

echo "Copying across Med jboss file to the correct directory"
ssh root@SC-1 "cp /var/tmp/jboss-eap-ericsson-6.0.tgz_med /opt/ericsson/nms/jboss/jboss-eap-ericsson-6.0.tgz"
ssh root@SC-1 "scp /var/tmp/jboss-eap-ericsson-6.0.tgz_med root@SC-2:/opt/ericsson/nms/jboss/jboss-eap-ericsson-6.0.tgz"

for(( i=0; i<$numOfMedNodes; i++))
do
        ipaddress=${rpm_ipaddresses[$i]}
        jboss_name=${med_jboss_names[$i]}
        echo "Start new JBoss instance $jboss_name with Ip address $ipaddress"
        echo "  \"ssh root@$ipaddress \"amf-adm unlock $jboss_name\""
        ssh root@$ipaddress "amf-adm unlock $jboss_name"
done

echo "Copying across PM jboss file to the correct directory"
ssh root@SC-1 "cp /var/tmp/jboss-eap-ericsson-6.0.tgz_pm /opt/ericsson/nms/jboss/jboss-eap-ericsson-6.0.tgz"
ssh root@SC-1 "scp /var/tmp/jboss-eap-ericsson-6.0.tgz_pm root@SC-2:/opt/ericsson/nms/jboss/jboss-eap-ericsson-6.0.tgz"

for(( i=0; i<$numOfPmNodes; i++))
do
        ipaddress=${rpm_ipaddresses[$i]}
        jboss_name=${pm_jboss_names[$i]}
        echo "Start new JBoss instance $jboss_name with Ip address $ipaddress"
        echo "  \"ssh root@$ipaddress \"amf-adm unlock $jboss_name\""
        ssh root@$ipaddress "amf-adm unlock $jboss_name"
done

echo "Sleeping for $delay_between_deployments seconds to allow JBoss to start"
sleep $delay_between_deployments


# Deploy ear files located in /var/tmp/mediation_ear_location/ on the fresh jboss instances
cd $earLocation
mediationCoreFileName=`ls mediationcore*`
mediationServiceFileName=`ls mediationservice*`
pmServiceFileName=`ls pmservice-ear*`
dataPersistanceFileName=`ls DataPersistence-ear*`
topSyncFileName=`ls TopologySync-ear*`
tssFileName=`ls tss-service*`
camelFileName=`ls camel-engine-jca-rar*`
pibFileName=`ls PlatformIntegrationBridge*`

echo "Value for mediationCoreEarFile is: $mediationCoreFileName"
echo "Value for mediationServiceEarFile is: $mediationServiceFileName"
echo "Value for pmServiceEarFile is: $pmServiceFileName"
echo "Value for dataPersistanceEarFile is: $dataPersistanceFileName"
echo "Value for topSyncEarFile is: $topSyncFileName"
echo "Value for tssEarFile is: $tssFileName"
echo "Value for camelEarFile is: $camelFileName"
echo "Value for pibEarFile is: $pibFileName"

med_earFilesDeploy=( $tssFileName $mediationCoreFileName $mediationServiceFileName $camelFileName $pibFileName )
pm_earFilesDeploy=( $dataPersistanceFileName $pmServiceFileName $topSyncFileName $pibFileName )


echo "Deploying PmService software"
for(( j=0; j<${#pm_earFilesDeploy[*]}; j++))
do
        earFileToDeploy=${pm_earFilesDeploy[$j]}
        for(( i=0; i<$numOfPmNodes; i++))
        do
                ipaddress=${pm_ip_addresses[$i]}
                jboss_name=${pm_jboss_names[$i]}
                echo "  /opt/jboss-eap/bin/jboss-cli.sh --connect --controller=$ipaddress --user=$jboss_user --password=$jboss_pw -c \"deploy $earLocation$earFileToDeploy\""
                /opt/jboss-eap/bin/jboss-cli.sh --connect --controller=$ipaddress --user=$jboss_user --password=$jboss_pw -c "deploy $earLocation$earFileToDeploy"
                sleep 1
        done
done


echo "Deploying MediationCore and MediationService software"
for(( j=0; j<${#med_earFilesDeploy[*]}; j++))
do
        earFileToDeploy=${med_earFilesDeploy[$j]}
        for(( i=0; i<$numOfMedNodes; i++))
        do
                ipaddress=${med_ip_addresses[$i]}
                jboss_name=${med_jboss_names[$i]}
                echo "  Deploying $earLocation$earFileToDeploy ear file on $jboss_name with Ip address $ipaddress"
                echo "  /opt/jboss-eap/bin/jboss-cli.sh --connect --controller=$ipaddress --user=$jboss_user --password=$jboss_pw -c \"deploy $earLocation$earFileToDeploy\""
                /opt/jboss-eap/bin/jboss-cli.sh --connect --controller=$ipaddress --user=$jboss_user --password=$jboss_pw -c "deploy $earLocation$earFileToDeploy"
                sleep 1
        done
done

echo "Sleeping $delay_between_deployments seconds to allow DPS time to sync"
sleep $delay_between_deployments

