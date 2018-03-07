/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
import org.wso2.ltsdashboard.resthandlers.PrProcessor;
import org.wso2.ltsdashboard.resthandlers.ProcessorCommon;
import org.wso2.ltsdashboard.resthandlers.ProductProcessor;
import org.wso2.ltsdashboard.resthandlers.RepositoryProcessor;
import org.wso2.ltsdashboard.resthandlers.VersionProcessor;

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
        logger.debug("Request to get product names");
        ProductProcessor productProcessor = new ProductProcessor();
        JsonArray productList = productProcessor.getProductList();

        return makeResponseWithBody(productList);
    }

    @GET
    @Path("/release/quarters")
    public Response getQuarters() {
        logger.debug("Request to get quarters from current quarter");
        ProcessorCommon processorCommon = new ProcessorCommon();
        JsonArray productList = processorCommon.getQuaterDates();

        return makeResponseWithBody(productList);
    }


    @POST
    @Path("/products/versions")
    public Response getVersions(JsonObject product) {
        logger.debug("Request to versions");
        VersionProcessor versionProcessor = new VersionProcessor();
        int productId = product.get("productId").getAsInt();
        JsonArray productList = versionProcessor.getVersions(productId);

        return makeResponseWithBody(productList);
    }


    @POST
    @Path("/products/repos")
    public Response getRepository(JsonObject product) {
        logger.debug("Request to repos for a product");
        RepositoryProcessor repositoryProcessor = new RepositoryProcessor();
        int productId = product.get("productId").getAsInt();
        JsonArray repoList = repositoryProcessor.getRepos(productId);

        return makeResponseWithBody(repoList);
    }


    @POST
    @Path("/products/repos/branches")
    public Response getRepositoryBranches(JsonObject repoData) {
        logger.debug("Request to get branches for a repo");
        RepositoryProcessor repositoryProcessor = new RepositoryProcessor();
        String repoUrl = repoData.get("repoUrl").getAsString();
        String repoName = repoData.get("repoName").getAsString();
        int repoId = repoData.get("repoId").getAsInt();
        JsonArray branchList = repositoryProcessor.getBranchesForRepo(repoUrl, repoName,repoId);

        return makeResponseWithBody(branchList);
    }


    @POST
    @Path("/products/versions/add")
    public Response addVersion(JsonObject versionData) {
        Response response;
        logger.debug("Request to add a version for a product");
        VersionProcessor versionProcessor = new VersionProcessor();
        int productId = versionData.get("productId").getAsInt();
        String versionName = versionData.get("versionName").getAsString();
        boolean stat = versionProcessor.addVersion(productId, versionName);

        if(stat){
            response = makeResponseWithBody(new JsonArray());
        }
        else {
            response = makeResponseWithBadBody();
        }
        return response;
    }


    @POST
    @Path("/products/versionChange")
    public Response changeVersion(JsonObject versionData) {
        Response response;
        logger.debug("Request to change the version name of a product");
        VersionProcessor versionProcessor = new VersionProcessor();
        int versionId = versionData.get("versionId").getAsInt();
        String versionName = versionData.get("versionName").getAsString();
        boolean stat = versionProcessor.changeVersionName(versionId, versionName);

        if(stat){
            response = makeResponseWithBody(new JsonArray());
        }
        else {
            response = makeResponseWithBadBody();
        }
        return response;
    }


    @POST
    @Path("/products/deleteVersion")
    public Response deleteVersion(JsonObject versionData) {
        Response response;
        logger.debug("Request to delete the version of a product");
        VersionProcessor versionProcessor = new VersionProcessor();
        int versionId = versionData.get("versionId").getAsInt();
        boolean stat =versionProcessor.deleteVersionName(versionId);

        if(stat){
            response = makeResponseWithBody(new JsonArray());
        }
        else {
            response = makeResponseWithBadBody();
        }
        return response;
    }


    @POST
    @Path("/branches/versions/add")
    public Response addBranchVersion(JsonObject versionData) {
        Response response;
        logger.debug("Request to add version to a branch");
        RepositoryProcessor repositoryProcessor = new RepositoryProcessor();
        int versionId = versionData.get("versionId").getAsInt();
        String branchName = versionData.get("branchName").getAsString();
        int repoId = versionData.get("repoId").getAsInt();
        boolean stat = repositoryProcessor.addBranchVersion(versionId, branchName, repoId);

        if(stat){
            response = makeResponseWithBody(new JsonArray());
        }
        else {
            response = makeResponseWithBadBody();
        }
        return response;
    }


    @POST
    @Path("/branches/changeVersion")
    public Response changeBranchVersion(JsonObject versionData) {
        Response response;
        logger.debug("Request to change branch version");
        RepositoryProcessor repositoryProcessor = new RepositoryProcessor();
        int versionId = versionData.get("versionId").getAsInt();
        int branchId = versionData.get("branchId").getAsInt();
        boolean stat = repositoryProcessor.changeBranchVersion(versionId, branchId);

        if(stat){
            response = makeResponseWithBody(new JsonArray());
        }
        else {
            response = makeResponseWithBadBody();
        }
        return response;
    }


    @POST
    @Path("/features")
    public Response postIssues(JsonObject versionData) {
        logger.debug("Request to get Prs for the quarter");
        int versionId = versionData.get("versionId").getAsInt();
        String startDate = versionData.get("startDate").getAsString();
        String endDate = versionData.get("endDate").getAsString();
        PrProcessor prProcessor = new PrProcessor();
        JsonArray featureList = prProcessor.getPrsForVersion(versionId,startDate,endDate);

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

    private Response makeResponseWithBadBody() {
        return Response.serverError()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "POST, GET, PUT,UPDATE, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                .build();
    }


}
