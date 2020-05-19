<x-tag-head>
<x-tag-meta http-equiv="X-UA-Compatible" content="IE=edge"/>

<x-tag-script language="JavaScript"><!--
<X-INCLUDE url="https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.0.0/build/highlight.min.js"/>
--></x-tag-script>

<x-tag-script language="JavaScript"><!--
<X-INCLUDE url="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js" />
--></x-tag-script>

<x-tag-script language="JavaScript"><!--
<X-INCLUDE url="${gradleHelpersLocation}/spa_readme.js" />
--></x-tag-script>

<x-tag-style><!--
<X-INCLUDE url="https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.0.0/build/styles/github.min.css" />
--></x-tag-style>

<x-tag-style><!--
<X-INCLUDE url="${gradleHelpersLocation}/spa_readme.css" />
--></x-tag-style>
</x-tag-head>

# Fortify Vulnerability Exporter

## Introduction

This is work in progress. 

At the moment this utility provides just a prototype of how to wire various components
together; it doesn't do anything useful yet.
  
### Related Links

* **Downloads**:  
  _Beta versions may be unstable or non-functional. The `*-thirdparty.zip` file is for informational purposes only and does not need to be downloaded._
	* **Release versions**: https://bintray.com/package/files/fortify-ps/release/FortifyImportExportUtility?order=desc&sort=fileLastModified&basePath=&tab=files  
	* **Beta versions**: https://bintray.com/package/files/fortify-ps/beta/FortifyImportExportUtility?order=desc&sort=fileLastModified&basePath=&tab=files
	* **Sample configuration files**: [config](config)
* **GitHub**: https://github.com/fortify-ps/FortifyImportExportUtility
* **Automated builds**: https://travis-ci.com/fortify-ps/FortifyImportExportUtility


## TODO

Architecture & Design:

* Notify source system about exported entities (applications, release, vulnerabilities, ...)
    * Update source system based on configuration settings 
    * Actual functionality depends on source system and entity type
        * Vulnerability: bug link, comment, custom tag, ...
        * FoD application: application attrs
        * SSC version: version attrs
* Filter entities based on previously exported status
	* Target processors specify what information they need
	* All issues (independent of whether previously submitted), previously submitted issues (for updates), issues that have not yet been submitted
* How to easily support multiple configurations for a single source/target system
    * @ConfigurationProperties only allows for fixed property prefixes
    * Select between these configurations (connecting specific source and target configurations) 

Implementation:

* Add generic source filtering configuration and implementation
    * Filter & select SSC & FoD applications, releases, vulnerabilities, ...
    * Add filtering parameters to REST requests, perform regex filtering, ...
* Add functionality for grouping source data
    * Controlled by target configuration
* Finalize existing plugins
    * 'from FoD'
    * 'from SSC'
    * 'to file'
        * Generic data export to CSV, JSON, XML, ...
        * Support both console and file
        * Group data in multiple files in single run (by application/release/category/...)
* Add plugins:
    * 'from FPR'
        * Load vulnerability data from FPR files
    * 'to SonarQube external issue data'
        * To replace and enhance https://github.com/fortify-ps/FortifyFprToSonarQubeIssueData
        * Load vulnerabilities from SSC, FoD, FPR
        * Eventually replace https://github.com/fortify-ps/fortify-integration-sonarqube
        * Provide separate SonarQube plugins to enhance their generic issue data import
    * 'to bug trackers'
        * Eventually replace https://github.com/fortify-ps/FortifyBugTrackerUtility
    * 'to SSC' and 'to FoD': 
        * Import data from external sources into FoD or SSC
        * Import FPR/users/roles/applications/releases/... from file or from other FoD/SSC instances
    * 'from file': 
        * Read applications/releases/users/roles/... from a CSV/JSON/... file


Documentation:

* For users: 
    * Describe plugin system, optimize installation by removing unneeded plugins
	* Explain terminology, 'release' is used for both SSC version and FoD release
	* Troubleshooting; common error messages and solutions
	    * How to identify which plugins have been loaded
		* How to resolve configuration issues
