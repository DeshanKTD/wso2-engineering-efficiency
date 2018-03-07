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
import org.wso2.ltsdashboard.gitobjects.PullRequest;

/*
 * Containing support methods for pr call from rest
 */
public class PrProcessor {
    private SqlHandler sqlHandler;
    private String gitBaseUrl;
    private GitHandlerImplement gitHandlerImplement;

    public PrProcessor() {
        this.sqlHandler = new SqlHandler();
        PropertyReader propertyReader = new PropertyReader();
        this.gitBaseUrl = propertyReader.getGitBaseUrl();
        this.gitHandlerImplement = new GitHandlerImplement();

    }

    /**
     * Get All features for product version
     *
     * @param versionId - version id to extract features
     * @return Feature Set as a json array
     */
    public JsonArray getPrsForVersion(int versionId, String startDate, String endDate) {
        JsonArray prFeatureArray = new JsonArray();
        // get branch name repo name
        String url = "/product/repobranches";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_product_repobranches", jsonObject);
        JsonElement returnElement = this.sqlHandler.post(url, sendObject);
        JsonArray repoBranchJsonArray = new JsonArray();
        ProcessorCommon.checkValidResponseAndPopulateArray(returnElement, "versionDetails", "versionDetail", repoBranchJsonArray);

        // getting event lists from issue list
        for (JsonElement branchData : repoBranchJsonArray) {
            JsonObject branchObject = branchData.getAsJsonObject();
            String branchName = ProcessorCommon.trimJsonElementString(branchObject.get("branchName"));
            String repoUrl = ProcessorCommon.trimJsonElementString(branchObject.get("repoUrl"));
            String repoName = ProcessorCommon.extractRepoName(repoUrl);
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
}
