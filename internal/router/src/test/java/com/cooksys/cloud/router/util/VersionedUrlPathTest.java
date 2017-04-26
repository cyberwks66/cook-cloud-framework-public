package com.cooksys.cloud.router.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by timd on 3/7/17.
 */
public class VersionedUrlPathTest {
    @Test
    public void testValidPaths() throws Exception {

        VersionedUrlPath versionedUrlPath = VersionedUrlPath.valueOf("/_/1/2/3/_/some/path");
        assertTrue(versionedUrlPath.getVersion().getMajorVersion()==1);
        assertTrue(versionedUrlPath.getVersion().getMinorVersion()==2);
        assertTrue(versionedUrlPath.getVersion().getPatchVersion()==3);
        assertTrue("/some/path".equals(versionedUrlPath.getPathRemainder()));

        versionedUrlPath = VersionedUrlPath.valueOf("/_/1/2/3/_/some/path/");
        assertTrue(versionedUrlPath.getVersion().getMajorVersion()==1);
        assertTrue(versionedUrlPath.getVersion().getMinorVersion()==2);
        assertTrue(versionedUrlPath.getVersion().getPatchVersion()==3);
        assertTrue("/some/path".equals(versionedUrlPath.getPathRemainder()));

        versionedUrlPath = VersionedUrlPath.valueOf("/_/1/2/3-SNAPSHOT/_/some/path");
        assertTrue(versionedUrlPath.getVersion().getMajorVersion()==1);
        assertTrue(versionedUrlPath.getVersion().getMinorVersion()==2);
        assertTrue(versionedUrlPath.getVersion().getPatchVersion()==3);
        assertTrue("SNAPSHOT".equals(versionedUrlPath.getVersion().getPreReleaseVersion()));
        assertTrue("/some/path".equals(versionedUrlPath.getPathRemainder()));

        versionedUrlPath = VersionedUrlPath.valueOf("/_/1/2/3/_");
        assertTrue(versionedUrlPath.getVersion().getMajorVersion()==1);
        assertTrue(versionedUrlPath.getVersion().getMinorVersion()==2);
        assertTrue(versionedUrlPath.getVersion().getPatchVersion()==3);
        assertTrue("".equals(versionedUrlPath.getPathRemainder()));

        versionedUrlPath = VersionedUrlPath.valueOf("/_/1/2/3/_/");
        assertTrue(versionedUrlPath.getVersion().getMajorVersion()==1);
        assertTrue(versionedUrlPath.getVersion().getMinorVersion()==2);
        assertTrue(versionedUrlPath.getVersion().getPatchVersion()==3);
        assertTrue("".equals(versionedUrlPath.getPathRemainder()));
    }

    @Test
    public void invalidPaths() throws Exception {
        boolean exceptionThrown=false;

        try {
            VersionedUrlPath versionedUrlPath = VersionedUrlPath.valueOf("/_/A/2/3/_/some/path");
        } catch (IllegalArgumentException e) {
            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
        exceptionThrown=false;

        try {
            VersionedUrlPath versionedUrlPath = VersionedUrlPath.valueOf("/some/path");
        } catch (IllegalArgumentException e) {
            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
        exceptionThrown=false;

        try {
            VersionedUrlPath versionedUrlPath = VersionedUrlPath.valueOf("/_/1/A/3/_/some/path");
        } catch (IllegalArgumentException e) {
            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
        exceptionThrown=false;

        try {
            VersionedUrlPath versionedUrlPath = VersionedUrlPath.valueOf("/_/1/2/A-SNAPSHOT/_/some/path");
        } catch (IllegalArgumentException e) {
            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
        exceptionThrown=false;

        try {
            VersionedUrlPath versionedUrlPath = VersionedUrlPath.valueOf("/");
        } catch (IllegalArgumentException e) {
            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
        exceptionThrown=false;

        exceptionThrown=false;

        try {
            VersionedUrlPath versionedUrlPath = VersionedUrlPath.valueOf("/_///_/");
        } catch (IllegalArgumentException e) {
            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
        exceptionThrown=false;
    }
}