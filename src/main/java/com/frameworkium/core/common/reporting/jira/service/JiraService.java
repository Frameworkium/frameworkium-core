package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.zapi.Cycle;
import com.frameworkium.core.common.reporting.jira.zapi.Execution;

public class JiraService {

    public Project project() {
        return new Project();
    }

    public Cycle cycle() {
        return new Cycle();
    }

    public Execution execution() {
        return new Execution();
    }

    public Version version() {
        return new Version();
    }
}
