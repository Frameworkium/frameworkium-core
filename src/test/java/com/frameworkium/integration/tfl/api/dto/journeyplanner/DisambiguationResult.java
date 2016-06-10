package com.frameworkium.integration.tfl.api.dto.journeyplanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.yandex.qatools.allure.annotations.Step;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisambiguationResult {

    public Disambiguation toLocationDisambiguation;
    public Disambiguation fromLocationDisambiguation;
    public JourneyVector journeyVector;

    @Step
    public String getFirstDisambiguatedTo() {
        return toLocationDisambiguation.disambiguationOptions.get(0).parameterValue;
    }
}
