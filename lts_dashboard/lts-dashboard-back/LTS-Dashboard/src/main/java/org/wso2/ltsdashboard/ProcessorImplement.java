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
import org.wso2.ltsdashboard.connectionshandlers.PropertyReader;
import org.wso2.ltsdashboard.connectionshandlers.SqlHandler;
import org.wso2.ltsdashboard.gitobjects.PullRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ProcessorImplement {
    private final static Logger logger = Logger.getLogger(ProcessorImplement.class);
    private String gitBaseUrl;
    private GitHandlerImplement gitHandlerImplement;
    private String org;
    private SqlHandler sqlHandler = null;


    ProcessorImplement() {
        this.gitHandlerImplement = new GitHandlerImplement();
        PropertyReader propertyReader = new PropertyReader();
        sqlHandler = new SqlHandler();
        this.gitBaseUrl = propertyReader.getGitBaseUrl();
        this.org = propertyReader.getOrg();

    }

    public static void main(String[] args) {
        ProcessorImplement processorImplement = new ProcessorImplement();
//        processorImplement.getPrsForVersion(16);
        JsonArray jsonObject = processorImplement.getQuaterDates();
        System.out.println("hi");

    }

    /**
     * Get the product list
     *
     * @return ArrayList of Product names
     */

    JsonArray getProductList() {
        String url = "/product/names";
        JsonElement returnElement = this.sqlHandler.get(url);
        JsonArray productList = new JsonArray();
        this.checkValidResponseAndPopulateArray(returnElement, "products", "product", productList);

        return productList;
    }

    /**
     * Get the versions for particular product
     *
     * @param productName - repo name
     * @return - json array of Label names
     */
    JsonArray getVersions(String productName) {
        String url = "/product/version";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("productName", productName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_product_version", jsonObject);

        JsonElement returnElement = this.sqlHandler.post(url, sendObject, true);
        JsonArray versionJsonArray = new JsonArray();
        this.checkValidResponseAndPopulateArray(returnElement, "versions", "version", versionJsonArray);

        return versionJsonArray;
    }

    void addVersion(String productName, String versionName) {
        String url = "/version/add";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("productName", productName);
        jsonObject.addProperty("versionName", versionName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_version_add", jsonObject);

        this.sqlHandler.post(url, sendObject, false);
    }

    JsonArray getRepos(String productName) {
        String url = "/product/repos";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("productName", productName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_product_repos", jsonObject);

        JsonElement returnElement = this.sqlHandler.post(url, sendObject, true);
        JsonArray repoJsonArray = new JsonArray();
        this.checkValidResponseAndPopulateArray(returnElement, "repositories", "repository", repoJsonArray);

        return repoJsonArray;
    }


    /**
     * The system checks for the branches of current repo in db. The branches in db are tagged with a version
     * There can be new branches crated at git remote. So theses branches also needed to be display for tagging with
     * a version.
     *
     * @param repoUrl   - repo Id
     * @param repoName - repo name
     * @return array of prs with features
     */
    JsonArray getBranchesForRepo(String repoUrl, String repoName) {
        // get version labeled branches from db
        String url = "/repo/branches";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("repoName", repoName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_repo_branches", jsonObject);

        JsonElement returnElement = this.sqlHandler.post(url, sendObject, true);

        JsonArray branchJsonArray = new JsonArray();
        this.checkValidResponseAndPopulateArray(returnElement, "branches", "branch", branchJsonArray);
        // get branch names from db
        ArrayList<String> dbBranchNameArray = new ArrayList<>();
        for (JsonElement branch : branchJsonArray) {
            JsonObject branchObject = branch.getAsJsonObject();
            dbBranchNameArray.add(this.trimJsonElementString(branchObject.get("branchName")));
        }

        String gitRepoName = this.extractRepoName(repoUrl);


        logger.debug("Extracting branch data for " + repoName + "from git");
        // get branches from git
        url = gitBaseUrl + "repos/" + gitRepoName + "/branches";
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
    JsonArray getPrsForVersion(int versionId, String startDate, String endDate) {
        JsonArray prFeatureArray = new JsonArray();
        // get branch name repo name
        String url = "/product/repobranches";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_product_repobranches", jsonObject);
        JsonElement returnElement = this.sqlHandler.post(url, sendObject, true);
        JsonArray repoBranchJsonArray = new JsonArray();
        this.checkValidResponseAndPopulateArray(returnElement, "versionDetails", "versionDetail", repoBranchJsonArray);

        // getting event lists from issue list
        for (JsonElement branchData : repoBranchJsonArray) {
            JsonObject branchObject = branchData.getAsJsonObject();
            String branchName = this.trimJsonElementString(branchObject.get("branchName"));
            String repoUrl = this.trimJsonElementString(branchObject.get("repoUrl"));
            String repoName = this.extractRepoName(repoUrl);
            String urlPrs = this.gitBaseUrl + "search/issues?q=type:pr+base:" + branchName + "+repo:"+ repoName +
                    "+created:" + startDate + ".." + endDate + "&sort=created&order=desc";

            JsonArray prArray = this.gitHandlerImplement.getJSONArrayFromGit(urlPrs);
            for (JsonElement prElement : prArray) {
                PullRequest pullRequest = new PullRequest(prElement.getAsJsonObject(), branchName, repoName);
                prFeatureArray.add(pullRequest.getAsJsonObject());
            }
        }
        return prFeatureArray;
    }


    void addBranchVersion(int versionId, String branchName, String repoName) {
        String url = "/branch/add";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("repoName", repoName);
        jsonObject.addProperty("versionId", versionId);
        jsonObject.addProperty("branchName", branchName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_branch_add", jsonObject);
        this.sqlHandler.post(url, sendObject, false);
    }


    void changeBranchVersion(int versionId, int branchId) {
        String url = "/branch/change";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        jsonObject.addProperty("branchId", branchId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_branch_change", jsonObject);
        this.sqlHandler.post(url, sendObject, false);
    }


    void changeVersionName(int versionId, String versionName) {
        String url = "/version/change";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        jsonObject.addProperty("versionName", versionName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_version_change", jsonObject);
        this.sqlHandler.post(url, sendObject, false);
    }


    void deleteVersionName(int versionId) {
        String url = "/version/delete";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_version_delete", jsonObject);
        this.sqlHandler.post(url, sendObject, false);

    }


    JsonArray getQuaterDates() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        JsonArray quarterArray = new JsonArray();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int countQuarters = 0;

        // for last year
        for (int i = 0; i < 4; i++) {
            Calendar quarter = Calendar.getInstance();
            quarter.set(Calendar.YEAR, year - 1);
            quarter.set(Calendar.MONTH, i * 3);
            quarter.set(Calendar.DAY_OF_MONTH, 1);
            String startDate = simpleDateFormat.format(quarter.getTime());
            quarter.set(Calendar.MONTH, i * 3 + 3);
            quarter.add(Calendar.DAY_OF_MONTH, -1);
            String endDate = simpleDateFormat.format(quarter.getTime());

            JsonObject quarterSingleObject = new JsonObject();
            quarterSingleObject.addProperty("quarter", i);
            quarterSingleObject.addProperty("startDate", startDate);
            quarterSingleObject.addProperty("endDate", endDate);
            quarterSingleObject.addProperty("year", year - 1);
            quarterSingleObject.addProperty("id", countQuarters);

            countQuarters++;
            quarterArray.add(quarterSingleObject);

        }

        // for this year
        // number of quarters
        int month = calendar.get(Calendar.MONTH);
        int numberQuarters = month / 3;
        for (int i = 0; i < numberQuarters + 1; i++) {
            Calendar quarter = Calendar.getInstance();
            quarter.set(Calendar.YEAR, year);
            quarter.set(Calendar.MONTH, i * 3);
            quarter.set(Calendar.DAY_OF_MONTH, 1);
            String startDate = simpleDateFormat.format(quarter.getTime());
            quarter.set(Calendar.MONTH, i * 3 + 3);
            quarter.add(Calendar.DAY_OF_MONTH, -1);
            String endDate = simpleDateFormat.format(quarter.getTime());

            JsonObject quarterSingleObject = new JsonObject();
            quarterSingleObject.addProperty("quarter", i);
            quarterSingleObject.addProperty("startDate", startDate);
            quarterSingleObject.addProperty("endDate", endDate);
            quarterSingleObject.addProperty("year", year);
            quarterSingleObject.addProperty("id", countQuarters);

            countQuarters++;
            quarterArray.add(quarterSingleObject);
        }

        return quarterArray;

    }


    private void checkValidResponseAndPopulateArray(JsonElement element, String firstKey, String secondKey, JsonArray array) {
        JsonObject jsonObject = element.getAsJsonObject();
        if (jsonObject.has(firstKey) &&
                jsonObject.get(firstKey).getAsJsonObject().has(secondKey)) {
            JsonArray productJsonArray = jsonObject
                    .get(firstKey).getAsJsonObject().get(secondKey)
                    .getAsJsonArray();
            array.addAll(productJsonArray);
        }
    }


    private String trimJsonElementString(JsonElement text) {
        return text.toString().replace("\"", "");
    }

    private String generateStartDate() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        month = (month / 3) * 3;
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return simpleDateFormat.format(calendar.getTime());
    }


    private String extractRepoName(String repoUrl){
        return repoUrl.split("github.com/")[1];
    }


}
