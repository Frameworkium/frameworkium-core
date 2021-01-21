package com.frameworkium.core.common.listeners;

import com.beust.jcommander.ParameterException;
import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.dto.cycle.*;
import com.frameworkium.core.common.reporting.jira.dto.execution.AddTestToCycleOperationDto;
import com.frameworkium.core.common.reporting.jira.dto.project.ProjectDto;
import com.frameworkium.core.common.reporting.jira.dto.version.VersionDto;
import com.frameworkium.core.common.reporting.jira.service.Project;
import com.frameworkium.core.common.reporting.jira.service.Version;
import com.frameworkium.core.common.reporting.jira.zapi.Cycle;
import com.frameworkium.core.common.reporting.jira.zapi.Execution;
import io.qameta.allure.TmsLink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NotFoundException;
import org.testng.*;
import org.testng.internal.ConstructorOrMethod;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.String.format;


public class CreateJiraCycleListener implements ISuiteListener {
    private final Logger logger = LogManager.getLogger(CreateJiraCycleListener.class);
    private final Project project = new Project();
    private final Version version = new Version();
    private final Cycle cycle = new Cycle();
    private final Execution execution = new Execution();


    @Override
    public void onStart(ISuite suite) {
        if (!hasResultVersionSpecified() || !hasZapiRegexCycleSpecified()) {
            logger.info("ResultVersion and/or ZapiCycleRegex was not set. "
                    + "Skipping automated creation of JIRA version and Zephyr cycle as it is not needed...");
            return;
        }
        final String targetProjectKey = getProjectKey(suite);
        final List<String> jiraTestLinksInSuite = getAllTestLinksInSuite(suite);
        final ProjectDto targetProjectDto = project.getProject(targetProjectKey);
        createExecutionCyclesInJiraIfNeeded(targetProjectDto, jiraTestLinksInSuite);
    }

    @Override
    public void onFinish(ISuite suite) {
        logger.debug("CreateJiraCycleListener finish. Nothing left to do");
    }

    private boolean hasResultVersionSpecified() {
        return Property.JIRA_URL.isSpecified()
                && Property.RESULT_VERSION.isSpecified();
    }

    private boolean hasZapiRegexCycleSpecified() {
        return Property.JIRA_URL.isSpecified()
                && Property.ZAPI_CYCLE_REGEX.isSpecified();
    }

    private String getProjectKey(ISuite suite) {
        final String targetProjectKey = suite.getParameter("project-key");
        if (targetProjectKey == null) {
            throw new ParameterException("Missing JIRA project-key for CreateJiraCycleListener. "
                    + "Try setting project-key parameter in your testng.xml file e.g.\n"
                    + "https://testng.org/doc/documentation-main.html#testng-listeners\n"
                    + "\n"
                    + "    <listener class-name=\"CreateJiraCycleListener\">\n"
                    + "      <parameter name=\"project-key\" value=\"MYPROJECTKEY\"/>\n"
                    + "    </listener>"
                    + "\n");
        }
        logger.info(() -> format("Targeting JIRA project with key: %s", targetProjectKey));
        return targetProjectKey;
    }

    private List<String> getAllTestLinksInSuite(ISuite suite) {
        return suite.getAllMethods().stream()
                .map(ITestNGMethod::getConstructorOrMethod)
                .map(ConstructorOrMethod::getMethod)
                .flatMap(method -> Arrays.stream(method.getAnnotationsByType(TmsLink.class)))
                .filter(Objects::nonNull)
                .map(TmsLink::value)
                .distinct()
                .collect(Collectors.toList());
    }

    private void createExecutionCyclesInJiraIfNeeded(ProjectDto targetProjectDto, List<String> testLinks) {
        final VersionDto targetVersionDto = createResultVersionIfNoneExist(targetProjectDto);
        final Entry<String, CycleDto> targetCycleEntry = createZapiCycleIfNoneExist(targetProjectDto, targetVersionDto);
        createExecutionsIfNoneExist(targetProjectDto, targetVersionDto, targetCycleEntry, testLinks);
    }

