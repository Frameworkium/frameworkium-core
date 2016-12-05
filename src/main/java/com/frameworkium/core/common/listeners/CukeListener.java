package com.frameworkium.core.common.listeners;

import com.frameworkium.core.common.reporting.jira.zapi.Execution;
import cucumber.runtime.StepDefinitionMatch;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;
import gherkin.formatter.model.Step;
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

    public Allure getLifecycle() {
        return lifecycle;
    }

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
        getLifecycle().fire(new UpdateTestCaseTitle(title));

        //Update Description
        String description = "";
        if (!featureInUse.getDescription().isEmpty())
            description += "####Feature:" + System.lineSeparator();
        description += featureInUse.getDescription() + System.lineSeparator();
        if (!scenario.getDescription().isEmpty())
            description += "####Scenario:" + System.lineSeparator();
        description += scenario.getDescription();
        if (!description.isEmpty())
            getLifecycle().fire(new AddTestCaseDescriptionEvent(description));

        //Commit labels
        getLifecycle().fire(new AddTestCaseLabelEvent(getLabels(scenario)));

        //Update Zephyr with scen's test case = WIP
        if (updateTCMStatus) {
            updateTCMStatus(getTestCaseId(scenario), ZAPI_STATUS_WIP, "");
        }
    }

    private List<Label> getLabels(Scenario scenario) {
        //Update Labels (eg Feature, Story, TestCaseId, etc)
        List<Label> labels = new ArrayList<>();

        //Create feature label
        labels.add(AllureModelUtils.createFeatureLabel(featureInUse.getName()));

        List<Tag> featureAndScenarioTags = Stream
                .concat(featureInUse.getTags().stream(),
                        scenario.getTags().stream())
                .distinct()
                .collect(Collectors.toList());

        //Add feature and scenario tags
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
        for (String jiraId : testCaseIds) {
            if (!jiraId.isEmpty()) {
                comment = "Updated by CustomCukeListener" + System.lineSeparator() + comment;
                new Execution(jiraId).update(status, comment);
            }
        }
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
        if (match.getLocation() != null) {
            StepDefinitionMatch stepDef = (StepDefinitionMatch) match;
            Step step = (Step) getFieldValueInObject(stepDef, "step");
            getLifecycle().fire(new StepStartedEvent(
                    step.getKeyword() + " " + step.getName()));
        }
    }

    @Override
    public void result(Result result) {
        final String status = result.getStatus();
        if (!"undefined".equals(status) && !"passed".equals(status)) {
            scnStepBrokenCount++;
            latestError = result.getError();
            getLifecycle().fire(new StepFailureEvent().withThrowable(latestError));
        }
        getLifecycle().fire(new StepFinishedEvent());
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
                .filter(storyAnnotation -> !storyAnnotation.isEmpty())
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
        Description value;

        public AddTestCaseDescriptionEvent(String description) {
            this.value = new Description()
                    .withValue(description)
                    .withType(DescriptionType.MARKDOWN);
        }

        @Override
        public void process(TestCaseResult context) {
            context.setDescription(value);
        }
    }

    public class AddTestCaseLabelEvent implements TestCaseEvent {
        List<Label> labelsToAdd;

        public AddTestCaseLabelEvent(List<Label> labels) {
            this.labelsToAdd = labels;
        }

        @Override
        public void process(TestCaseResult context) {
            List<Label> labels = context.getLabels();
            labels.addAll(labelsToAdd);
            context.setLabels(labels);
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
