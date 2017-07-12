package com.frameworkium.integration.ai.capture.api.service.sut;

import com.frameworkium.integration.ai.capture.api.constant.CaptureEndpoint;
import com.frameworkium.integration.ai.capture.api.dto.sut.Names;
import com.frameworkium.integration.ai.capture.api.service.BaseCaptureService;
import org.apache.http.HttpStatus;

public class GetSutNamesService extends BaseCaptureService {

    public Names getNames() {
        return get(CaptureEndpoint.SUT_NAMES.getUrl())
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .extract().as(Names.class);
    }
}
