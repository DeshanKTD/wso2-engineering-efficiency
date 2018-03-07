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

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Contains common methods for other Processor classes
 */
public class ProcessorCommon {

    public static void checkValidResponseAndPopulateArray(JsonElement element, String firstKey, String secondKey, JsonArray array) {
        JsonObject jsonObject = element.getAsJsonObject();
        if (jsonObject.has(firstKey) &&
                jsonObject.get(firstKey).getAsJsonObject().has(secondKey)) {
            JsonArray productJsonArray = jsonObject
                    .get(firstKey).getAsJsonObject().get(secondKey)
                    .getAsJsonArray();
            array.addAll(productJsonArray);
        }
    }


    static boolean checkValidNoReplyResponse(JsonElement reply){
        boolean returnBool = false;
        JsonObject requestSuccess = reply.getAsJsonObject();
        if(requestSuccess.getAsJsonObject()
                .get("UpdatedRowCount")
                .getAsJsonObject().get("Value")
                .getAsInt()>0) {
            returnBool = true;
        }

        return returnBool;
    }


    static String trimJsonElementString(JsonElement text) {
        return text.toString().replace("\"", "");
    }

    static String extractRepoName(String repoUrl){
        return repoUrl.split("github.com/")[1];
    }


    public JsonArray getQuaterDates() {        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        JsonArray quarterArray = new JsonArray();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int countQuarters = 0;

        // for last year
        for (int i = 0; i < 4; i++) {
            Calendar quarter = Calendar.getInstance();
            quarter.set(Calendar.YEAR, year - 1);
            quarter.set(Calendar.MONTH, i * 3);
            quarter.set(Calendar.DAY_OF_MONTH, 1);
            String startDate = simpleDateFormat.format(quarter.getTime());
            quarter.set(Calendar.MONTH, i * 3 + 3);
            quarter.add(Calendar.DAY_OF_MONTH, -1);
            String endDate = simpleDateFormat.format(quarter.getTime());

            JsonObject quarterSingleObject = new JsonObject();
            quarterSingleObject.addProperty("quarter", i);
            quarterSingleObject.addProperty("startDate", startDate);
            quarterSingleObject.addProperty("endDate", endDate);
            quarterSingleObject.addProperty("year", year - 1);
            quarterSingleObject.addProperty("id", countQuarters);

            countQuarters++;
            quarterArray.add(quarterSingleObject);

        }

        // for this year
        // number of quarters
        int month = calendar.get(Calendar.MONTH);
        int numberQuarters = month / 3;
        for (int i = 0; i < numberQuarters + 1; i++) {
            Calendar quarter = Calendar.getInstance();
            quarter.set(Calendar.YEAR, year);
            quarter.set(Calendar.MONTH, i * 3);
            quarter.set(Calendar.DAY_OF_MONTH, 1);
            String startDate = simpleDateFormat.format(quarter.getTime());
            quarter.set(Calendar.MONTH, i * 3 + 3);
            quarter.add(Calendar.DAY_OF_MONTH, -1);
            String endDate = simpleDateFormat.format(quarter.getTime());

            JsonObject quarterSingleObject = new JsonObject();
            quarterSingleObject.addProperty("quarter", i);
            quarterSingleObject.addProperty("startDate", startDate);
            quarterSingleObject.addProperty("endDate", endDate);
            quarterSingleObject.addProperty("year", year);
            quarterSingleObject.addProperty("id", countQuarters);

            countQuarters++;
            quarterArray.add(quarterSingleObject);
        }

        return quarterArray;

    }
}
