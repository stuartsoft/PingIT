# PingIT

**A simple Android app to streamline helpdesk interactions**

![Status](https://www.dropbox.com/s/vmmlgcykl2wvbim/pingitstatus.png?dl=1)

Built from the ground up, this project aims to better connect students and helpdesk staff, providing a 1-on-1 chat service, equipment repair notifications, and FAQ database.

<p align="center"><img title="" src="https://github.com/stuartsoft/PingIT/raw/master/sample.png" height="350"/>&nbsp;&nbsp;<img title="" src="https://github.com/stuartsoft/PingIT/raw/master/demo.gif" height="350"/>&nbsp;&nbsp;<img title="" src="https://github.com/stuartsoft/PingIT/raw/master/sample2.png" height="350"/></p>

###Development Prerequisites
* JDK 1.7+
* Android Studio
  * API 21 or above
  * Android Support Library 23.1.1
  * Android Build Tools 23.0.2
* Fabric Intellij Plugin

###Setup
1. Clone this repo via the url

  ```
  git clone http://github.com/stuartsoft/pingit.git
  ```
2. Open the project in Android Studio, and allow gradle to build the project. Optionally, you may start a Gradle Sync/Build through the menu

  ```
  Tools > Android > Sync Project with Gradle Files
  ```
3. To deploy a development build to an emulator or Android device, select

  ```
  Run > Run 'app'
  ```

###Team City Build Server Setup
  **Default Branch**
  ```
  refs/heads/master
  ```
  **Branch Specification**
  ```
  +:refs/heads/master
  +:refs/heads/dev
  ```
  **Build Trigger**
  ```
  VCS Trigger     Branch filter: +:*
  ```
  **Build Artifact Paths**
  ```
  app/build/outputs/apk/app-debug.apk
  app/build/outputs/lint-results.html => quality/lint
  app/build/outputs/lint-results_files => quality/lint/lint-results_files
  app/build/reports/androidTests/connected => quality/tests
  ```
  **Build Parameters**
  ```
  env.ANDROID_HOME = ~/Android/Sdk
  ```
  **Build Steps**
  ```
  Gradle
      clean build - use wrapper
      Execute: If all previous steps finished successfully
  Instrumented tests
      Custom script:
        #Check connected devices
        adb devices
        #Wake devices
        /home/stuart/.BuildServer/wakeDevices.sh
        #run connected Android Tests
        chmod +x ./gradlew
        ./gradlew cAT
      Execute: If all previous steps finished successfully
  Mirror Build Status Badge
      Custom script: ~/.BuildServer/MirrorBuildStatusWrapper.sh
      Execute: Even if some of the previous steps failed
  ```
###License
```
The MIT License (MIT)

Copyright (c) 2016 Grove City College

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

