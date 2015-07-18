package com.tfl.pages.web;

//import org.joda.time.Period;
//import org.joda.time.format.PeriodFormatter;
//import org.joda.time.format.PeriodFormatterBuilder;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;


public class JourneyPlannerResultsPage extends BasePage<JourneyPlannerResultsPage> {

    @Name("Page Title Area")
    @Visible
    @FindBy(css="h1 span.hero-headline")
    private WebElement pageTitleArea;
   
    public String getTitleText() {
        
      return pageTitleArea.getText();
    }
    
    
  
}
