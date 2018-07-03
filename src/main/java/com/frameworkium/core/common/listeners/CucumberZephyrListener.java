package com.frameworkium.core.common.listeners;

import com.frameworkium.core.common.reporting.jira.zapi.Execution;
import cucumber.runtime.CucumberException;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.frameworkium.core.common.reporting.jira.JiraConfig.ZapiStatus.*;

public class CucumberZephyrListener implements Formatter, Reporter {

    private Boolean updateTCMStatus = true;
    private int scnStepBrokenCount = 0;
    private Throwable latestError;

    @Override
    public void syntaxError(String s, String s1, List<String> list, String s2, Integer integer) {}

    @Override
    public void uri(String s) {}

    @Override
    public void feature(Feature feature) {}

    @Override
    public void scenario(Scenario scenario) {}

    @Override
    public void scenarioOutline(ScenarioOutline scenarioOutline) {}

    @Override
    public void examples(Examples examples) {}

    @Override
    public void startOfScenarioLifeCycle(Scenario scenario) {
        scnStepBrokenCount = 0;

        //Update Zephyr with scen's test case = WIP
        if (updateTCMStatus) {
            updateTCMStatus(getTestCaseId(scenario), ZAPI_STATUS_WIP, "");
        }
    }


    private List<String> getTestCaseId(Scenario scenario) {
        return retrieveTagStream(scenario.getTags(), "TestCaseId")
                .collect(Collectors.toList());
    }

    private void updateTCMStatus(List<String> testCaseIds, int status, String comment) {
        final String updatedComment = "Updated by Cucumber Zephyr Listener\n" + comment;
        testCaseIds.stream()
                .filter(StringUtils::isNotBlank)
                .map(Execution::new)
                .forEach(ex -> ex.update(status, updatedComment));
    }

    @Override
    public void endOfScenarioLifeCycle(Scenario scenario) {
        // Update Zephyr with scen's test result
        if (updateTCMStatus) {
            final List<String> testCaseIds = getTestCaseId(scenario);
            if (scnStepBrokenCount > 0) {
                updateTCMStatus(testCaseIds, ZAPI_STATUS_FAIL, latestError.getLocalizedMessage());
            } else {
                updateTCMStatus(testCaseIds, ZAPI_STATUS_PASS, "");
            }
        }
    }

    @Override
    public void background(Background background) {}

    @Override
    public void step(Step step) {}

    @Override
    public void done() {}

    @Override
    public void close() {}

    @Override
    public void eof() {}

    @Override
    public void before(Match match, Result result) {}

    @Override
    public void after(Match match, Result result) {}

    @Override
    public void match(Match match) {}

    @Override
    public void result(Result result) {
        String status = result.getStatus();
        if (isNotSkipped(status) && resultIsBroken(status)) {
            scnStepBrokenCount++;
            latestError = result.getError();
            if (latestError instanceof CucumberException) {
                latestError = latestError.getCause();
            }
        }
    }

    private boolean isNotSkipped(String status) {
        return "skipped".equals(status);
    }

    private boolean resultIsBroken(String status) {
        return "undefined".equals(status)
                || "passed".equals(status);
    }

    @Override
    public void embedding(String s, byte[] bytes) {}

    @Override
    public void write(String s) {}

    private Stream<String> retrieveTagStream(List<Tag> tags, String tagName) {
        final String tagSearch = "@" + tagName + "(";
        return tags.stream()
                .map(Tag::getName)
                .filter(name -> name.startsWith(tagSearch))
                .map(name -> name
                        .replace(tagSearch, "")
                        .replace(")", "")
                        .trim()
                        .replaceAll("^\"|\"$", ""));
    }

}
