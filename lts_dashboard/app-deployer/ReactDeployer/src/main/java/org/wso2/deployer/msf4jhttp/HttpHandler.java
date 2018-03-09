/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.deployer.msf4jhttp;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/*
 * TODO - comment class work
 */
public class HttpHandler {

    private static final Logger logger = Logger.getLogger(HttpHandler.class);

    public String get(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", "application/json");
//        request.addHeader("Authorization", dssUserName + ":" + dssPassword);
        String responseString = null;

        try {

            HttpResponse response = httpClient.execute(request);
            logger.debug("Request successful for " + url);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

        } catch (IllegalStateException e) {
            logger.error("The response is empty ");
        } catch (NullPointerException e) {
            logger.error("Bad request to the URL");
        } catch (IOException e) {
            logger.error("The request was unsuccessful with dss");
        }

        return responseString;
    }


    public String post(String url, String object) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        request.addHeader("Accept", "application/json");
//        request.addHeader("Authorization", dssUserName + ":" + dssPassword);
        request.addHeader("Content-Type", "application/json");
        String responseString = null;

        try {
            StringEntity entity = new StringEntity(object);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            logger.debug("Request successful for " + url);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

        } catch (IllegalStateException e) {
            logger.error("The response is empty ");
        } catch (NullPointerException e) {
            logger.error("Bad request to the URL");
        } catch (IOException e) {
            logger.error("The request was unsuccessful with dss");
        }
        return responseString;
    }
}
