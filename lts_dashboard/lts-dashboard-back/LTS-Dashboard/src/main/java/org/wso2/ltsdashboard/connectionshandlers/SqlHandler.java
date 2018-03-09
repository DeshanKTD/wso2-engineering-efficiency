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

package org.wso2.ltsdashboard.connectionshandlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;


public class SqlHandler {
    private final static Logger logger = Logger.getLogger(SqlHandler.class);
    private String dssUrl;
    private String dssUserName;
    private String dssPassword;


    public SqlHandler() {
        PropertyReader propertyReader = new PropertyReader();
        this.dssUserName = propertyReader.getDssUser();
        this.dssPassword = propertyReader.getDssPassword();
        this.dssUrl = propertyReader.getDssUrl();
    }


    public JsonElement get(String uri) {
        return this.get(uri, this.dssUrl);
    }


    public JsonElement get(String uri, String dssUrl) {
        String url = dssUrl + uri;
        JsonElement element = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", dssUserName + ":" + dssPassword);

        try {

            HttpResponse response = httpClient.execute(request);
            logger.debug("Request successful for " + url);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            element = new JsonParser().parse(responseString);
        } catch (IllegalStateException e) {
            logger.error("The response is empty ");
        } catch (NullPointerException e) {
            logger.error("Bad request to the URL");
        } catch (IOException e) {
            logger.error("The request was unsuccessful with dss");
        }

        return element;
    }


    public JsonElement post(String uri, JsonObject data) {
        return this.post(uri, this.dssUrl, data);
    }

    public JsonElement post(String uri, String dssUrl, JsonObject data) {
        String url = dssUrl + uri;
        JsonElement element = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", dssUserName + ":" + dssPassword);
        request.addHeader("Content-Type", "application/json");

        try {
            StringEntity entity = new StringEntity(data.toString());
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            logger.debug("Request successful for " + url);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            element = new JsonParser().parse(responseString);

        } catch (IllegalStateException e) {
            logger.error("The response is empty ");
        } catch (NullPointerException e) {
            logger.error("Bad request to the URL");
        } catch (IOException e) {
            logger.error("The request was unsuccessful with dss");
        }

        return element;
    }


    public JsonObject createPostDataObject(String endpoint, JsonObject data) {
        JsonObject sendObject = new JsonObject();
        sendObject.add(endpoint, data);
        return sendObject;
    }


}