    private VersionDto createResultVersionIfNoneExist(ProjectDto targetProject) {
        final List<VersionDto> projectVersions = project.getProjectVersions(targetProject.key);

        final Optional<VersionDto> targetVersion = projectVersions.stream()
                .filter(versionDto -> versionDto.name.equals(Property.RESULT_VERSION.getValue()))
                .findFirst();
        targetVersion.ifPresent(foundVersion ->
                logger.info(() -> format("Found in Zephyr; [ resultVersion: %s ] with [ resultVersionId: %s ]",
                        Property.RESULT_VERSION.getValue(), foundVersion.id)));
        return targetVersion.orElseGet(() -> {
            final VersionDto createdVersion = version
                    .createVersion(VersionDto.newBuilder()
                            .projectId(targetProject.id)
                            .name(Property.RESULT_VERSION.getValue())
                            .build());
            logger.info(() -> format("Created in Zephyr; [ resultVersion: %s ] with [ resultVersionId: %s ]",
                    Property.RESULT_VERSION.getValue(), createdVersion.id));
            return createdVersion;
        });
    }

    private Entry<String, CycleDto> createZapiCycleIfNoneExist(
            ProjectDto targetProjectDto, VersionDto targetVersionDto) {

        final Optional<Entry<String, CycleDto>> targetCycleEntry =
                getListOfCycles(targetProjectDto.id, targetVersionDto.id);
        targetCycleEntry.ifPresent(cycleDtoEntry ->
                logger.info(() -> format("Found in Zephyr; [ cycle: %s ] with [ cycleId: %s ]",
                        Property.ZAPI_CYCLE_REGEX.getValue(), cycleDtoEntry.getKey())));
        return targetCycleEntry.orElseGet(() -> {
            final String createdCycleId = cycle.createNewCycle(
                    CreateNewCycleDto.newBuilder().cycleLightDto(
                            CycleLightDto.newBuilder()
                                    .versionId(targetVersionDto.id)
                                    .projectId(targetProjectDto.id)
                                    .name(Property.ZAPI_CYCLE_REGEX.getValue())
                                    .build())
                            .build()).id;
            logger.info(() -> format("Created in Zephyr; [ cycle: %s ] with [ cycleId: %s ]",
                    Property.ZAPI_CYCLE_REGEX.getValue(), createdCycleId));

            return getListOfCycles(targetProjectDto.id, targetVersionDto.id)
                    .orElseThrow(() -> new NotFoundException(format("Unable to find in JIRA newly created"
                                    + " [ cycle: %s ] with [ cycleId: %s ]. Cycle creation may have failed",
                            Property.ZAPI_CYCLE_REGEX.getValue(), createdCycleId)));
        });
    }

    private Optional<Entry<String, CycleDto>> getListOfCycles(
            Long targetProjectId, Long targetVersionId) {
        Predicate<Entry<String, CycleDto>> hasTargetVersion = stringCycleInformationDtoEntry
                -> stringCycleInformationDtoEntry.getValue().versionName.equals(Property.RESULT_VERSION.getValue());
        Predicate<Entry<String, CycleDto>> hasTargetCycle = stringCycleInformationDtoEntry
                -> stringCycleInformationDtoEntry.getValue()
                .cycleLightDto.name.equals(Property.ZAPI_CYCLE_REGEX.getValue());
        return cycle
                .getListOfCycle(targetProjectId, targetVersionId).map.entrySet().stream()
                .filter(hasTargetVersion)
                .filter(hasTargetCycle)
                .findFirst();
    }

    private void createExecutionsIfNoneExist(
            ProjectDto targetProjectDto, VersionDto targetVersionDto,
            Entry<String, CycleDto> targetCycleEntry, List<String> testLinks) {
        Long totalCycleExecutions = targetCycleEntry.getValue().totalCycleExecutions;

        logger.info(() -> format("Preexisting executions (%d total) found under "
                        + "[ version: %s, cycle: %s ]. Opting to NOT add further executions.",
                totalCycleExecutions,
                targetVersionDto.name,
                targetCycleEntry.getValue().cycleLightDto.name));
        if (totalCycleExecutions != 0) {
            return;
        }

        execution.addTestsToCycle(
                AddTestToCycleOperationDto.newBuilder()
                        .cycleId(targetCycleEntry.getKey())
                        .projectId(targetProjectDto.id)
                        .versionId(targetVersionDto.id)
                        .issues(testLinks)
                        .method("1")
                        .build());

        logger.info(() -> format("Added execution run in JIRA under [ version: %s, cycleName: %s, cycleId: %s ].\n"
                        + "  List of tests added into execution: \n"
                        + "  %s",
                targetVersionDto.name,
                targetCycleEntry.getValue().cycleLightDto.name,
                targetCycleEntry.getKey(),
                testLinks));
    }
}
