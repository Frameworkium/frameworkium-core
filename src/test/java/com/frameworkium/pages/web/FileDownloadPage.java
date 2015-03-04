package com.frameworkium.pages.web;

import static com.jayway.restassured.RestAssured.expect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class FileDownloadPage extends BasePage<FileDownloadPage> {

    @Visible
    @Name("Generic download link")
    @FindBy(css = "div.example a")
    private List<Link> allDownloadLinks;

    
    @Visible
    @Name("First download link")
    @FindBy(css = "div.example a:first-of-type")
    private Link firstDownloadLink;

    
    @Step("Get the size of the first downloadable file")
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
    
    @Step("Return all download links")
    public List<String> getListOfDownloadableFiles() {
    	
    	List<String> listOfFiles = new ArrayList<String>();

    	for(Link lnk : allDownloadLinks) {
    		listOfFiles.add(lnk.getText());
    	}
        return listOfFiles;
        
    }

   
}
