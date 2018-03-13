package org.wso2.ltsdashboard.resthandlers;

import com.google.gson.JsonArray;
import junit.framework.TestCase;
import org.junit.Assert;

/*
 * TODO - comment class work
 */public class ProductProcessorTest extends TestCase {
    public void testGetProductList() throws Exception {
        ProductProcessor productProcessor = new ProductProcessor();
        JsonArray response = productProcessor.getProductList();
        Assert.assertNotNull(response);
    }

}