package com.frameworkium.core.common.listeners;

import com.frameworkium.core.common.reporting.jira.zapi.Execution;
import cucumber.runtime.StepDefinitionMatch;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;
import gherkin.formatter.model.Step;
import org.apache.commons.lang3.StringUtils;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.config.AllureModelUtils;
import ru.yandex.qatools.allure.events.*;
import ru.yandex.qatools.allure.model.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.frameworkium.core.common.reporting.jira.JiraConfig.ZapiStatus.*;

public class CukeListener implements Formatter, Reporter {

    private Allure lifecycle = Allure.LIFECYCLE;
    private Boolean updateTCMStatus = true;
    private Feature featureInUse;
    private int scnStepBrokenCount = 0;
    private Throwable latestError;

    @Override
    public void syntaxError(String s, String s1, List<String> list, String s2, Integer integer) {
    }

    @Override
    public void uri(String s) {
    }

    @Override
    public void feature(Feature feature) {
        featureInUse = feature;
    }

    @Override
    public void scenario(Scenario scenario) {
    }

    @Override
    public void scenarioOutline(ScenarioOutline scenarioOutline) {
    }

    @Override
    public void examples(Examples examples) {
    }

    @Override
    public void startOfScenarioLifeCycle(Scenario scenario) {
        scnStepBrokenCount = 0;

        //Update Title
        final String title = featureInUse.getName() + " | " + scenario.getName();
        lifecycle.fire(new UpdateTestCaseTitle(title));

        //Update Description
        final String description = buildFeatureAndScenarioDescription(scenario);
        if (!description.isEmpty()) {
            lifecycle.fire(new AddTestCaseDescriptionEvent(description));
        }

        //Commit labels
        lifecycle.fire(new AddTestCaseLabelEvent(getLabels(scenario)));

        //Update Zephyr with scen's test case = WIP
        if (updateTCMStatus) {
            updateTCMStatus(getTestCaseId(scenario), ZAPI_STATUS_WIP, "");
        }
    }

    private String buildFeatureAndScenarioDescription(Scenario scenario) {
        StringBuilder descriptionBuilder = new StringBuilder();
        final String featureDescription = featureInUse.getDescription();
        if (!featureDescription.isEmpty()) {
            descriptionBuilder.append("####Feature:")
                    .append(System.lineSeparator())
                    .append(featureDescription)
                    .append(System.lineSeparator());
        }
        final String scenarioDescription = scenario.getDescription();
        if (!scenarioDescription.isEmpty()) {
            descriptionBuilder.append("####Scenario:")
                    .append(System.lineSeparator())
                    .append(scenarioDescription);
        }
        return descriptionBuilder.toString();
    }

    private List<Label> getLabels(Scenario scenario) {
        //Update Labels (eg Feature, Story, TestCaseId, etc)
        List<Label> labels = new ArrayList<>();

        //Create feature label
        labels.add(AllureModelUtils.createFeatureLabel(featureInUse.getName()));

        //Add feature and scenario tags
        List<Tag> featureAndScenarioTags = Stream
                .concat(featureInUse.getTags().stream(),
                        scenario.getTags().stream())
                .distinct()
                .collect(Collectors.toList());

        labels.addAll(getStoryLabelsFromTags(featureAndScenarioTags));

        //Add testcaseId ref
        final String testKey = getTestCaseId(scenario.getTags()).split(",")[0];
        labels.add(AllureModelUtils.createTestLabel(testKey));
        return labels;
    }

    private List<String> getTestCaseId(Scenario scenario) {
        return retrieveTagStream(scenario.getTags(), "TestCaseId")
                .collect(Collectors.toList());
    }

    private void updateTCMStatus(List<String> testCaseIds, int status, String comment) {
        final String updatedComment = "Updated by CustomCukeListener\n" + comment;
        testCaseIds.stream()
                .filter(StringUtils::isNotBlank)
                .map(Execution::new)
                .forEach(ex -> ex.update(status, updatedComment));
    }

