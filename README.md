# PingIT
**A simple Android app to streamline helpdesk interactions**
![Status](https://www.dropbox.com/s/vmmlgcykl2wvbim/pingitstatus.png?dl=1)

Built from the ground up, this project aims to better connect students and helpdesk staff, providing a 1-on-1 chat service, equipment repair notifications, and FAQ database.

<p align="center"><img title="" src="https://github.com/stuartsoft/PingIT/raw/master/sample.png" height="250"/>&nbsp;&nbsp;<img title="" src="https://github.com/stuartsoft/PingIT/raw/master/sample2.png" height="250"/></p>

###Development Prerequisites
* JDK 1.7+
* Android Studio
  * API 21 or above
  * Android Support Library 23.1.1
  * Android Support Repo rev 25
  * Android SDK Tools 24.4.1
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
  app/build/outputs/lint-results.html
  ```
  **Build Parameters**
  ```
  env.ANDROID_HOME = Path to Android/SDK
  ```
  
###License
  MIT