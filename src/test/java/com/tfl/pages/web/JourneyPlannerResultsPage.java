package com.tfl.pages.web;

import java.util.List;

//import org.joda.time.Period;
//import org.joda.time.format.PeriodFormatter;
//import org.joda.time.format.PeriodFormatterBuilder;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;
import com.tfl.pages.web.components.NavBar;


public class JourneyPlannerResultsPage extends BasePage<JourneyPlannerResultsPage> {

    @Name("NavBar")
    @Visible
    private NavBar navBar;

    @FindBy(css="div#loader-window")
    private WebElement journeyPlannerLoading;
    
    
    @Name("Disambiguation Options - From")
    @FindBy(css="ol#disambiguation-options-from li.disambiguation-option a")
    private List<WebElement> disambiguationOptionsFrom;
    
    @Name("Disambiguation Options - To")
    @FindBy(css="ol#disambiguation-options-to li.disambiguation-option a")
    private List<WebElement> disambiguationOptionsTo;
    
    @Name("Results Summary Divs")
    @FindBy(css="div.summary-results")
    private List<WebElement> resultsSummaries;

    @Name("Journey Times")
    @FindBy(css="div.journey-time")
    private List<WebElement> journeyTimes;
    
    public NavBar theNavBar() {
        return navBar;
    }

    
    public JourneyPlannerResultsPage disambiguateFromAndTo() {
        
        if (!disambiguationOptionsFrom.isEmpty()) {
            disambiguationOptionsFrom.get(0).click();            
        }
        
        if (!disambiguationOptionsTo.isEmpty()) {
            disambiguationOptionsTo.get(0).click();            
            
        }
        
        wait.until(ExpectedConditions.visibilityOfAllElements(resultsSummaries));
    
        return this;
    }
    
    
    public int getShortestJourneyTimeInMinutes() {
    
//        List<Period> journeyTimesAsStringList = new ArrayList<Period>();
//        for (WebElement journeyTime : journeyTimes)        
//        {
//           
//            PeriodFormatter formatter = new PeriodFormatterBuilder()
//            .appendHours().appendSuffix("hours ")
//            .appendMinutes().appendSuffix("mins")
//            .toFormatter();
//
//            Period p = formatter.parsePeriod(journeyTime.getText());
//          
//            journeyTimesAsStringList.add(p);
//            System.out.println(p.getMinutes());
//        }
//        
//        return journeyTimesAsStringList.get(0).getMinutes();
        return 1;
    }
    
    
  
}
