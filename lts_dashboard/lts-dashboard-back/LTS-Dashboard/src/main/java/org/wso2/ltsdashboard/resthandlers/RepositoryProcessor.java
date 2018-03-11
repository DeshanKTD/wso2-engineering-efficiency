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

package org.wso2.ltsdashboard.resthandlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.wso2.ltsdashboard.connectionshandlers.GitHandlerImplement;
import org.wso2.ltsdashboard.connectionshandlers.PropertyReader;
import org.wso2.ltsdashboard.connectionshandlers.SqlHandler;

import java.util.ArrayList;

/*
 * Containing support methods for repository/ branches call from rest
 */
public class RepositoryProcessor {
    private SqlHandler sqlHandler = null;
    private String gitBaseUrl;
    private GitHandlerImplement gitHandlerImplement;

    public RepositoryProcessor() {
        PropertyReader propertyReader = new PropertyReader();
        this.sqlHandler = new SqlHandler();
        this.gitBaseUrl = propertyReader.getGitBaseUrl();
        this.gitHandlerImplement = new GitHandlerImplement();
    }

    /**
     * Returns repos for selected product
     *
     * @param productId - product name
     * @return json array of repos
     */
    public JsonArray getRepos(int productId) {
        String url = "/product/repos";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("productId", productId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_product_repos", jsonObject);

        JsonElement returnElement = this.sqlHandler.post(url, sendObject);
        JsonArray repoJsonArray = new JsonArray();
        ProcessorCommon.checkValidResponseAndPopulateArray(returnElement, "repositories", "repository", repoJsonArray);

        return repoJsonArray;
    }

    /**
     * The system checks for the branches of current repo in db. The branches in db are tagged with a version
     * There can be new branches crated at git remote. So theses branches also needed to be display for tagging with
     * a version.
     *
     * @param repoUrl  - repo Id
     * @param repoName - repo name
     * @return array of prs with features
     */
    public JsonArray getBranchesForRepo(String repoUrl, String repoName, int repoId) {
        // get version labeled branches from db
        String url = "/repo/branches";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("repoId", repoId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_repo_branches", jsonObject);

        JsonElement returnElement = this.sqlHandler.post(url, sendObject);

        JsonArray branchJsonArray = new JsonArray();
        ProcessorCommon.checkValidResponseAndPopulateArray(returnElement, "branches", "branch", branchJsonArray);
        // get branch names from db
        ArrayList<String> dbBranchNameArray = new ArrayList<>();
        for (JsonElement branch : branchJsonArray) {
            JsonObject branchObject = branch.getAsJsonObject();
            dbBranchNameArray.add(ProcessorCommon.trimJsonElementString(branchObject.get("branchName")));
        }

        String gitRepoName = ProcessorCommon.extractRepoName(repoUrl);


//        logger.debug("Extracting branch data for " + repoName + "from git");
        // get branches from git
        url = gitBaseUrl + "repos/" + gitRepoName + "/branches";
        JsonArray gitBranchArray = this.gitHandlerImplement.getJSONArrayFromGit(url);
        for (JsonElement branch : gitBranchArray) {
            JsonObject branchObject = branch.getAsJsonObject();
            String name = ProcessorCommon.trimJsonElementString(branchObject.get("name"));
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


    public boolean addBranchVersion(int versionId, String branchName, int repoId) {
        String url = "/branch/add";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("repoId", repoId);
        jsonObject.addProperty("versionId", versionId);
        jsonObject.addProperty("branchName", branchName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_branch_add", jsonObject);

        JsonElement requsetSuccess = this.sqlHandler.post(url, sendObject);
        return ProcessorCommon.checkValidNoReplyResponse(requsetSuccess);
    }


    public boolean changeBranchVersion(int versionId, int branchId) {
        String url = "/branch/change";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        jsonObject.addProperty("branchId", branchId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_branch_change", jsonObject);

        JsonElement requsetSuccess = this.sqlHandler.post(url, sendObject);
        return ProcessorCommon.checkValidNoReplyResponse(requsetSuccess);
    }


}
