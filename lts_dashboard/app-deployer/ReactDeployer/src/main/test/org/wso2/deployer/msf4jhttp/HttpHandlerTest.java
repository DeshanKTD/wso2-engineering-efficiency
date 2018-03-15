package org.wso2.deployer.msf4jhttp;

import junit.framework.Assert;
import junit.framework.TestCase;

/*
 * TODO - comment class work
 */public class HttpHandlerTest extends TestCase {

    HttpHandler handler = null;
    public void setUp() throws Exception {
        handler = new HttpHandler();
    }

    public void testGet() throws Exception {
        String response = handler.get("/release/quarters");
        Assert.assertNotNull(response);
    }


}