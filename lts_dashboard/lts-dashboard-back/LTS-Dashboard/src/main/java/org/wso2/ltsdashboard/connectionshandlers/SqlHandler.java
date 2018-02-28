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

import com.google.gson.JsonArray;
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
import java.util.ArrayList;


public class SqlHandler {
    private final static Logger logger = Logger.getLogger(SqlHandler.class);
    private String dssUrl;
    private String dssUserName;
    private String dssPassword;
    private String gitBaseUrl;
    // queries


    public SqlHandler() {
        PropertyReader propertyReader = new PropertyReader();
        this.dssUserName = propertyReader.getDssUser();
        this.dssPassword = propertyReader.getDssPassword();
        this.dssUrl = propertyReader.getDssUrl();
        this.gitBaseUrl = propertyReader.getGitBaseUrl();
    }

    public static void main(String[] args) {
        SqlHandler sqlHandler = new SqlHandler();
//        JsonElement data = sqlHandler.get("/table/products");
//        JsonObject jsonObject = data.getAsJsonObject();
//        System.out.println("hi");
        sqlHandler.createProductNamesAndRepos();
    }

    public JsonElement get(String uri) {
        String url = this.dssUrl + uri;
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

    public JsonElement post(String uri, JsonObject data, boolean isResponse) {
        String url = this.dssUrl + uri;
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
            if (isResponse) {
                element = new JsonParser().parse(responseString);
            }
        } catch (IllegalStateException e) {
            logger.error("The response is empty ");
        } catch (NullPointerException e) {
            logger.error("Bad request to the URL");
        } catch (IOException e) {
            logger.error("The request was unsuccessful with dss");
        }

        return element;
    }

    void createProductNamesAndRepos() {

        // populate product
        JsonElement productNamesEx = this.get("/table/products");
        JsonObject productListObject = productNamesEx.getAsJsonObject();
        if (productListObject.has("products") &&
                productListObject.get("products").getAsJsonObject().has("product")) {
            JsonArray productJsonArray = productListObject
                    .get("products").getAsJsonObject().get("product")
                    .getAsJsonArray();
            for (JsonElement productName : productJsonArray) {
                String pName = productName.getAsJsonObject().get("productName").toString();
                JsonObject prodNameInsert = new JsonObject();
                prodNameInsert.addProperty("productName", this.trimString(pName));
                JsonObject sendObject = createPostDataObject("_post_product_add", prodNameInsert);
                JsonElement returnElement = this.post("/product/add", sendObject, false);
            }
        }

        // populate repos
        JsonElement productNameObjects = this.get("/product/names");
        productListObject = productNameObjects.getAsJsonObject();


        GitHandler gitHandler = new GitHandlerImplement();

        if (productListObject.has("products") &&
                productListObject.get("products").getAsJsonObject().has("product")) {
            JsonArray productJsonArray = productListObject
                    .get("products").getAsJsonObject().get("product")
                    .getAsJsonArray();
            // iterate in products
            for (JsonElement product : productJsonArray) {
                // existing repo list
                ArrayList<String> existintRepoNames = new ArrayList<>();

                // create product repo object
                int productId = product.getAsJsonObject().get("productId").getAsInt();
                String productName = this.trimString(product.getAsJsonObject().get("productName").toString());

                // check for duplicates
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("productId", productId);
                JsonObject sendObject = createPostDataObject("_post_product_repos", jsonObject);
                JsonElement returnElement = this.post("/product/repos", sendObject, true);
                JsonObject returnObject = returnElement.getAsJsonObject();
                if (returnObject.has("repositories") &&
                        returnObject.get("repositories").getAsJsonObject().has("repository")) {
                    JsonArray repoArray = returnObject.get("repositories")
                            .getAsJsonObject()
                            .get("repository")
                            .getAsJsonArray();

                    for (JsonElement repo : repoArray) {
                        existintRepoNames.add(this.trimString(repo.getAsJsonObject().get("repoName").toString()));
                    }
                }

                // get repos from jnks_dashboards
                jsonObject = new JsonObject();
                jsonObject.addProperty("productName", productName);
                sendObject = createPostDataObject("_post_table_repos", jsonObject);
                returnElement = this.post("/table/repos", sendObject, true);
                returnObject = returnElement.getAsJsonObject();

                if (returnObject.has("repos") &&
                        returnObject.get("repos").getAsJsonObject().has("repo")) {
                    JsonArray repoArray = returnObject.get("repos")
                            .getAsJsonObject()
                            .get("repo")
                            .getAsJsonArray();

                    for (JsonElement repo : repoArray) {
                        String repoName = this.trimString(repo.getAsJsonObject().get("repoName").toString());
                        JsonArray repoDetail = gitHandler
                                .getJSONArrayFromGit(gitBaseUrl + "/repos/wso2/" + repoName + "/labels");
                        if (repoDetail != null && repoDetail.size() > 0) {
                            if (!existintRepoNames.contains(repoName)) {
                                // add repo to repo list
                                jsonObject = new JsonObject();
                                jsonObject.addProperty("productId", productId);
                                jsonObject.addProperty("repoName", repoName);
                                sendObject = createPostDataObject("_post_repo_add", jsonObject);
                                returnElement = this.post("/repo/add", sendObject, false);
                            }
                        }
                    }
                }
            } // end for loop of product
        }
    }


    public JsonObject createPostDataObject(String endpoint, JsonObject data) {
        JsonObject sendObject = new JsonObject();
        sendObject.add(endpoint, data);
        return sendObject;
    }

    String trimString(String text) {
        return text.replace("\"", "").replace("\\", "");
    }


}
