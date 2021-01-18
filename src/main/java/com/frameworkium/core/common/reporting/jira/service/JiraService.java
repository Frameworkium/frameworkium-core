package com.frameworkium.core.common.reporting.jira.service;

public class JiraService {

    public Project project() {
        return new Project();
    }

    public CycleService cycle() {
        return new CycleService();
    }

    public ExecutionService execution() {
        return new ExecutionService();
    }

    public VersionService version() {
        return new VersionService();
    }
}
