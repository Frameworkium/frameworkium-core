package com.frameworkium.integration.tfl.api.dto.carparkoccupancy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bay {
    public String bayType;
    public Integer bayCount;
    public Integer free;
    public Integer occupied;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
