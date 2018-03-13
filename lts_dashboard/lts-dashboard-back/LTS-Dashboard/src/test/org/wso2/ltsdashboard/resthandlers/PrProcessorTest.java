package org.wso2.ltsdashboard.resthandlers;

import com.google.gson.JsonArray;
import junit.framework.TestCase;
import org.junit.Assert;

/*
 * Test Pr processor
 */

public class PrProcessorTest extends TestCase {
    public void testGetPrsTForVersion() throws Exception {
        PrProcessor prProcessorTest = new PrProcessor();
        JsonArray prArray = prProcessorTest.getPrsForVersion(4,"2018-01-01","2018-03-31");
        Assert.assertNotNull(prArray);
    }

}

