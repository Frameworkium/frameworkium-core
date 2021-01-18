package com.frameworkium.core.common.reporting.jira.service;

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
