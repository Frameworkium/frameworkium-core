package com.frameworkium.integration.tfl.api.dto.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisambiguationResultDto {

    private DisambiguationDto toLocationDisambiguation;
    private DisambiguationDto fromLocationDisambiguation;
    private JourneyVectorDto journeyVector;

    public DisambiguationDto getToLocationDisambiguation() {
        return toLocationDisambiguation;
    }

    public void setToLocationDisambiguation(DisambiguationDto toLocationDisambiguation) {
        this.toLocationDisambiguation = toLocationDisambiguation;
    }

    public DisambiguationDto getFromLocationDisambiguation() {
        return fromLocationDisambiguation;
    }

    public void setFromLocationDisambiguation(DisambiguationDto fromLocationDisambiguation) {
        this.fromLocationDisambiguation = fromLocationDisambiguation;
    }

    public JourneyVectorDto getJourneyVector() {
        return journeyVector;
    }

    public void setJourneyVector(JourneyVectorDto journeyVector) {
        this.journeyVector = journeyVector;
    }
}
