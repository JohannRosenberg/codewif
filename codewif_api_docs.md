![graphics/codewif_icon_with_text_400x104.png](graphics/codewif_icon_with_text_400x104.png)

# Codewif API Documentation

The following classes are available for testing:

| Class      | Description                                                                                                                                                       |
|------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| TestResult | Test results are stored in this class which is part of the TestInfo class.                                                                                        |
| TestRunner | Tests are executed by this singleton. The test runner also communicates with the Codewif Service to store UI snapshots and send test results to a backend server. |
| TestSetup  | You use this class to add UnitTest instances to your test setup.                                                                                                  |
| UITestInfo | If a test is a UI test, this class contains information about snapshots and hashcodes for previous snapshots as well as for the current UI under test.            |
| UnitTest   | Used to define the type of test you want to run: synchronous or asynchronous. This is also where your test code is located.                                       |

<br> 

## TestRunner 
This is primary class that your app or library will communicate with to setup and execute tests. TestRunner is a singleton.

| Method/Property                | Description                                                                                      |
|--------------------------------|--------------------------------------------------------------------------------------------------|
| addTestSetups                  | Adds test classes to a list that will be run by the test runner. **REQUIRED**                    |
| cancelTesting                  | Cancels testing.                                                                                 |
| closeUIWhenTesting             | Closes Codewif's UI before testing starts.                                                       |
| displayTestResults             | Displays the screen that lists all the tests that are running or ran.                            | 
| displayTests                   | Displays the screen that lists all the tests that can be run.                                    | 
| exportAllTestsToJson           | Creates a JSON formatted report containing all the tests.                                        | 
| exportFailedTestsToJson        | Creates a JSON formatted report containing only the tests that failed.                           | 
| runTests                       | Runs one or more tests. **REQUIRED**                                                             | 
| sendTestResultsToBackend       | Sends the test results to a backend server.                                                      | 
| setAppContext                  | Provides Codewif access to your app under test. **REQUIRED**                                     | 
| setAppName                     | Identifies the name of your Android project if it's an app. **REQUIRED if testing an app**       | 
| setGitBranchName               | Identifies the name of your Git branch that is being used for testing. **REQUIRED**              | 
| setLibraryPackageName          | Identifies the name of your Android project if it's a library. **REQUIRED if testing a library** | 
| setOnTestingCompletedListener  | A callback that gets called after all tests have completed.                                      | 
| setProjectId                   | A unique id that identifies your Android project being tested.                                   | 
| setVersionName                 | Identifies the version of your app or library. **REQUIRED**                                      | 
| showTestResultsAfterTesting    | Displays the test results screen after testing has completed.                                    | 
| terminateTestingOnFirstFailure | Causes testing to terminate when the first test fails.                                           | 
<br>

### addTestSetups
Adds one or more test classes. Each test class must inherit from TestSetup. The order in which the class references are added is the order in which tests are carried
out. This method must be the last method your test setup calls before calling ```runTests```.

```kotlin
// Example
TestRunner.addTestSetups(::MathUnitTests, ::StringUnitTests)
```

### cancelTesting
Cancels testing if it is in progress.

```kotlin
// Example
TestRunner.cancelTesting()
```

### closeUIWhenTesting
Causes Codewif's UI to close before testing starts (if it was opened before testing began) and remains closed during the duration of testing. After testing has
completed, Codewif's UI will be shown again if it was shown before testing began. If the UI was not shown before testing began and you want to show it after testing
completes, you need to call the ```showTestResultsAfterTesting``` method.

```kotlin
// Example
TestRunner.closeUIWhenTesting()
```

### displayTestResults
Displays Codewif's test results screen. By default, when Codewif's UI is shown, the tests screen is shown where all your tests are defined. This is a different
screen than the test results screen. If you want to show the test results screen while testing is underway, call this before you call ```runTests```.

```kotlin
// Example
TestRunner.displayTestResults()
```

### displayTests
Displays Codewif's tests screen. The tests screen is where all your tests are defined. If you want to show the tests screen while testing is underway, call this
before you call ```runTests```. You would normally call this method if you want to manually start testing. The tests screen allows you to enable or disable any
number of tests and it also lets you click on a single test to execute it immediately.

```kotlin
// Example
TestRunner.displayTests()
```

### exportAllTestsToJson
Exports (serializes) the test results into a JSON string. The test results includes all tests. The exported data is essentially a report and contains information
that includes:

* Project Id
* Git branch name
* Name of app or library
* Version of the app or library
* A timestamp when the tests were executed
* Info about each test: test id, whether the test succeeded or failed, duration andwhether the test was skipped. If the test is a UI test, the hashcode and url to
  the snapshot are included.

```kotlin
// Example
val testResultsJson = TestRunner.exportAllTestsToJson()
```

### exportFailedTestsToJson
Same as the ```exportAllTestsToJson``` except only the failed tests are exported.

```kotlin
// Example
val testResultsJson = TestRunner.exportFailedTestsToJson()
```

