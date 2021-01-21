package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.zapi.Cycle;
import com.frameworkium.core.common.reporting.jira.zapi.Execution2;

public class JiraService {

    public Project project() {
        return new Project();
    }

    public Cycle cycle() {
        return new Cycle();
    }

    public Execution2 execution() {
        return new Execution2();
    }

    public Version version() {
        return new Version();
    }
}
