/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.ltsdashboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


/**
 * This is the Microservice resource class.
 * See <a href="https://github.com/wso2/msf4j#getting-started">https://github.com/wso2/msf4j#getting-started</a>
 * for the usage of annotations.
 *
 * @since 1.0-SNAPSHOT
 */

@Path("/lts")
public class LtsDashboard {
    private final static Logger logger = Logger.getLogger(LtsDashboard.class);

    LtsDashboard() {
    }

    @GET
    @Path("/products/names")
    public Response getProducts() {
        logger.debug("Request to products");
        ProcessorImplement processorImplement = new ProcessorImplement();
        JsonArray productList = processorImplement.getProductList();

        return makeResponseWithBody(productList);
    }


    @POST
    @Path("/products/versions")
    public Response getLabels(JsonObject product) {
        logger.debug("Request to versions");
        ProcessorImplement processorImplement = new ProcessorImplement();
        int productName = product.get("productId").getAsInt();
        JsonArray productList = processorImplement.getVersions(productName);

        return makeResponseWithBody(productList);
    }



    @POST
    @Path("/products/repos")
    public Response getRepository(JsonObject product) {
        logger.debug("Request to versions");
        ProcessorImplement processorImplement = new ProcessorImplement();
        int productId = product.get("productId").getAsInt();
        JsonArray repoList = processorImplement.getRepos(productId);

        return makeResponseWithBody(repoList);
    }


    @POST
    @Path("/products/repos/branches")
    public Response getRepositoryBranches(JsonObject repoData) {
        logger.debug("Request to versions");
        ProcessorImplement processorImplement = new ProcessorImplement();
        int repoId = repoData.get("repoId").getAsInt();
        String repoName = repoData.get("repoName").getAsString();
        JsonArray branchList = processorImplement.getBranchesForRepo(repoId,repoName);

        return makeResponseWithBody(branchList);
    }


    @POST
    @Path("/products/versions/add")
    public Response addVersion(JsonObject versionData) {
        logger.debug("Request to versions");
        ProcessorImplement processorImplement = new ProcessorImplement();
        int productId = versionData.get("productId").getAsInt();
        String versionName = versionData.get("versionName").getAsString();
        processorImplement.addVersion(productId,versionName);


        return makeResponseWithBody(new JsonArray());
    }


    @POST
    @Path("/products/versionChange")
    public Response changeVersion(JsonObject versionData) {
        ProcessorImplement processorImplement = new ProcessorImplement();
        int versionId = versionData.get("versionId").getAsInt();
        String versionName = versionData.get("versionName").getAsString();
        processorImplement.changeVersionName(versionId,versionName);


        return makeResponseWithBody(new JsonArray());
    }


    @POST
    @Path("/products/deleteVersion")
    public Response deleteVersion(JsonObject versionData) {
        ProcessorImplement processorImplement = new ProcessorImplement();
        int versionId = versionData.get("versionId").getAsInt();
        processorImplement.deleteVersionName(versionId);


        return makeResponseWithBody(new JsonArray());
    }


    @POST
    @Path("/branches/versions/add")
    public Response addBranchVersion(JsonObject versionData) {
        logger.debug("Request to versions");
        ProcessorImplement processorImplement = new ProcessorImplement();
        int versionId = versionData.get("versionId").getAsInt();
        String branchName = versionData.get("branchName").getAsString();
        int repoId = versionData.get("repoId").getAsInt();
        processorImplement.addBranchVersion(versionId,branchName,repoId);

        return makeResponseWithBody(new JsonArray());
    }


    @POST
    @Path("/branches/changeVersion")
    public Response changeBranchVersion(JsonObject versionData) {
        logger.debug("Request to versions");
        ProcessorImplement processorImplement = new ProcessorImplement();
        int versionId = versionData.get("versionId").getAsInt();
        int branchId = versionData.get("branchId").getAsInt();
        processorImplement.changeBranchVersion(versionId, branchId);

        return makeResponseWithBody(new JsonArray());
    }


    @POST
    @Path("/features")
    @Consumes("application/json")
    public Response postIssues(JsonArray issueList) {
        logger.debug("Request to features");
        ProcessorImplement processorImplement = new ProcessorImplement();
        JsonArray featureList = processorImplement.getAllFeatures(issueList);

        return makeResponseWithBody(featureList);
    }


    @OPTIONS
    @Path("/products/versions")
    public Response versionOptions() {
        return makeResponse();
    }


    @OPTIONS
    @Path("/products/repos")
    public Response getReposOptions() {
        return makeResponse();
    }

    @OPTIONS
    @Path("/products/repos/branches")
    public Response getReposBranchesOptions() {
        return makeResponse();
    }


    @OPTIONS
    @Path("/products/versions/add")
    public Response versionAddOptions() {
        return makeResponse();
    }

    @OPTIONS
    @Path("/products/deleteVersion")
    public Response versionDeleteOptions() {
        return makeResponse();
    }

    @OPTIONS
    @Path("/products/versionChange")
    public Response versionChangeOptions() {
        return makeResponse();
    }

    @OPTIONS
    @Path("/branches/versions/add")
    public Response branchVersionAddOptions() {
        return makeResponse();
    }


    @OPTIONS
    @Path("/branches/changeVersion")
    public Response branchVersionChangeOptions() {
        return makeResponse();
    }


    @OPTIONS
    @Path("/products")
    public Response productsOptions() {
        return makeResponse();
    }


    @OPTIONS
    @Path("/issues")
    public Response issuesOptions() {
        return makeResponse();
    }

    @OPTIONS
    @Path("/milestone")
    public Response milestoneOptions() {
        return makeResponse();
    }

    @OPTIONS
    @Path("/features")
    public Response featureOptions() {
        return makeResponse();
    }

    private Response makeResponse() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "POST, GET, PUT,UPDATE, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                .build();
    }


    private Response makeResponseWithBody(Object sendingObject) {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "POST, GET, PUT,UPDATE, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                .entity(sendingObject)
                .build();
    }


}
