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

package org.wso2.ltsdashboard.connectionshandlers;

import org.apache.log4j.Logger;
import org.wso2.ltsdashboard.Constants;


/*
 * Read the properties of the application
 */
public class PropertyReader {

    private final static Logger logger = Logger.getLogger(PropertyReader.class);
    private static String dssUrl;
    private static String gitToken;
    private static String gitBaseUrl;
    private static String accessUsername;
    private static String accessUserPassword;

    public PropertyReader() {
        loadConfigs();
    }


    /**
     * Load configs from the file
     */
    private static void loadConfigs() {
        try {
            gitToken = System.getenv(Constants.GIT_TOKEN);
            logger.info(gitToken);
            dssUrl = System.getenv(Constants.DSS_URL);
            gitBaseUrl = System.getenv(Constants.GIT_BASE_URL);
            accessUsername = System.getenv(Constants.BACKEND_ACCESS_USER);
            accessUserPassword = System.getenv(Constants.BACKEND_ACCESS_PASSWORD);

        } catch (Exception e) {
            logger.error("Failed to load the environmental variables");
        }

    }


    String getGitToken() {
        return gitToken;
    }

    String getDssUrl() {
        return dssUrl;
    }

    public String getGitBaseUrl() {
        return gitBaseUrl;
    }

    public String getAccessUsername() {
        return accessUsername;
    }

    public String getAccessUserPassword() {
        return accessUserPassword;
    }

}
