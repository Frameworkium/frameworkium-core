[![Build Status](https://drone.io/github.com/jvanderwee/bootstrapium/status.png)](https://drone.io/github.com/jvanderwee/bootstrapium/latest)

Frameworkium
=======================

This project is based on Ardesco's [Selenium-Maven-Template](https://github.com/Ardesco/Selenium-Maven-Template) and Joe VDW's [Bootstrapium](https://github.com/joevanderwee/bootstrapium) with some handy added extras for getting started quickly with Selenium and Appium.

Open the ``` frameworkium ``` directory in a terminal window/command prompt and run ``` mvn clean verify ``` to run the example tests using Firefox.

### Stuff you can do

Want to run the tests on a different browser? No problem, just provide the 'browser' argument:

```bash
mvn clean verify -Dbrowser=chrome 
```

All you need to do is ensure the browser is installed in the default location.

Running mobile web tests using Appium on Sauce Labs is only slightly more involved:

```bash
export SAUCE_USERNAME=<username>
export SAUCE_ACCESS_KEY=<access_key>
mvn clean verify -Dplatform=ios -Dbrowser=safari -Dsauce=true 
```

A full list of arguments can be found on the [project wiki](https://github.com/robertgates55/frameworkium/wiki).

After running your tests, you can generate an [Allure](http://allure.qatools.ru) test report by simply running:

```bash
mvn site 
```

Frameworkium sets you up for other stuff too - check out the [project wiki](https://github.com/robertgates55/frameworkium/wiki) for further info.
