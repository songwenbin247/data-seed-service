package com.ge.predix.solsvc.dataseed.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.solsvc.bootstrap.ams.common.AssetConfig;
import com.ge.predix.solsvc.bootstrap.ams.factories.ModelFactory;
import com.ge.predix.solsvc.dataseed.asset.AssetDataInitialization;
import com.ge.predix.solsvc.dataseed.asset.ClassificationDataInitialization;
import com.ge.predix.solsvc.dataseed.asset.GroupDataInitialization;
import com.ge.predix.solsvc.dataseed.asset.TagDataInitialization;
import com.ge.predix.solsvc.dataseed.util.SpreadSheetParser;
import com.ge.predix.solsvc.restclient.impl.RestClient;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

/**
 * Loads data in to Predix Asset
 * @author predix -
 */
@Component
public class DataSeedService {
    private static final Logger logger = LoggerFactory.getLogger(DataSeedService.class);

    @Autowired
    private AssetDataInitialization assetDataInit;

    @Autowired
    private TagDataInitialization tagDataInit;

    @Autowired
    private GroupDataInitialization groupDataInit;

    @Autowired
    private ClassificationDataInitialization classDataInit;

    @Autowired
    private RestClient restClient;

    @Autowired
    private ModelFactory modelFactory;

    @Autowired
    private AssetConfig assetConfig;

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Upload Excel spreadsheet to Predix Asset
     * @param name -
     * @param data -
     * @return -
     * @throws DataFormatException -
     */
    public String uploadXlsData(String name, InputStream data) throws DataFormatException {
        List<String> workSheets = new ArrayList<>();
        workSheets.add("Asset"); //$NON-NLS-1$
        workSheets.add("Fields"); //$NON-NLS-1$
        workSheets.add("Tag"); //$NON-NLS-1$
        workSheets.add("Classification"); //$NON-NLS-1$
        workSheets.add("Group"); //$NON-NLS-1$

        SpreadSheetParser parser = new SpreadSheetParser();
        Map<String, String[][]> content = parser.parseInputFile(data, workSheets);

        logger.debug("zoneId=" + this.assetConfig.getZoneId()); //$NON-NLS-1$
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());

        this.classDataInit.seedData(content, headers);
        this.groupDataInit.seedData(content, headers);
        this.tagDataInit.seedData(content, headers);
        this.assetDataInit.seedData(content, headers);

        return "You successfully uploaded " + name + "!"; //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Upload an Xml file to Predix Asset
     * @param name -
     * @param data -
     * @return -
     * @throws IOException -
     */
    @SuppressWarnings("unchecked")
	public String uploadXmlData(String name, InputStream data) throws IOException {
        XMLSerializer serializer = new XMLSerializer();
        JSON json = serializer.readFromStream(data);
        @SuppressWarnings("rawtypes")
		Map map = this.mapper.readValue(json.toString(), Map.class);
        uploadJson(map);

        return String.format("You successfully uploaded %s!", name); //$NON-NLS-1$
    	
    }

    /**
     * Upload a JSON File to Predix Asset
     * @param name -
     * @param data -
     * @return -
     * @throws IOException -
     */
    @SuppressWarnings("unchecked")
	public String uploadJsonData(String name, InputStream data) throws IOException {
        logger.debug("Processing Json data file " + name); //$NON-NLS-1$
        @SuppressWarnings("rawtypes")
		Map map = this.mapper.readValue(data, Map.class);
        uploadJson(map);

        return "You successfully uploaded " + name + "!"; //$NON-NLS-1$//$NON-NLS-2$;
    }

    @Autowired(required = false)
	@SuppressWarnings({ "unchecked", "nls", "rawtypes" })
	private void uploadJson(Map<String,?> map) {
        logger.debug("zoneId={}", this.assetConfig.getZoneId());
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());

        for (Object key : map.keySet()) {
            Object value = map.get(key);

            if (value instanceof List) {
                this.modelFactory.createModel((List) value, headers);
            } else {
                List<Object> list = new LinkedList<>();
                list.add(value);
                this.modelFactory.createModel(list, headers);
            }
        }
    }
}
