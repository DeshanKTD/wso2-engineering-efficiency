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

package org.wso2.deployer.ms4jresources.prs;

import org.apache.log4j.Logger;
import org.wso2.deployer.msf4jhttp.HttpHandler;
import org.wso2.deployer.msf4jhttp.RequestHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * Servelet to get features
 */
public class FeatureGetter extends HttpServlet {
    private static final Logger logger = Logger.getLogger(FeatureGetter.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        HttpHandler httpHandler = new HttpHandler();
        String requestBody = RequestHelper.getRequestBody(request);
        String backResponse = httpHandler.post("/features", requestBody);
        response.setCharacterEncoding("UTF-8");

        try {
            PrintWriter out = response.getWriter();
            out.print(backResponse);
        } catch (IOException e) {
            logger.error("Error while writing to out");
        }
    }
}
