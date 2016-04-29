package jira.services;

import com.frameworkium.core.api.annotations.DeserialiseAs;
import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import com.jayway.restassured.specification.RequestSpecification;
import jira.entities.Issue;
import ru.yandex.qatools.allure.annotations.Step;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by robertgates55 on 26/04/2016.
 */
public class IssueService extends BaseService<IssueService> {


    @DeserialiseAs
    private Issue issue;

    public static IssueService newInstance(String issueId){
        RequestSpecification reqSpec = given().auth().preemptive().basic("frameworkium","frameworkium");
        return ServiceFactory.newInstance(IssueService.class, String.format("http://52.29.130.45:8080/rest/api/latest/issue/%s",issueId), reqSpec);
    }

    @Step
    public String getUrlOfIssue(){
        return issue.self;
    }

    @Step
    public String getIdOfIssue(){
        return issue.id;
    }

}
