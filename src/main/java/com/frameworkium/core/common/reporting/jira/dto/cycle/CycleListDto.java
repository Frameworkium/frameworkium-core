package com.frameworkium.core.common.reporting.jira.dto.cycle;

import com.fasterxml.jackson.annotation.*;
import com.frameworkium.core.api.dto.AbstractDTO;

import java.util.HashMap;
import java.util.Map;

public class CycleListDto extends AbstractDTO<CycleListDto> {
    public Map<String, CycleDto> map = new HashMap<>();
    public Long recordsCount;

    @JsonCreator
    public CycleListDto(@JsonProperty("recordsCount") Long recordsCount) {
        this.recordsCount = recordsCount;
    }

    @JsonAnySetter
    public void setMap(String key, CycleDto cycleDto) {
        map.put(key, cycleDto);
    }

    @JsonAnyGetter
    public Map<String, CycleDto> getMap() {
        return map;
    }
}