    @Override
    public void endOfScenarioLifeCycle(Scenario scenario) {
        //Update Zephyr with scen's test result
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
    public void background(Background background) {
    }

    @Override
    public void step(Step step) {
    }

    @Override
    public void done() {
    }

    @Override
    public void close() {
    }

    @Override
    public void eof() {
    }

    @Override
    public void before(Match match, Result result) {
    }

    @Override
    public void after(Match match, Result result) {
    }

    @Override
    public void match(Match match) {
        if (match.getLocation() != null && scnStepBrokenCount == 0) {
            StepDefinitionMatch stepDef = (StepDefinitionMatch) match;
            Step step = (Step) getFieldValueInObject(stepDef, "step");
            lifecycle.fire(new StepStartedEvent(
                    step.getKeyword() + " " + step.getName()));
        }
    }

    @Override
    public void result(Result result) {
        if (resultIsNotSkipped(result)) {
            if (resultIsBroken(result)) {
                scnStepBrokenCount++;
                latestError = result.getError();
                lifecycle.fire(new StepFailureEvent().withThrowable(latestError));
            }
            lifecycle.fire(new StepFinishedEvent());
        }
    }

    private boolean resultIsNotSkipped(Result result) {
        final String status = result.getStatus();
        return !"skipped".equals(status);
    }

    private boolean resultIsBroken(Result result) {
        final String status = result.getStatus();
        return !"undefined".equals(status) && !"passed".equals(status);
    }

    @Override
    public void embedding(String s, byte[] bytes) {
    }

    @Override
    public void write(String s) {
    }

    private <T> Object getFieldValueInObject(T m, String field) {
        try {
            Field fieldInObject = getFieldInObject(m, field);
            fieldInObject.setAccessible(true);
            return fieldInObject.get(m);
        } catch (Exception e) {
            return null;
        }
    }

    private <T> Field getFieldInObject(T m, String field) throws NoSuchFieldException {
        return m.getClass().getDeclaredField(field);
    }

    private String getTestCaseId(List<Tag> tags) {
        return retrieveTagValue(tags, "TestCaseId");
    }

    private List<Label> getStoryLabelsFromTags(List<Tag> tags) {
        //Get the story annotations from the feature
        return Stream
                .concat(retrieveTagStream(tags, "Story"),
                        retrieveTagStream(tags, "Stories"))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .map(AllureModelUtils::createStoryLabel)
                .collect(Collectors.toList());
    }

    private Stream<String> retrieveTagStream(List<Tag> tags, String tagName) {
        final String tagSearch = "@" + tagName + "(";
        return tags.stream()
                .map(Tag::getName)
                .filter(name -> name.startsWith(tagSearch))
                .map(name -> name
                        .replace(tagSearch, "")
                        .replace(")", "")
                        .trim());
    }

    private String retrieveTagValue(List<Tag> tags, String tagName) {

        return retrieveTagStream(tags, tagName)
                .collect(Collectors.joining(","));
    }

    public class UpdateTestCaseTitle implements TestCaseEvent {
        String title;

        public UpdateTestCaseTitle(String title) {
            this.title = title;
        }

        @Override
        public void process(TestCaseResult context) {
            context.setTitle(title);
        }
    }

    public class AddTestCaseDescriptionEvent implements TestCaseEvent {
        Description description;

        public AddTestCaseDescriptionEvent(String description) {
            this.description = new Description()
                    .withValue(description)
                    .withType(DescriptionType.MARKDOWN);
        }

        @Override
        public void process(TestCaseResult context) {
            context.setDescription(description);
        }
    }

    public class AddTestCaseLabelEvent implements TestCaseEvent {
        List<Label> labelsToAdd;

        public AddTestCaseLabelEvent(List<Label> labels) {
            this.labelsToAdd = labels;
        }

        @Override
        public void process(TestCaseResult context) {
            context.getLabels().addAll(labelsToAdd);
        }
    }

    public class TestFailedEvent implements TestCaseEvent {
        Status status;

        public TestFailedEvent() {
            this.status = Status.FAILED;
        }

        @Override
        public void process(TestCaseResult context) {
            context.setStatus(status);
            context.setStop(System.currentTimeMillis());
        }
    }
}
