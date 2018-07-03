package com.frameworkium.core.api.dto;

import java.util.Arrays;
import java.util.List;

public class TopLevelDTO extends AbstractDTO<TopLevelDTO> {
    LowLevelDTO lowLevelDTO = new LowLevelDTO();
    List<String> stringList = Arrays.asList("1", "a");
}
