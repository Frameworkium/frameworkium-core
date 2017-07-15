package com.frameworkium.integration.ai.capture.api.dto.sut;


import com.frameworkium.integration.ai.capture.api.dto.executions.BaseDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Versions extends BaseDTO <Versions> {

    public List<String> versions;

    @SuppressWarnings("WeakerAccess")
    public static List<String> preExistingFwiumCoreVersions = Arrays.asList("master", "selenium-3.0", "2.3.0",
            "cucumber-jvm-parallel-plugin", "grid-extras-video-capture", "custom_browser_impl",
            "2.3.1", "2.3.2", "2.3.3", "electron-v1", "feature/test-build", "2.3.4",
            "selenium-3.3-upgrade", "cuke-testid-fix", "appium-upversion", "2.4",
            "extra-video-exception", "ff-gecko-ver-update", "parallel-ff-gird-test");

    public static Versions newFwiumCoreInstance(){
        Versions versions = new Versions();
        versions.versions = new ArrayList<>();
        versions.versions.addAll(preExistingFwiumCoreVersions);
        return versions;
    }

}
