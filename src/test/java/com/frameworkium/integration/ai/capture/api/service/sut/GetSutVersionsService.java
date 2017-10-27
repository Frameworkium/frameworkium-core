package com.frameworkium.integration.ai.capture.api.service.sut;

import com.frameworkium.integration.ai.capture.api.constant.CaptureEndpoint;
import com.frameworkium.integration.ai.capture.api.dto.sut.Versions;
import com.frameworkium.integration.ai.capture.api.service.BaseCaptureService;

public class GetSutVersionsService extends BaseCaptureService {

    @SuppressWarnings("SameParameterValue")
    public Versions getVersions(String software){
        return get(CaptureEndpoint.SUT_VERSIONS.getUrl(software))
                .extract().as(Versions.class);
    }

}
