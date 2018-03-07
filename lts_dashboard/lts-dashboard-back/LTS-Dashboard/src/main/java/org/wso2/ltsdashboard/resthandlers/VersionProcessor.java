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
import org.wso2.ltsdashboard.connectionshandlers.SqlHandler;

/*
 * Containing support methods for version call from rest
 */
public class VersionProcessor {
    private SqlHandler sqlHandler = null;

    public VersionProcessor() {
        this.sqlHandler = new SqlHandler();
    }

    /**
     * Get the versions for particular product
     *
     * @param productId - repo name
     * @return - json array of Label names
     */
    public JsonArray getVersions(int productId) {
        String url = "/product/version";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("productId", productId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_product_version", jsonObject);

        JsonElement returnElement = this.sqlHandler.post(url, sendObject);
        JsonArray versionJsonArray = new JsonArray();
        ProcessorCommon.checkValidResponseAndPopulateArray(returnElement, "versions", "version", versionJsonArray);

        return versionJsonArray;
    }


    public boolean addVersion(int productId, String versionName) {
        String url = "/version/add";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("productId", productId);
        jsonObject.addProperty("versionName", versionName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_version_add", jsonObject);

        JsonElement requestSuccess = this.sqlHandler.post(url, sendObject);
        return ProcessorCommon.checkValidNoReplyResponse(requestSuccess);
    }


    public boolean changeVersionName(int versionId, String versionName) {
        String url = "/version/change";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        jsonObject.addProperty("versionName", versionName);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_version_change", jsonObject);

        JsonElement returnSuccess = this.sqlHandler.post(url, sendObject);
        return ProcessorCommon.checkValidNoReplyResponse(returnSuccess);
    }

    public boolean deleteVersionName(int versionId) {
        String url = "/version/delete";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionId", versionId);
        JsonObject sendObject = sqlHandler.createPostDataObject("_post_version_delete", jsonObject);

        JsonElement requestSuccess = this.sqlHandler.post(url, sendObject);
        return ProcessorCommon.checkValidNoReplyResponse(requestSuccess);
    }

    public static void main(String[] args) {
        VersionProcessor versionProcessor = new VersionProcessor();
        versionProcessor.deleteVersionName(18);
    }


}
