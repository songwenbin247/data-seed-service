package com.ge.predix.solsvc.dataseed.service;

import java.util.List;

import org.apache.http.Header;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ge.predix.solsvc.dataseed.asset.AssetDataHarvest;
import com.ge.predix.solsvc.dataseed.boot.DataseedServiceApplication;
import com.ge.predix.solsvc.restclient.impl.RestClient;

//@RunWith(SpringJUnit4ClassRunner.class)
/**
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DataseedServiceApplication.class)
@WebAppConfiguration
@IntegrationTest({"server.port=0"})@SuppressWarnings("nls")
public class DataseedServiceHarvest {

	@Autowired 
	private AssetDataHarvest assetDataHarvest;
	
	@Autowired
	private RestClient restClient;
	
	/**
	 * 
	 */
    @Test
	public void harvestTest() {
        //String baseUri = getBaseUri();
       // String url = baseUri + "/asset";
        
		List<Header> authorization = this.restClient.getSecureTokenForClientId() ;
		String appId = "rmdapp";
		this.assetDataHarvest.harvestData(authorization.toString(), appId);
	}
}
