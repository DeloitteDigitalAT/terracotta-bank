# Terracotta Bank

Terracotta Bank is an intentionally-vulernable web application, useful for practicing detection, exploitation, and mitigation of common web application security vulnerabilities.

Terrcotta Bank binds locally to port 8080 by default, and while it is running, the machine on which it is running is vulnerable in the same way that this application is.

## Getting Started

Terracotta Bank is a fully-functional Spring Boot web application that lacks most common security mechanisms and makes numerous classic security mistakes.
 
To run Terracotta Bank, simply clone the repo and then run:

```bash
./gradlew bootRun
```

And browse to `localhost:8080` to begin looking for vulnerabilities.

## Usage

The intent of this application is to support engineers in practicing and learning about secure coding, ethical hacking, and detection and triage techniques. There are several ways to engage with this application to achieve this.

### Blind Pentesting

Because Terracotta Bank is a real web application, it can be effective to point popular pentesting tools like Burpsuite, ZAP, and sqlmap at it to discover vulnerabilities.

For example, try pointing [http://sqlmap.org/](sqlmap) at the `/login` endpoint to find more than one way to log in.

### Unit Tests

Terracotta Bank is equipped with dozens of intentionally failing unit tests. These are useful for praticing mitigation. As mitigation steps are added, the unit tests begin to pass, helping engineers evaluate the strength of their mitigations.

### Lessons

For a more guided approach, see the `/lessons` folder, which has written guides explaining the nature of a given vulnerability along with some hints for how to exploit and mitigate it in the application.
 
### Branches

For each lesson, there is a git branch with individual commits which progressively make the application more secure against the vulnerability described. Follow the commit messages to gain an understanding of good mitigations and why they are beneficial.

## Find Security Bugs

We have enabled the [findsecbugs gradle plugin](https://spotbugs.readthedocs.io/en/latest/gradle.html).
To see it in action you can run:

* `./gradlew spotbugsMain` to run SpotBugs analysis against your production code in your CI pipeline.

You will then be able to see the output below in the console:

```
Starting a Gradle Daemon (subsequent builds will be faster)

> Task :compileJava
Note: /Users/jdamore/dev/projects/terracotta-bank/terracotta-bank-servlet/src/main/java/com/joshcummings/codeplay/terracotta/config/WebConfiguration.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.

> Task :spotbugsMain FAILED
The following classes needed for analysis were missing:
  prepare
  apply

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':spotbugsMain'.
> A failure occurred while executing com.github.spotbugs.internal.spotbugs.SpotBugsRunner
   > SpotBugs rule violations were found. See the report at: file:///Users/jdamore/dev/projects/terracotta-bank/terracotta-bank-servlet/build/reports/spotbugs/main.html

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 16s
3 actionable tasks: 2 executed, 1 up-to-date
```

In addition you can also publish the report generated in `./build/reports/spotbugs/main.html`

