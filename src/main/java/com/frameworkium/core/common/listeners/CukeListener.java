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
import ru.yandex.qatools.allure.events.StepFailureEvent;
import ru.yandex.qatools.allure.events.StepFinishedEvent;
import ru.yandex.qatools.allure.events.StepStartedEvent;
import ru.yandex.qatools.allure.events.TestCaseEvent;
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

    //These are just used to enable/disable for debugging - remove once happy with beta
    private Boolean logSteps = true;
    private Boolean logScenarioAsStep = true;
    private Boolean updateLabels = true;
    private Boolean updateTCMStatus = true;

    private Feature featureInUse;
    private int feaStepBrokenCount = 0;
    private int scnStepBrokenCount = 0;
    private Throwable latestError;
    private Boolean hasBackground = false;


    @Override
    public void syntaxError(String s, String s1, List<String> list, String s2, Integer integer) {

    }

    @Override
    public void uri(String s) {

    }

    @Override
    public void feature(Feature feature) {

        feaStepBrokenCount=0;
        featureInUse = feature;

        if(updateTCMStatus)
            updateTCMStatus(getTestCaseId(feature.getTags()), JiraConfig.ZapiStatus.ZAPI_STATUS_WIP, "");
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

        if (logScenarioAsStep) {
            if (!getTestCaseId(scenario.getTags()).isEmpty()) {
                getLifecycle().fire(new StepStartedEvent("[SCN (" + getTestCaseId(scenario.getTags()) + ")] " + scenario.getName()));
            } else {
                getLifecycle().fire(new StepStartedEvent("[SCN] " + scenario.getName()));
            }
        }

        getLifecycle().fire(new UpdateTestCaseTitle(featureInUse.getName()));

        if(!featureInUse.getDescription().isEmpty())
            getLifecycle().fire(new AddTestCaseDescriptionEvent(featureInUse.getDescription()));

        if(updateLabels) {
            List<Label> labels = new ArrayList<Label>();

            labels.add(AllureModelUtils.createFeatureLabel(featureInUse.getName()));

            List<Tag> featureAndScenTags = featureInUse.getTags();
            featureAndScenTags.addAll(scenario.getTags());
            labels.addAll(getStoryLabelsFromTags(featureAndScenTags));
            //Only use the first testcaseid tag - otherwise we'll append the scenario ones too
            labels.add(AllureModelUtils.createTestLabel(getTestCaseId(featureInUse.getTags()).split(",")[0]));

            getLifecycle().fire(new AddTestCaseLabelEvent(labels));


        }

        if(updateTCMStatus)
            updateTCMStatus(getTestCaseId(scenario.getTags()), JiraConfig.ZapiStatus.ZAPI_STATUS_WIP, "");

    }

    @Override
    public void background(Background background) {
        hasBackground = true;
        getLifecycle().fire(new StepStartedEvent("[BACKGROUND]"));
    }

    @Override
    public void scenario(Scenario scenario) {
        if (hasBackground) {
            //End the background step
            getLifecycle().fire(new StepFinishedEvent());
        }
    }

    @Override
    public void step(Step step) {

    }

    @Override
    public void endOfScenarioLifeCycle(Scenario scenario) {

        if (logScenarioAsStep){
            if (scnStepBrokenCount > 0) {
                getLifecycle().fire(new StepFailureEvent().withThrowable(latestError));
            }
            getLifecycle().fire(new StepFinishedEvent());
        }

        if(updateTCMStatus) {
            if (scnStepBrokenCount > 0) {
                updateTCMStatus(getTestCaseId(scenario.getTags()), JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL, latestError.getLocalizedMessage());
            } else {
                updateTCMStatus(getTestCaseId(scenario.getTags()), JiraConfig.ZapiStatus.ZAPI_STATUS_PASS, "");
            }
        }
    }

    @Override
    public void done() {

        if(updateTCMStatus) {
            if (feaStepBrokenCount > 0) {
                updateTCMStatus(getTestCaseId(featureInUse.getTags()), JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL, latestError.getLocalizedMessage());
            } else {
                updateTCMStatus(getTestCaseId(featureInUse.getTags()), JiraConfig.ZapiStatus.ZAPI_STATUS_PASS, "");
            }
        }
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
    public void result(Result result) {
        if (result.getStatus() != "undefined") {

            if (result.getStatus() != "passed") {
                scnStepBrokenCount++;
                feaStepBrokenCount++;
                latestError = result.getError();
            }

            if (logSteps) {
                if (result.getStatus() != "passed") {
                    getLifecycle().fire(new StepFailureEvent().withThrowable(result.getError()));
                }
                getLifecycle().fire(new StepFinishedEvent());
            }
        }
    }

    @Override
    public void after(Match match, Result result) {

    }

    @Override
    public void match(Match match) {
        if (logSteps) {
            if(match.getLocation() != null) {
                StepDefinitionMatch stepDef = (StepDefinitionMatch) match;
                Step step = (Step) getFieldValueInObject(stepDef, "step");
                getLifecycle().fire(new StepStartedEvent(step.getKeyword() + " " + step.getName()));
            }
        }
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


}
