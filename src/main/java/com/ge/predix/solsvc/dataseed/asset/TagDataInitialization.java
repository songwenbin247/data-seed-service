package com.ge.predix.solsvc.dataseed.asset;

import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.bootstrap.ams.dto.Tag;
import com.ge.predix.solsvc.bootstrap.ams.factories.TagFactory;

@Component
@SuppressWarnings("nls")
public class TagDataInitialization extends BaseDataHandler {
    private static final int METER_URI = 0;
    private static final int METER_NAME = 1;
    private static final int METER_DESC = 2;
    private static final int METER_TYPE = 3;
    private static final int METER_DATA_TYPE = 4;
    private static final int METER_UOM = 5;
    private static Logger logger = LoggerFactory.getLogger(TagDataInitialization.class);

    @Autowired
    private TagFactory tagFactory;

    @PostConstruct
    public void intilizeTagData() {
        logger.info("*******************Seed data Initialization complete*********************");
    }

    public void seedData(Map<String, String[][]> content, List<Header> headers) throws DataFormatException {
        String[][] tags = content.get("Tag");
        if (tags != null) {
            for (String[] row : tags) {
                if (row[METER_NAME] != null) {
                    Tag tag = new Tag();
                    tag.setUri(row[METER_URI]);
                    tag.setName(row[METER_NAME]);
                    tag.setUom(row[METER_UOM]);
                    tag.setDescription(row[METER_DESC]);
                    tag.setDataType(row[METER_DATA_TYPE]);
                    tag.setTagType(row[METER_TYPE]);

                    this.tagFactory.createTag(tag, headers);
                }
            }
        }
    }
}
