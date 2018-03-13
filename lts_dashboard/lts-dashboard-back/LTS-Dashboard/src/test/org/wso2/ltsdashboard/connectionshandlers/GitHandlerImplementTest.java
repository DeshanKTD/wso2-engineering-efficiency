package org.wso2.ltsdashboard.connectionshandlers;

import com.google.gson.JsonArray;
import junit.framework.TestCase;
import org.junit.Assert;

/*
 * TODO - comment class work
 */public class GitHandlerImplementTest extends TestCase {
    public void testGetJSONArrayFromGit() throws Exception {
        GitHandlerImplement gitHandlerImplement = new GitHandlerImplement();
        JsonArray arrayFromGit = gitHandlerImplement
                .getJSONArrayFromGit("https://api.github.com/repos/ballerina-lang/ballerina/pulls");
        Assert.assertNotNull(arrayFromGit);
    }

}