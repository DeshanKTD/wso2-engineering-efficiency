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

package org.wso2.deployer.msf4jhttp;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * Read the properties of the application
 */
public class PropertyReader {

    private final static Logger logger = Logger.getLogger(PropertyReader.class);
    private final static String CONFIG_FILE = "config.properties";
    private String backendUrl;

    public PropertyReader() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
        loadConfigs(inputStream);
    }


    /**
     * Load configs from the file
     *
     * @param input - input stream of the file
     */
    private void loadConfigs(InputStream input) {
        Properties prop = new Properties();
        try {
            prop.load(input);
            this.backendUrl = prop.getProperty("backend_url");


        } catch (FileNotFoundException e) {
            logger.error("The configuration file is not found");
        } catch (IOException e) {
            logger.error("The File cannot be read");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("The File InputStream is not closed");
                }
            }
        }

    }

    public String getBackendUrl() {
        return this.backendUrl;
    }

    ;

}