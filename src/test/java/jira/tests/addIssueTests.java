package jira.tests;

import com.frameworkium.core.api.tests.BaseTest;
import jira.services.IssueService;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class addIssueTests extends BaseTest {

    @Test
    public void getIssueTest() {

        String issueKey = "TEST-2";
        IssueService issueService = IssueService.newInstance(issueKey);
        assertThat(issueService.getUrlOfIssue()).contains(issueService.getIdOfIssue());
    }

}
