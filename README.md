# Frameworkium Lite

![CI](https://github.com/ham1/frameworkium-lite/actions/workflows/ci.yaml/badge.svg)
[![codecov](https://codecov.io/gh/ham1/frameworkium-lite/branch/master/graph/badge.svg?token=07Bjy2ePfw)](https://codecov.io/gh/ham1/frameworkium-lite)

A Framework for writing maintainable Selenium and REST API tests in Java.

To use this you will need the following in your `pom.xml`

```xml
  <repositories>
    <!-- Enables us to get dependencies from github -->
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>

  <dependencies>
    <dependency>
      <groupId>com.github.ham1</groupId>
      <artifactId>frameworkium-lite</artifactId>
      <version>4.4.1</version>
    </dependency>
  </dependencies>
```

Forked from https://github.com/Frameworkium/frameworkium-core

Incorporates a forked and updated version of
https://github.com/yandex-qatools/htmlelements
