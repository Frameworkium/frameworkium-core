package com.frameworkium.core

import com.frameworkium.core.common.reporting.jira.zapi.Attachment
import com.frameworkium.core.common.reporting.jira.zapi.Execution
import com.frameworkium.core.common.reporting.jira.zapi.ExecutionExtraFactory
import com.google.inject.AbstractModule
import com.google.inject.Provides
import spock.mock.DetachedMockFactory


class JiraMockModule extends AbstractModule {
    @Override
    void configure() {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()
        bind(Execution).toInstance(detachedMockFactory.Mock(Execution))
        bind(Attachment).toInstance(detachedMockFactory.Stub(Attachment))
    }


//    @Provides
//    Attachment getAttachment() {
//        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()
//        return detachedMockFactory.Mock(Attachment)
//    }
//
//    @Provides
//    Execution getExecution() {
//        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()
//        return detachedMockFactory.Mock(Execution)
//    }

//    @Provides
//    ExecutionExtraFactory getExecutionExtraFactory() {
//        return new ExecutionExtraFactory(() -> getAttachment(), () -> getExecution());
//    }
}
