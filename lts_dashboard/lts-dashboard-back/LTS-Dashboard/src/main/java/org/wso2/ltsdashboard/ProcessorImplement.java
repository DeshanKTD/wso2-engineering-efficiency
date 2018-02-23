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


package org.wso2.ltsdashboard;

/*
 * Handle basic exposing methods for the API
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.wso2.ltsdashboard.connectionshandlers.GitHandlerImplement;
import org.wso2.ltsdashboard.connectionshandlers.SqlHandler;

import java.util.ArrayList;


public class ProcessorImplement {
    private final static Logger logger = Logger.getLogger(ProcessorImplement.class);
    private String gitBaseUrl = "https://api.github.com/";
    private GitHandlerImplement gitHandlerImplement;
    private String org = "wso2";
    private SqlHandler sqlHandler = null;


    ProcessorImplement() {
        this.gitHandlerImplement = new GitHandlerImplement();
        sqlHandler = new SqlHandler();

    }


    /**
     * Get the product list
     *
     * @return ArrayList of Product names
     */

    JsonArray getProductList() {
        String url = "/product/names";
        JsonElement returnElement = this.sqlHandler.get(url);
        JsonObject productsBasicObject = returnElement.getAsJsonObject();
        JsonArray productList = new JsonArray();

        if (productsBasicObject.has("products") &&
                productsBasicObject.get("products").getAsJsonObject().has("product")) {
            JsonArray productJsonArray = productsBasicObject
                    .get("products").getAsJsonObject().get("product")
                    .getAsJsonArray();
            productList = productJsonArray;

        }

        return productList;
    }


    /**
     * Get the versions for particular product
     *
     * @param productId - repo name
     * @return - json array of Label names
     */
    JsonArray getVersions(int productId) {
        String url = "/product/names";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("productId", productId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_product_version", jsonObject);

        JsonElement returnElement = this.sqlHandler.post(url, sendObject, true);
        JsonObject versionBasicObject = returnElement.getAsJsonObject();
        JsonArray versionJsonArray = new JsonArray();

        if (versionBasicObject.has("versions") &&
                versionBasicObject.get("versions").getAsJsonObject().has("version")) {
            JsonArray productJsonArray = versionBasicObject
                    .get("versions").getAsJsonObject().get("version")
                    .getAsJsonArray();
            versionJsonArray = productJsonArray;

        }

        return versionJsonArray;

    }


    void addVersion(int productId, String versionName) {
        String url = "/version/add";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("productId", productId);
        jsonObject.addProperty("versionName", versionName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_version_add", jsonObject);

        JsonElement returnElement = this.sqlHandler.post(url, sendObject, false);
    }


    JsonArray getRepos(int productId) {
        String url = "/product/repos";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("productId", productId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_version_add", jsonObject);

        JsonElement returnElement = this.sqlHandler.post(url, sendObject, true);
        JsonObject repoJsonObject = returnElement.getAsJsonObject();
        JsonArray repoJsonArray = new JsonArray();

        if (repoJsonObject.has("repositories") &&
                repoJsonObject.get("repositories").getAsJsonObject().has("repository")) {
            JsonArray productJsonArray = repoJsonObject
                    .get("repositories").getAsJsonObject().get("repository")
                    .getAsJsonArray();
            repoJsonArray = productJsonArray;

        }

        return repoJsonArray;


    }


    JsonArray getBranchesForRepo(int repoId, String repoName) {

        // get version labeled branches from db
        String url = "/repo/branches";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("repoId", repoId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_repo_branches", jsonObject);

        JsonElement returnElement = this.sqlHandler.post(url, sendObject, true);
        JsonObject branchJsonObject = returnElement.getAsJsonObject();
        JsonArray branchJsonArray = new JsonArray();

        if (branchJsonObject.has("branches") &&
                branchJsonObject.get("branches").getAsJsonObject().has("branch")) {
            branchJsonArray = branchJsonObject
                    .get("branches").getAsJsonObject().get("branch")
                    .getAsJsonArray();
        }

        // get branch names from db
        ArrayList<String> dbBranchNameArray = new ArrayList<>();
        for (JsonElement branch : branchJsonArray) {
            JsonObject branchObject = branch.getAsJsonObject();
            dbBranchNameArray.add(this.trimJsonElementString(branchObject.get("branchName")));
        }

        // get branches from git
        url = gitBaseUrl + "repos/" + this.org + "/" + repoName + "/branches";
        JsonArray gitBranchArray = this.gitHandlerImplement.getJSONArrayFromGit(url);
        for (JsonElement branch : gitBranchArray) {
            JsonObject branchObject = branch.getAsJsonObject();
            String name = trimJsonElementString(branchObject.get("name"));
            if (!dbBranchNameArray.contains(name)) {
                JsonObject tempBranchObject = new JsonObject();
                tempBranchObject.addProperty("branchId", -1);
                tempBranchObject.addProperty("branchName", name);
                tempBranchObject.addProperty("versionId", -1);
                tempBranchObject.addProperty("versionName", "null");
                branchJsonArray.add(tempBranchObject);
            }
        }

        return branchJsonArray;

    }

    /**
     * Get All features for product version
     *
     * @param versionId - version id to extract features
     * @return Feature Set as a json array
     */
    JsonArray getPrsForVersion(int versionId) {
        JsonArray issueList = new JsonArray();
        // get branch name repo name
        String url = "/product/repobranches";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_product_repobranches", jsonObject);
        JsonElement returnElement = this.sqlHandler.post(url, sendObject, true);
        JsonObject repoBranchJsonObject = returnElement.getAsJsonObject();
        JsonArray repoBranchJsonArray = new JsonArray();

        if (repoBranchJsonObject.has("versionDetails") &&
                repoBranchJsonObject.get("versionDetails").getAsJsonObject().has("versionDetail")) {
            JsonArray productJsonArray = repoBranchJsonObject
                    .get("versionDetails").getAsJsonObject().get("versionDetail")
                    .getAsJsonArray();
            repoBranchJsonArray.addAll(productJsonArray);

        }

        // getting event lists from issue list
        for (JsonElement branchData : repoBranchJsonArray) {
            JsonObject branchObject = branchData.getAsJsonObject();
            String branchName = this.trimJsonElementString(branchObject.get("branchName"));
            String repoName = this.trimJsonElementString(branchObject.get("repoName"));
            // get the last tag date

            // search for prs with branch name and date > tag date

//            JsonArray eventTempList = gitHandlerImplement.getJSONArrayFromGit(issueUrl,
//                    "application/vnd.github.mockingbird-preview");
        }
        return issueList;
    }


    void addBranchVersion(int versionId, String branchName, int repoId) {
        String url = "/branch/add";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("repoId", repoId);
        jsonObject.addProperty("versionId", versionId);
        jsonObject.addProperty("branchName", branchName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_branch_add", jsonObject);
        JsonElement returnElement = this.sqlHandler.post(url, sendObject, false);
    }


    void changeBranchVersion(int versionId, int branchId) {
        String url = "/branch/change";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        jsonObject.addProperty("branchId", branchId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_branch_change", jsonObject);
        JsonElement returnElement = this.sqlHandler.post(url, sendObject, false);
    }


    void changeVersionName(int versionId, String versionName) {
        String url = "/version/change";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        jsonObject.addProperty("versionName", versionName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_version_change", jsonObject);
        JsonElement returnElement = this.sqlHandler.post(url, sendObject, false);
    }


    void deleteVersionName(int versionId) {
        String url = "/version/delete";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_version_delete", jsonObject);
        JsonElement returnElement = this.sqlHandler.post(url, sendObject, false);

    }

    private String trimJsonElementString(JsonElement text) {
        return text.toString().replace("\"", "");
    }


}
