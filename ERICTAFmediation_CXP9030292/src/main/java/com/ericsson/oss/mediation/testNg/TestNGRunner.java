/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.mediation.testNg;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;

public class TestNGRunner {

    public static void main(final String[] args) {
        // Retrieve the suite file from the jar and then write it out to
        // filesystem for use with testNG
        final InputStream is = TestNGRunner.class.getResourceAsStream("/suite.xml");
        final File mysuite = new File("testng.xml");
        // when the tests are finished delete the file just created from the
        // filesystem
        mysuite.deleteOnExit();
        try {
            final OutputStream os = new FileOutputStream(mysuite);
            final byte buf[] = new byte[2048];
            int len;
            try {
                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
                os.close();
                is.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        final List<String> xmlFileList = new ArrayList<String>();
        xmlFileList.add("testng.xml");
        final TestNG testng = new TestNG();
        testng.setTestSuites(xmlFileList);
        testng.run();
        System.exit(0);
    }

}