### runTests
Causes all the tests to be executed that are added to the test runner with the ```addTestSetups``` method. This must be the last method that is called in your test
setup. When testing is in progress and Codewif's UI is shown, you can always cancel testing by pressing on the Cancel button, ![graphics/hand.svg](graphics/hand.svg)
, in the top right corner of the toolbar.


```kotlin
// Example
TestRunner.runTests()
```

### sendTestResultsToBackend
Lets you send test results to a backend server. It takes 3 parameters which are all optional. If you choose to send the test results to your own backend, you could
initially send the results to a free online service called hookbin, https://hookbin.com. You can quickly verify the JSON data and see any request headers you choose
to include.

**includeAllTests:** If set to true, all test results are sent. If set to false (the default), only tests that have failed are sent.

**url:** The url where the test results are sent to. If not specified, Codewif's backend will be used (currently under development). For Codewif's backend, you will
have to obtain a userid and secret that you then include in your setup (this feature is currently not available).

**requestHeaders:** If you are using your own backend server and need to include HTTP headers in the request, specify the headers in this MutableMap. A map entry's key
is the name of the request header and the value is the header's value.


```kotlin
// Example 1
TestRunner.sendTestResultsToBackend()

// Example 2
TestRunner.sendTestResultsToBackend(includeAllTests = true)

// Example 3
val requestHeaders = mutableMapOf("api_key" to "abc", "secret" to "my_secret")
TestRunner.sendTestResultsToBackend(url = "https://www.myserver.com/my_test_results_endpoint/", requestHeaders)

```

### setAppContext
This provides Codewif with a reference to your app or library under test. This must be the very first thing you call when setting up your tests.

```kotlin
// Example
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this
        val mainScope = MainScope()

        mainScope.launch {
            TestController
                .setAppContext(context)
                .setAppName()
                .setVersionName(com.codewif.sample.BuildConfig.VERSION_NAME)
                .setGitBranchName("unit_tests")
                .onTestingCompleted()
                .sendTestResultsToBackend()
                .hideUIWhenTesting()
                .showTestResultsAfterTesting()
                .addTestSetups()
                .runTests()
        }
    }
}
```

### setAppName
If your app under test is an app (and not a library), call this method. Codewif will obtain the name of your app. Usually this is the text that is set in the
app_name element of your app's string resource file.


```kotlin
// Example
TestRunner.setAppName()
```

### setGitBranchName
Indicates the name of the Git test branch that the app or library is built from.

**branchName:** The name of the branch.


```kotlin
// Example
TestRunner.setGitBranchName("unit_tests")
```

### setLibraryPackageName
If the app being tested is an Android library (AAR), call this so that Codewif can determine the package name that the library uses.

```kotlin
// Example
TestRunner.setLibraryPackageName()
```

### setOnTestingCompletedListener
Creates a callback that will get called after all tests have been completed.

**callback: (succeeded: Boolean) -> Unit:** A lambda expression. When called, the *succeeded* parameter will be set to true if all tests have passed.


```kotlin
// Example
TestRunner.setOnTestingCompletedListener { succeeded ->
    // Do something after all tests have completed
}
```

### setProjectId
The project Id is a string that should uniquely identify your project. This can be whatever you want it to be. It is used to distinguish one of your projects from
other projects you have. You should avoid using anything that could potentially change during the lifetime of your app or even during initial development. For
example, you should avoid using your app's package name (example: com.mycompany.myapp) because some developers do change their packages name before making their
first production release. Your project Id is used by Codewif to associate all the test data. If you were to change your project Id after Codewif stored screen
snapshots for UI tests, those screenshots would no longer be available.

If your organisation uses Jira for project management, you might find it useful to use a project Id that matches with the project Id that you use for your Jira
project so that you can easily correlate test data with your Jira project, which is especially useful if you also use CI tools that need to work with the test
results.

**projectId:** Some text that will help to uniquely identify your app or library that is being tested.


```kotlin
// Example
TestRunner.setProjectId("My Calculator App")
```

### setVersionName
Sets the version name to identify the version of your app or library.

**versionName:** Some text to identify the version.


```kotlin
// Example 1
// This will extract the version name from the versionName property in your build.gradle file.
TestRunner.setVersionName(com.codewif.sample.BuildConfig.VERSION_NAME)

// Example 2
TestRunner.setVersionName("0.1-Alpha")
```

### showTestResultsAfterTesting
This will cause Codewif's test results screen to be displayed after testing has completed. This is useful if you plan on running the tests manually or need to check
screenshots when UI tests failed. You wouldn't use this if your are running fully automated tests on a backend CI server.

```kotlin
// Example
TestRunner.showTestResultsAfterTesting()
```

### terminateTestingOnFirstFailure
This will cause testing to be terminated as soon as the first test has failed. If you have several asynchronous tests in progress when some test fails, it is
possible that there may be a slight delay before all tests are completely canceled.

```kotlin
// Example
TestRunner.terminateTestingOnFirstFailure()
```
