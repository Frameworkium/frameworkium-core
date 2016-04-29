package tfl.entities.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by robertgates55 on 26/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItineraryResult {
    public Journey[] journeys;
}

