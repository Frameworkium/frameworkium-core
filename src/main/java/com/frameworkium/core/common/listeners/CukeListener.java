package com.frameworkium.core.common.listeners;

import com.frameworkium.core.common.reporting.jira.JiraConfig;
import com.frameworkium.core.common.reporting.jira.zapi.Execution;
import cucumber.runtime.StepDefinitionMatch;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;
import gherkin.formatter.model.Step;
import org.apache.commons.lang.StringUtils;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.config.AllureModelUtils;
import ru.yandex.qatools.allure.events.*;
import ru.yandex.qatools.allure.model.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        scnStepBrokenCount=0;

        //Update Title
        getLifecycle().fire(new UpdateTestCaseTitle(featureInUse.getName() + " | " + scenario.getName()));

        //Update Description
        String description = "";
        if(!featureInUse.getDescription().isEmpty())
            description += "####Feature:" + System.getProperty("line.separator");
        description += featureInUse.getDescription() + System.getProperty("line.separator");
        if(!scenario.getDescription().isEmpty())
            description += "####Scenario:" + System.getProperty("line.separator");
        description += scenario.getDescription();
        if(!description.isEmpty())
            getLifecycle().fire(new AddTestCaseDescriptionEvent(description));

        //Update Labels (eg Feature, Story, TestCaseId, etc)
        List<Label> labels = new ArrayList<Label>();

        //Create feature label
        labels.add(AllureModelUtils.createFeatureLabel(featureInUse.getName()));

        List<Tag> featureAndScenTags = featureInUse.getTags();
        featureAndScenTags.addAll(scenario.getTags());

        //Add feature and scen tags
        labels.addAll(getStoryLabelsFromTags(featureAndScenTags));
        //Add testcaseId ref
        labels.add(AllureModelUtils.createTestLabel(getTestCaseId(scenario.getTags()).split(",")[0]));

        //Commit these labels
        getLifecycle().fire(new AddTestCaseLabelEvent(labels));

        //Update Zephyr with scen's test case = WIP
        if(updateTCMStatus)
            updateTCMStatus(getTestCaseId(scenario.getTags()), JiraConfig.ZapiStatus.ZAPI_STATUS_WIP, "");

    }

    @Override
    public void endOfScenarioLifeCycle(Scenario scenario) {
        //Update Zephyr with scen's test result
        if(updateTCMStatus) {
            if (scnStepBrokenCount > 0) {
                updateTCMStatus(getTestCaseId(scenario.getTags()), JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL, latestError.getLocalizedMessage());
            } else {
                updateTCMStatus(getTestCaseId(scenario.getTags()), JiraConfig.ZapiStatus.ZAPI_STATUS_PASS, "");
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
        if(match.getLocation() != null) {
            StepDefinitionMatch stepDef = (StepDefinitionMatch) match;
            Step step = (Step) getFieldValueInObject(stepDef, "step");
            getLifecycle().fire(new StepStartedEvent(step.getKeyword() + " " + step.getName()));
        }
    }

    @Override
    public void result(Result result) {
        if (result.getStatus() != "undefined" && result.getStatus() != "passed") {
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


    private String getTestCaseId(List<Tag> tags){
        return retrieveTagValue(tags, "TestCaseId");
    }

    private List<Label> getStoryLabelsFromTags(List<Tag> tags){
        List<Label> labels = new ArrayList<Label>();
        List<String> storyAnnotations = new ArrayList<>();

        //Get the story annotations from the feature
        storyAnnotations.addAll(Arrays.asList(retrieveTagValue(tags,"Story").split(",")));
        storyAnnotations.addAll(Arrays.asList(retrieveTagValue(tags,"Stories").split(",")));

        for (String storyAnnotation : storyAnnotations){
            if(!storyAnnotation.isEmpty())
                labels.add(AllureModelUtils.createStoryLabel(storyAnnotation.trim()));
        }
        return labels;
    }

    private String retrieveTagValue(List<Tag> tags, String tagName){
        List<String> values = new ArrayList<>();
        String tagSearch = "@" + tagName + "(";
        for (Tag tag : tags){
            if(tag.getName().startsWith(tagSearch))
                values.add(tag.getName().replace(tagSearch,"").replace(")","").trim());
        }
        //Return a comma separated list
        return StringUtils.join(values,",");
    }

    private void updateTCMStatus(String testCaseId, int status, String comment){
        for(String jiraId : testCaseId.split(",")) {
            if (!jiraId.isEmpty()) {
                comment = "Updated by CustomCukeListener" + System.lineSeparator() + comment;
                new Execution(jiraId).update(status, comment, null);
            }
        }
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
            this.value = new Description().withValue(description).withType(DescriptionType.MARKDOWN);
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
