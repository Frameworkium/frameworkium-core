Bootstrapium
=======================

This project is based on Ardesco's [Selenium-Maven-Template](https://github.com/Ardesco/Selenium-Maven-Template) with some handy added extras for getting started quickly with Selenium and Appium.

Get started in four easy steps:

1. Open a terminal window/command prompt
2. ``` git clone https://github.com/jvanderwee/bootstrapium.git ```  
3. ``` cd bootstrapium ```
4. ``` mvn clean verify ```

All dependencies will be downloaded and the example Selenium test will run (assuming you have Firefox installed in the default location!).

### Stuff you can do

Want to run your tests on a different browser? No problem, just provide the 'browser' argument:

```bash
mvn clean verify -Dbrowser=chrome 
```

Running your mobile web tests using Appium on Sauce Labs is only slightly more involved:

```bash
mvn clean verify -DsauceUser={username} -DsauceKey={accessKey} -Dplatform=ios -Dbrowser=safari -DplatformVersion=8.1 -DdeviceName="iPhone Simulator" 
```

A full list of arguments can be found on the [project wiki](https://github.com/jvanderwee/bootstrapium/wiki).

After running your tests, you can generate an [Allure](http://allure.qatools.ru) test report by simply running:

```bash
mvn site 
```

Bootstrapium does other stuff too - check out the [project wiki](https://github.com/jvanderwee/bootstrapium/wiki) for further info.
