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

package org.wso2.ltsdashboard.tablepopulate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.wso2.ltsdashboard.connectionshandlers.GitHandlerImplement;
import org.wso2.ltsdashboard.connectionshandlers.SqlHandler;
import org.wso2.ltsdashboard.resthandlers.ProcessorCommon;

/*
 * TODO - comment class work
 */
public class TablePopulate {
    private String baseUrl = "https://localhost:9445/services/LtsDashboardService";
    private SqlHandler sqlHandler = new SqlHandler();
    private JsonArray orgs = new JsonArray();
    private GitHandlerImplement gitHandlerImplement = new GitHandlerImplement();


    void populateProducts() {
        JsonElement jsonElement = sqlHandler.get("/orgs/get", this.baseUrl);
        ProcessorCommon.checkValidResponseAndPopulateArray(jsonElement, "orgs", "org", orgs);

        JsonArray productArray = new JsonArray();
        JsonElement products = sqlHandler.get("/product/get", this.baseUrl);
        ProcessorCommon.checkValidResponseAndPopulateArray(products, "products", "product", productArray);

        for (JsonElement product : productArray) {
            JsonObject jsonObject = product.getAsJsonObject();
            String productName = jsonObject.get("productName").getAsString();

            JsonObject sendRawJsonObject = new JsonObject();
            sendRawJsonObject.addProperty("productName", productName);
            JsonObject sendObject = sqlHandler.createPostDataObject("_post_product_add", sendRawJsonObject);
            JsonElement returnSuccess = sqlHandler.post("/product/add", this.baseUrl, sendObject);
            System.out.println();

        }

        //get update array
        JsonArray wso2componentProduct = new JsonArray();
        JsonElement productwso2 = sqlHandler.get("/product/names");
        ProcessorCommon.checkValidResponseAndPopulateArray(productwso2, "products", "product", wso2componentProduct);

        for (JsonElement product : productArray) {
            JsonObject jsonObject = product.getAsJsonObject();
            int productId = jsonObject.get("productId").getAsInt();
            String productName = jsonObject.get("productName").getAsString();
            int productIdComponent = this.getProductId(productName, wso2componentProduct);

            JsonArray repos = new JsonArray();
            JsonObject sendObject = new JsonObject();
            sendObject.addProperty("productId", productId);
            sendObject = sqlHandler.createPostDataObject("_post_product_components", sendObject);
            JsonElement repoList = sqlHandler.post("/product/components", this.baseUrl, sendObject);
            ProcessorCommon.checkValidResponseAndPopulateArray(repoList, "components", "component", repos);

            for (JsonElement org : this.orgs) {
                JsonObject orgObject = org.getAsJsonObject();
                String orgname = orgObject.get("orgName").getAsString();
                int orgId = orgObject.get("orgId").getAsInt();

                for (JsonElement repo : repos) {
                    JsonObject jsonObject1 = repo.getAsJsonObject();
                    String repoName = null;
                    JsonArray data = new JsonArray();

                    try {
                        repoName = jsonObject1.get("repoName").getAsString();
                        String url = "https://api.github.com/repos/" + orgname + "/" + repoName + "/branches";
                        data = this.gitHandlerImplement.getJSONArrayFromGit(url);
                        if (repoName != null) {
                            System.out.println("adding : " + url);
                            if (data.size() > 0) {
                                JsonObject addRepoObject = new JsonObject();
                                addRepoObject.addProperty("repoName", repoName);
                                addRepoObject.addProperty("repoUrl", "https://github.com/" + orgname + "/" + repoName);
                                addRepoObject.addProperty("productId", productIdComponent);
                                addRepoObject.addProperty("orgId", orgId);

                                addRepoObject = this.sqlHandler.createPostDataObject("_post_repo_add", addRepoObject);
                                JsonElement ret = this.sqlHandler.post("/repo/add", this.baseUrl, addRepoObject);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }


                }

            }


        }


    }

    int getProductId(String productName, JsonArray productList) {
        int productId = 0;
        for (JsonElement element : productList) {
            JsonObject jsonObject = element.getAsJsonObject();
            String pName = jsonObject.get("productName").getAsString();
            int pId = jsonObject.get("productId").getAsInt();

            if (productName.equals(pName)) {
                productId = pId;
            }
        }

        return productId;
    }
}
