package com.frameworkium.core;


import com.frameworkium.core.common.reporting.jira.zapi.*;
import com.google.inject.Module;
import com.google.inject.*;



public class JiraModule implements Module {
    @Override
    public void configure(final Binder binder) {
    }

    @Provides
    ExecutionExtraFactory getExecutionExtraFactory() {
        return new ExecutionExtraFactory(Attachment::new, Execution::new);
    }
}