* For developers: Describe architecture
	* Scheduled run vs run-once
	* Modifyable property scope
	* Extensible enums
	* Plugin system, provided dependencies, ...
	

## Migrating from FortifyBugTrackerUtility

Once finished, FortifyImportExportUtility can fully replace FortifyBugTrackerUtility, taking into account
the following differences:

FortifyBugTrackerUtility:
* Is invoked manually for each run, leaving scheduling to the operating system or CI/CD system
* Configuration is done using quite technical Spring XML configuration files
* Vulnerabilities are loaded in two phases; previously submitted vulnerabilities are loaded separately from new vulnerabilities to be submitted
* Only a single source and single target system is supported within a single configuration file, and within a single run

FortifyImportExportUtility:
* Can either be invoked manually for a single run, or can be run as a long-running process that handles scheduling of individual runs
* Configuration is done using more concise and user-friendly YAML configuration files
* All vulnerabilities are loaded only once from the source system, independent of whether they were previously exported
    * How to differentiate between previously submitted versus new vulnerabilities is now decided in the target configuration
* As an advanced use case, FortifyImportExportUtility can process multiple sources and targets within a single run
	
When migrating from FortifyBugTrackerUtility, please verify the following:
* FortifyImportExportUtility can successfully identify vulnerabilities previously exported with FortifyBugTrackerUtility
* TODO


## Developers

The following sections provide information that may be useful for developers of this utility.

### IDE's

This project uses Lombok. In order to have your IDE compile this project without errors, 
you may need to add Lombok support to your IDE. Please see https://projectlombok.org/setup/overview 
for more information.

### Gradle Wrapper

It is strongly recommended to build this project using the included Gradle Wrapper
scripts; using other Gradle versions may result in build errors and other issues.

The Gradle build uses various helper scripts from https://github.com/fortify-ps/gradle-helpers;
please refer to the documentation and comments in included scripts for more information. 

### Common Commands

All commands listed below use Linux/bash notation; adjust accordingly if you
are running on a different platform. All commands are to be executed from
the main project directory.

* `./gradlew tasks --all`: List all available tasks
* Build: (plugin binary will be stored in `build/libs`)
	* `./gradlew clean build`: Clean and build the project
	* `./gradlew build`: Build the project without cleaning
	* `./gradlew dist`: Build distribution zip
* Version management:
	* `./gradlew printProjectVersion`: Print the current version
	* `./gradlew startSnapshotBranch -PnextVersion=2.0`: Start a new snapshot branch for an upcoming `2.0` version
	* `./gradlew releaseSnapshot`: Merge the changes from the current branch to the master branch, and create release tag
* `./fortify-scan.sh`: Run a Fortify scan; requires Fortify SCA to be installed

Note that the version management tasks operate only on the local repository; you will need to manually
push any changes (including tags and branches) to the remote repository.

### Versioning

The various version-related Gradle tasks assume the following versioning methodology:

* The `master` branch is only used for creating tagged release versions
* A branch named `<version>-SNAPSHOT` contains the current snapshot state for the upcoming release
* Optionally, other branches can be used to develop individual features, perform bug fixes, ...
	* However, note that the Gradle build may be unable to identify a correct version number for the project
	* As such, only builds from tagged versions or from a `<version>-SNAPSHOT` branch should be published to a Maven repository

### CI/CD

Travis-CI builds are automatically triggered when there is any change in the project repository,
for example due to pushing changes, or creating tags or branches. If applicable, binaries and related 
artifacts are automatically published to Bintray using the `bintrayUpload` task:

* Building a tagged version will result in corresponding release version artifacts to be published
* Building a branch named `<version>-SNAPSHOT` will result in corresponding beta version artifacts to be published
* No artifacts will be deployed for any other build, for example when Travis-CI builds the `master` branch

See the [Related Links](#related-links) section for the relevant Travis-CI and Bintray links.


## License
<x-insert text="<!--"/>

See [LICENSE.TXT](LICENSE.TXT)

<x-insert text="-->"/>

<x-include url="file:LICENSE.TXT"/>
