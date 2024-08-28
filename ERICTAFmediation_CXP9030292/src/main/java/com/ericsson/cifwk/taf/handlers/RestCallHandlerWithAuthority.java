package com.ericsson.cifwk.taf.handlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.tal.ToolExecutor;
import com.ericsson.cifwk.taf.tal.implementation.rest.curl.CurlRestHandler;
import com.ericsson.cifwk.taf.tal.rest.RestExecutionParameters;
import com.ericsson.cifwk.taf.tal.rest.RestOperation;
import com.ericsson.cifwk.taf.tal.rest.RestPostEncoding;
import com.ericsson.cifwk.taf.tal.rest.RestProtocol;
import com.ericsson.cifwk.taf.tal.rest.RestResponse;
import com.ericsson.cifwk.taf.tal.rest.RestResponseCode;

public class RestCallHandlerWithAuthority {

	private Logger logger = Logger.getLogger(this.getClass());
	enum UpgradeOperationType {
		SERVICE("SERVICE");
		private String name;
		private UpgradeOperationType(String name) {
			this.name = name;
		}
		@Override
		public String toString(){
			return name;
		}
	}
	
	enum UpgradeOperationPhase {
		SERVICE_INSTANCE_UPGRADE_PREPARE("SERVICE_INSTANCE_UPGRADE_PREPARE");
		private String name;
		private UpgradeOperationPhase(String name) {
				this.name = name;
		}
		
		@Override
		public String toString(){
			return name;
		}
	}
	
//	public static String UPGRADE_MANAGER_URI = "UpgradeManager/rest/upgradeService/upgrade";
//	public String doServiceFrameworkJobForProducingUpgradeManagerOperator(String host,String port,String user, String pass,  String appServerId, String serviceId, UpgradeOperationType upgradeType, UpgradeOperationPhase phase ){
//		Map<String,String> restUriParameters = new HashMap<String,String>();
//		restUriParameters.put("user", user);
//		restUriParameters.put("password", pass);
//		restUriParameters.put("app_server_identifier", appServerId);
//		restUriParameters.put("service_identifier", serviceId);
//		restUriParameters.put("upgrade_operation_type",upgradeType.toString());
//		restUriParameters.put("upgrade_phase",phase.toString());
//		
//		Map<String,String> authenticationParams = new HashMap<String,String>();
//		authenticationParams.put(user, pass);
//		RestExecutionParameters params = new RestExecutionParameters(RestOperation.GET,
//				RestPostEncoding.URL,
//				restUriParameters,
//				RestProtocol.HTTP,
//				host,
//				port,
//				UPGRADE_MANAGER_URI,
//				authenticationParams);
//		
//		
//		ToolExecutor restTool = new CurlRestHandler();
//		try {
//			restTool.execute(params);
//		} catch (Exception e) {
//			logger.error("Error during executing rest call",e);
//		}
//		RestResponse response = (RestResponse) restTool.getResponse();
//		if (response.getResponseMap().get(RestResponse.RESPONSE_CODE) != RestResponseCode.OK)
//			throw new RuntimeException("Problem returned from rest call: " + response.getResponseMap().get(RestResponse.RESPONSE_CONTENT) + response.getResponseMap().get(RestResponse.ERROR_RESPONSE_CONTENT));
//		return (String) response.getResponseMap().get(RestResponse.RESPONSE_CONTENT);
//	}


//	public static void main(String[] args){
//		new RestCallHandlerWithAuthority().doServiceFrameworkJobForProducingUpgradeManagerOperator("10.45.239.166", "8080", "guest", "guestp", "atrcxb1139", "MediationService1", UpgradeOperationType.SERVICE, UpgradeOperationPhase.SERVICE_INSTANCE_UPGRADE_PREPARE);
//	}
	
}
