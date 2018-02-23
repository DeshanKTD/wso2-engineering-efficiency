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
 * Interface of processor class
 */

import com.google.gson.JsonArray;

public interface Processor {

    /**
     * Get the product list
     *
     * @return ArrayList of Product names
     */

    JsonArray getProductList();

    /**
     * Get the labels for particular product
     *
     * @param productName -product name
     * @return - json array of Label names
     */
    JsonArray getVersions(String productName);

    /**
     * Get the repository list for a product
     *
     * @param productName - product name
     * @return - json array of repolist
     */
    JsonArray getReposForProduct(String productName);

    /**
     * get branches for a product
     *
     * @param productName - product name
     * @return - json array of branches
     */
    JsonArray getBranchesForProduct(String productName);


    /**
     * Add product version to the db
     * @param productName - product name
     * @param version - version name
     * @return
     */
    boolean addProductVersion(String productName, String version);

    /**
     * map branch to a version
     * @param productName - product name
     * @param version - version name
     * @param repo - repository
     * @param branchName - branch name
     * @return
     */
    boolean addBranchToVersion(String productName, String version,String repo, String branchName);

    /**
     * change the version of the branch
     *
     * @param productName - prduct name
     * @param repo - repo name
     * @param version - version name
     * @param branchName - branch name
     * @return
     */
    boolean updateBranchToVersion(String productName,String version,String repo, String branchName);



    /**
     * Get PRs for give product name and label
     *
     * @param productName - Product name that map to database
     * @param version       - label extracted
     * @return a json array of issue details
     */

    // return all features with pr list
    JsonArray getPrList(String productName, String version);

}
