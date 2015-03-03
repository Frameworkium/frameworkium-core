package com.frameworkium.pages.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class FileUploadSuccessPage extends BasePage<FileUploadSuccessPage> {

    @Visible
    @Name("Uploaded Files")
    @FindBy(css = "div#uploaded-files")
    private WebElement uploadedFiles;

    @Step("Get uploaded files list")
    public String getUploadedFiles() {	
        return uploadedFiles.getText();
    }
   
}
