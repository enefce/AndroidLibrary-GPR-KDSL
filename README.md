
[![Build Status](https://github.com/enefce/AndroidLibrary-GPR-KDSL/workflows/Android%20CI/badge.svg)](https://github.com/enefce/AndroidLibrary-GPR-KDSL/actions)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fenefce%2FAndroidLibrary-GPR-KDSL.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fenefce%2FAndroidLibrary-GPR-KDSL?ref=badge_shield)
[![Library Version](https://img.shields.io/badge/LibraryVersion-v1.0.2-brightgreen)](https://github.com/enefce/AndroidLibrary-GPR-KDSL/packages/50498)

![License](https://img.shields.io/github/license/enefce/AndroidLibrary-GPR-KDSL?color=2fc544)


# Sample Android Library for Publishing to GitHub Packages using Kotlin DSL script

  This Android project showcases the steps to publish and consume Android Libraries on the GitHub Packages using Kotlin DSL script.
   It is made up of 2 modules 
   
  1. ##### sampleAndroidLib2
   - Android library module with basic functionality
   - Publishes the generated library file onto the GitHub Packages
   - The build.gradle.kts file inside this module has the code (plugin, tasks and authentication) related to publishing the library
  2.  #####  app
   - Sample Android application module with the build.gradle.kts file that shows the code for consuming an Android library from GitHub Packages.
 
------------
## Publish Android library to GitHub Packages

### Step 1 : Generate a Personal Access Token for GitHub
- Inside you GitHub account:
	- Settings -> Developer Settings -> Personal Access Tokens -> Generate new token
	- Make sure you select the following scopes (" write:packages", " read:packages") and Generate a token
	- After Generating make sure to copy your new personal access token. You won’t be able to see it again!

### Step 2: Store your GitHub - Personal Access Token details
- Create a **github.properties** file within your root Android project
- In case of a public repository make sure you  add this file to .gitignore to keep the token private
	- Add properties **gpr.usr**=*GITHUB_USERID* and **gpr.key**=*PERSONAL_ACCESS_TOKEN*
	- Replace GITHUB_USERID with personal / organisation Github User ID and PERSONAL_ACCESS_TOKEN with the token generated in **#Step 1**
	
> Alternatively you can also add the **GPR_USER** and **GPR_API_KEY** values to your environment variables on you local machine or build server to avoid creating a github properties file

### Step 3 : Update build.gradle.kts inside the library module
- Add the following code to **build.gradle.kts** inside the library module
```markdown
import java.io.FileInputStream
import java.util.*
plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    id("maven-publish")
}
```
```markdown
val githubProperties = Properties()
githubProperties.load(FileInputStream(rootProject.file("github.properties")))
```
```markdown
fun getVersionName(): String {
    return "1.0.2" // Replace with version Name
}
```
```markdown
fun getArtificatId(): String {
    return "sampleAndroidLib2" // Replace with library name ID
}
```
```markdown
publishing {
    publications {
        create<MavenPublication>("gpr") {
            run {
                groupId = "com.enefce.libraries"
                artifactId = getArtificatId()
                version = getVersionName()
                artifact("$buildDir/outputs/aar/${getArtificatId()}-release.aar")
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            /** Configure path of your package repository on Github
             *  Replace GITHUB_USERID with your/organisation Github userID and REPOSITORY with the repository name on GitHub
             */
            url = uri("https://maven.pkg.github.com/enefce/AndroidLibrary-GPR-KDSL")
            credentials {
                /**Create github.properties in root project folder file with gpr.usr=GITHUB_USER_ID  & gpr.key=PERSONAL_ACCESS_TOKEN
                 * OR
                 * Set environment variables
                 */
                username = githubProperties.get("gpr.usr") as String? ?: System.getenv("GPR_USER")
                password =
                    githubProperties.get("gpr.key") as String? ?: System.getenv("GPR_API_KEY")

            }
        }
    }
}
```
### Step 4 : Publish the Android Library onto GitHub Packages
> Make sure to build and run the tasks to generate the library files inside ***build/outputs/aar/*** before proceeding to publish the library.

- Execute the ****Publish**** gradle task which is inside your library module
  
```markdown
$ gradle publish
```
- Once the task is successful you should be able to see the Package under the **Packages** tab of the GitHub Account
- In case of a failure run the task with *--stacktrace*, *--info* or *--debug* to check the logs for detailed information about the causes.
	
>NOTE: The library file (aar) will not include the transitive dependencies (i.e.dependencies used by the library).
For Maven repos, Gradle will download the dependencies using the pom file which will contain the dependencies list.
In this sample code/project, the pom file generated does not include the nested dependencies list. Either you have to specify the dependencies in your project that uses the library or you'll have to modify the code to generate a pom file with dependencies included while building and publishing the library.
------------
## Using a library from the GitHub Packages
> Currently the GitHub Packages requires us to Authenticate to download an Android Library (Public or Private) hosted on the GitHub Packages. This might change for future releases

> Steps 1 and 2 can be skipped if already followed while publishing a library

### Step 1 : Generate a Personal Access Token for GitHub
- Inside you GitHub account:
	- Settings -> Developer Settings -> Personal Access Tokens -> Generate new token
	- Make sure you select the following scopes ("read:packages") and Generate a token
	- After Generating make sure to copy your new personal access token. You won’t be able to see it again!

### Step 2: Store your GitHub - Personal Access Token details
- Create a **github.properties** file within your root Android project
- In case of a public repository make sure you  add this file to .gitignore for keep the token private
	- Add properties **gpr.usr**=*GITHUB_USERID* and **gpr.key**=*PERSONAL_ACCESS_TOKEN*
	- Replace GITHUB_USERID with personal / organisation Github User ID and PERSONAL_ACCESS_TOKEN with the token generated in **#Step 1**
	
> Alternatively you can also add the **GPR_USER** and **GPR_API_KEY** values to your environment variables on you local machine or build server to avoid creating a github properties file

### Step 3 : Update build.gradle.kts inside the application module
- Add the following code to **build.gradle.kts** inside the application module that will be using the library published on GitHub Packages Repository
```markdown
val githubPropertiesFile = rootProject.file("github.properties");
val githubProperties = Properties()
githubProperties.load(FileInputStream(githubPropertiesFile))
```
```markdown
    //GitHub Authentication
    repositories {
        maven {
            name = "GitHubPackages"
            /*  Configure path to the library hosted on GitHub Packages Registry
             *  Replace UserID with package owner userID and REPOSITORY with the repository name
             *  e.g. "https://maven.pkg.github.com/enefce/AndroidLibrary-GPR-KDSL"
             */
            url = uri("https://maven.pkg.github.com/UserID/REPOSITORY")
	    
            credentials {
                /**Create github.properties in root project folder file with gpr.usr=GITHUB_USER_ID  & gpr.key =PERSONAL_ACCESS_TOKEN**/
                username = githubProperties["gpr.usr"] as String? ?: System.getenv("GPR_USER")
                password = githubProperties["gpr.key"] as String? ?: System.getenv("GPR_API_KEY")
            }
        }
    }
```

- inside dependencies of the build.gradle.kts of app module, use the following code
```markdown
dependencies {
    //consume library
        implementation("com.enefce.libraries:sampleAndroidLib2:1.0.2")
	...
}
```



## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fenefce%2FAndroidLibrary-GPR-KDS.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fenefce%2FAndroidLibrary-GPR-KDS?ref=badge_large)


