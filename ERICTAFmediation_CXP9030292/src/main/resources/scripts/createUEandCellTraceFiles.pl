#!/usr/bin/perl


@dirs= ('00001','00002','00003','00004','00005','00006','00007','00008','00009','00010'
                ,'00011','00012','00013','00014','00015','00016','00017','00018','00019','00020'
                ,'00021','00022','00023','00024','00025','00026','00027','00028','00029','00030'
                ,'00031','00032','00033','00034','00035','00036','00037','00038','00039','00040'
                ,'00041','00042','00043','00044','00045','00046','00047','00048','00049','00050');


foreach $i(@dirs) {
						
	print "\nCalling tar -xvf /netsim/netsim_dbdir/simdir/netsim/netsimdir/ueAndCellTrace_pmData.tar -C /netsim/netsim_dbdir/simdir/netsim/netsimdir/LTEC1110x160-ST-FDD-5K-LTE01/LTE01ERBS".$i."/fs/c/\n";
	$cpCmd = "tar -xvf /netsim/netsim_dbdir/simdir/netsim/netsimdir/ueAndCellTrace_pmData.tar -C /netsim/netsim_dbdir/simdir/netsim/netsimdir/LTEC1110x160-ST-FDD-5K-LTE01/LTE01ERBS".$i."/fs/c/";
	system($cpCmd);

}
