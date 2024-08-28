/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.cifwk.taf.handlers

class NetSimHandler {
	
	List<String> getNodeNames(int noOfLTEElements=5, int noOfRNC36Elements=5, int noOfRNC37Elements=5, int noOfSFTPElements=5){
		List<String> result = []
		
		if(noOfLTEElements > 0){
			for (int i : 1..Math.min(9,noOfLTEElements)){
				result << "LTE01ERBS0000$i".toString() 
			}	
			if(noOfLTEElements > 9){
				for (int i : 10..Math.min(99,noOfLTEElements)){
					result << "LTE01ERBS000$i".toString()
				}
			}
			if(noOfLTEElements > 99){
				for (int i : 100..Math.min(999,noOfLTEElements)){
					result << "LTE01ERBS00$i".toString()
				}
			}
		}
		
		if(noOfRNC36Elements > 0){
			for (int i : 1..Math.min(9,noOfRNC36Elements)){
				result << "RNC36RBS0$i".toString()
			}
			if(noOfRNC36Elements > 9){
				for (int i : 10..Math.min(999,noOfRNC36Elements)){
					result << "RNC36RBS$i".toString()
				}
			}
		}
		
		if(noOfRNC37Elements > 0){
			for (int i : 1..Math.min(9,noOfRNC37Elements)){
				result << "RNC37RBS0$i".toString()
			}
			if(noOfRNC37Elements > 9){
				for (int i : 10..Math.min(999,noOfRNC37Elements)){
					result << "RNC37RBS$i".toString()
				}
			}
		}
		
		if(noOfSFTPElements > 0){
			for (int i : 1..Math.min(9,noOfSFTPElements)){
				result << "RNC26RBS0$i".
				toString()
			}
			
			if(noOfSFTPElements > 9){
				for (int i : 10..Math.min(999,noOfSFTPElements)){
					result << "RNC26RBS$i".toString()
				}
			}
		}
	
		
		return result
	}
	
	List<String> getNodeName(int noOfLTEElements=5){
		List<String> result = []
		
		for (int i : 1..Math.min(9,noOfSFTPElements)){
			result << "RNC26RBS0$i".
			toString()
		}
//		for (int i : 10..Math.min(999,noOfSFTPElements)){
//			result << "RNC26RBS$i".toString()
//		}
		
		return result
	}
	
	
	List<String> getRNCNodeName(int noOfRNC36Elements=5){
		List<String> result = []
		
		for (int i : 1..Math.min(9,noOfRNC36Elements)){
			result << "RNC26RBS0$i".toString()
		}
//		for (int i : 10..Math.min(999,noOfRNC36Elements)){
//			result << "RNC26RBS$i".toString()
//		}
		
		return result
	}
	
		
	void placeFileOnFtpServer(final String currentSourceDir) {
	// final FTPClient client = new FTPClient();
	// final FileInputStream fis = null;
	// try {
	// client.connect(nodeIpAddress, 21);
	// System.out.print(client.getReplyString());
	// client.changeWorkingDirectory(sourceDir);
	// System.out.print(client.getReplyString());
	// /** change to BIS */
	// fis = new FileInputStream(localSourceDir + sourceFile);
	// client.storeFile(nodeName + templateFileType, fis);
	// System.out.print(client.getReplyString());
	// client.logout();
	// client.disconnect();
	// } catch (SocketException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }		
}
	
	
}
