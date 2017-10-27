package com.frameworkium.integration.ai.capture.api.tests.sutversions;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.ai.capture.api.dto.sut.Versions;
import com.frameworkium.integration.ai.capture.api.service.sut.GetSutVersionsService;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class SoftwareUnderTestVersionsTests extends BaseTest {

    @Test
    public void get_versions(){
        Versions expected = Versions.newFwiumCoreInstance();
        Versions response = new GetSutVersionsService()
                .getVersions("frameworkium-core");
        assertThat(response.versions).containsAllIn(expected.versions);
    }


}
