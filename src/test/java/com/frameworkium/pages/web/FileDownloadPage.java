package com.frameworkium.pages.web;

import static com.jayway.restassured.RestAssured.expect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class FileDownloadPage extends BasePage<FileDownloadPage> {

    @Visible
    @Name("First download link")
    @FindBy(css = "div.example a:first-of-type")
    private Link firstDownloadLink;

    
    @Step("Return the text displayed in the header")
    public int getSizeOfFirstFile() {
    	
    	String downloadURL = firstDownloadLink.getReference();

    	final InputStream inputStream = expect().log().headers().when().get(downloadURL).asInputStream();

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
			IOUtils.copy(inputStream, byteArrayOutputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        IOUtils.closeQuietly(byteArrayOutputStream);
        IOUtils.closeQuietly(inputStream);

        return byteArrayOutputStream.size();
        
    }

   
}
