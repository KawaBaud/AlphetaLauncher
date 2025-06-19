# Alpheta Launcher

A resurrection of the original Minecraft Launcher from 2010, named after the year of original Minecraft Launcher's release.

## Features

- [x] Microsoft and Legacy account authentication
   - ~~Mojang authentication has been disabled since Mojang auth servers were shut down.~~
   - _Note: Accounts without Minecraft: Java Edition cannot connect to servers with `online-mode` enabled._
- [x] Play Legacy Minecraft versions from February 2010 to May 2013
- [x] Offline play support
   - _Requires initial online authentication_
- [x] Original Minecraft sound effects and music
- [x] Real-time language switching

---

### Report Issues or Suggest Features

[![GitHub Issues (Open)](https://img.shields.io/github/issues/KawaBaud/AlphetaLauncher?logo=github&style=for-the-badge)](https://github.com/KawaBaud/AlphetaLauncher/issues)
[![GitHub Issues (Closed)](https://img.shields.io/github/issues-closed/KawaBaud/AlphetaLauncher?logo=github&style=for-the-badge)](https://github.com/KawaBaud/AlphetaLauncher/issues?q=is%3Aissue+is%3Aclosed)

Found a bug or have a feature suggestion? Follow these steps:

1. Navigate to [issue tracker](https://github.com/KawaBaud/AlphetaLauncher/issues)
2. Select `New issue`
3. Provide clear description of occurred bug or feature request
4. Review and submit your issue

### Make Changes and Pull Requests

[![GitHub Pull Requests (Open)](https://img.shields.io/github/issues-pr/KawaBaud/AlphetaLauncher?logo=github&style=for-the-badge)](https://github.com/KawaBaud/AlphetaLauncher/pulls)
[![GitHub Pull Requests (Closed)](https://img.shields.io/github/issues-pr-closed/KawaBaud/AlphetaLauncher?logo=github&style=for-the-badge)](https://github.com/KawaBaud/AlphetaLauncher/pulls?q=is%3Apr+is%3Aclosed)

Want to contribute code? Follow these steps:

1. Fork this repository to your GitHub account
2. Create new branch for your feature or bugfix
3. Make and test your changes carefully
4. Submit pull request describing what you changed and why

---

## Prerequisites

[![Java](https://img.shields.io/badge/Java-8%2B-blue?style=for-the-badge)](https://www.java.com/en/download/)
[![SBT](https://img.shields.io/badge/SBT-1.9.6-blue?logo=scala&color=C71A36&style=for-the-badge)](https://www.scala-sbt.org/download.html)

### Building the Project

We use SBT (Scala Build Tool) to manage dependencies and build project. To get started:

1. Clone or download this repository
2. Open this project in your preferred IDE or terminal
3. Make your desired changes
4. Build using `sbt clean compile assembly` or your IDE's build system

The compiled JAR (Java Archive) file will be generated in `target` directory.

## Licence

See [LICENSE](LICENSE) for details.
