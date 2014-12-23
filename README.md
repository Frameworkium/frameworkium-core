[![Build Status](https://drone.io/github.com/jvanderwee/bootstrapium/status.png)](https://drone.io/github.com/jvanderwee/bootstrapium/latest)

Bootstrapium
=======================

This project is based on Ardesco's [Selenium-Maven-Template](https://github.com/Ardesco/Selenium-Maven-Template) with some handy added extras for getting started quickly with Selenium and Appium.

The easiest way to get starting is by using [bootstrapium-vagrant](https://github.com/jvanderwee/bootstrapium-vagrant) to create a VM with Bootstrapium and its dependencies pre-installed.

If you do not wish to use Vagrant then clone this project and ensure PhantomJS and chrome-driver are on your path.

Open the ``` bootstrapium ``` directory in a terminal window/command prompt and run ``` mvn clean verify ``` to run the example test using PhantomJS.

### Stuff you can do

Want to run your tests on a different browser? No problem, just provide the 'browser' argument:

```bash
mvn clean verify -Dbrowser=chrome 
```

All you need to do is ensure the browser is installed in the default location (bootstrapium-vagrant pre-installs Firefox and Chrome).

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
