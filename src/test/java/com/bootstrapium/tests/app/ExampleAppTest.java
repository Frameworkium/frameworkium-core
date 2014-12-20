package com.bootstrapium.tests.app;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.bootstrapium.tests.internal.BaseTest;

public class ExampleAppTest extends BaseTest{

	/**
	 * https://appium.s3.amazonaws.com/TestApp7.1.app.zip
	 * @throws Exception
	 */
	@Test
    public void testIOSApp() throws Exception {
		WebDriver driver = getDriver();
        List<WebElement> fields = driver.findElements(By.className("UIATextField"));
        Integer sum = 0;
        for (WebElement field : fields) {
            Integer num = randInt(1, 100);
            sum += num;
            field.sendKeys(num.toString());
        }
        driver.findElement(By.className("UIAButton")).click();
        String result = driver.findElement(By.className("UIAStaticText")).getText();

        assertThat(result).comparesEqualTo(sum.toString());
    }

    public static Integer randInt(int min, int max) {
        Random rand = new Random();
        Integer randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
