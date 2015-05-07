package com.google.pages.web;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class ResultsPage extends BasePage<ResultsPage> {

    @Name("Search result titles")
    @Visible
    @FindBy(css = ".rc .r a")
    private List<WebElement> resultTitles;
    
    @Step("Get search result titles")
    public List<String> getResultTitles() {
       List<String> resultTitlesAsString = new ArrayList<String>();
       
       for(WebElement result : resultTitles) {
           resultTitlesAsString.add(result.getText());
       }
       
       return resultTitlesAsString;
    }
    
}
