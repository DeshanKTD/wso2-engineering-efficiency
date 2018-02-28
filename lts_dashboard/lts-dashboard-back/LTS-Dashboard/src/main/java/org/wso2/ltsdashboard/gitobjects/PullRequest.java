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

package org.wso2.ltsdashboard.gitobjects;
/*
 * The data about a PR
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PullRequest {
    private String[] features;
    private String author;
    private boolean validMarketingMessage;
    private String url;
    private String featureName;
    private JsonArray labels;
    private String repoName;
    private String branchName;


    public PullRequest(JsonObject prObject,String branchName,String repoName) {
        this.author = this.trimJsonElementString(prObject.get("user").getAsJsonObject().get("login"));
        this.url = this.trimJsonElementString(prObject.get("html_url"));
        this.featureName = this.trimJsonElementString(prObject.get("title"));
        this.features = this.createFeatures(trimJsonElementString(prObject.get("body")));
        this.validMarketingMessage = this.checkMarketingMessageISValid(trimJsonElementString(prObject.get("body")));
        this.labels = this.createLabelArray(prObject.get("labels").getAsJsonArray());
        this.repoName = repoName;
        this.branchName = branchName;
    }


    private String[] createFeatures(String body) {
        String[] features = null;
        String featurePart = null;
        if (body.contains("## Marketing")) {
            String[] bodyParts = body.split("## Marketing");
            featurePart = bodyParts[1].split("##")[0];
        }

        if (featurePart != null) {
            features = formatFeatureText(featurePart);
        }
        return features;
    }

    private String[] formatFeatureText(String featureText) {
        String text = featureText.replace("\\r\\n", "%%%%");
        return text.split("%%%%");
    }


    private boolean checkMarketingMessageISValid(String body) {
        boolean valid = true;
        String marketingMessage = null;
        if (body.contains("## Marketing")) {
            String[] bodyParts = body.split("## Marketing");
            marketingMessage = bodyParts[1].split("##")[0];
        }
        if (marketingMessage == null) {
            valid = false;
        } else if (marketingMessage.toLowerCase()
                .contains("drafts of marketing content that will describe and promote this feature")) {
            valid = false;
        } else if (marketingMessage.length() < 50) {
            valid = false;
        }
        return valid;
    }

    private JsonArray getFeatureList() {
        JsonArray jsonArray = new JsonArray();
        if (this.features != null) {
            for (String feature : this.features) {
                if (!feature.equals("") && !feature.equals("N/A")) {
                    jsonArray.add(feature);
                }
            }
        }
        return jsonArray;
    }

    private JsonArray createLabelArray(JsonArray labels){
        JsonArray labelArray = new JsonArray();
        for(JsonElement element:labels){
            labelArray.add(element.getAsJsonObject().get("name"));
        }

        return labelArray;
    }



    public JsonObject getAsJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user", this.author);
        jsonObject.addProperty("title", this.featureName);
        jsonObject.add("features", this.getFeatureList());
        jsonObject.addProperty("validMarketing", this.validMarketingMessage);
        jsonObject.addProperty("url", this.url);
        jsonObject.add("labels",this.labels);
        jsonObject.addProperty("repoName",this.repoName);
        jsonObject.addProperty("branch",this.branchName);

        return jsonObject;

    }


    private String trimJsonElementString(JsonElement text) {
        return text.toString().replace("\"", "");
    }


}
