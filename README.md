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

## OWASP Dependency Checker

We have enabled the [OWASP Dependencx Checker gradle plugin](https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/index.html).
To see it in action you can run:

* `./gradlew check or ./gradlew dependencyCheckAnalyze` to run the analysis against your project in your CI pipeline.

You will then be able to see the output below in the console:

```
> Task :dependencyCheckAnalyze
Verifying dependencies for project terracotta-bank-servlet
Checking for updates and analyzing dependencies for vulnerabilities
----------------------------------------------------
.NET Assembly Analyzer could not be initialized and at least one 'exe' or 'dll' was scanned. The 'mono' executable could not be found on the path; either disable the Assembly Analyzer or configure the path mono. On some systems mono-runtime and mono-devel need to be installed.
----------------------------------------------------
Generating report for project terracotta-bank-servlet
Found 59 vulnerabilities in project terracotta-bank-servlet

One or more dependencies were identified with known vulnerabilities:

jstl-1.2.jar: ids:(javax.servlet:jstl:1.2, cpe:/a:apache:standard_taglibs:1.2.1) : CVE-2015-0254
tomcat-embed-jasper-8.5.29.jar: ids:(cpe:/a:apache:tomcat:8.5.29, cpe:/a:apache_tomcat:apache_tomcat:8.5.29, org.apache.tomcat.embed:tomcat-embed-jasper:8.5.29, cpe:/a:apache_software_foundation:tomcat:8.5.29) : CVE-2018-11784, CVE-2018-1336, CVE-2018-8014, CVE-2018-8034, CVE-2018-8037
tomcat-embed-websocket-8.5.29.jar: ids:(cpe:/a:apache:tomcat:8.5.29, cpe:/a:apache_tomcat:apache_tomcat:8.5.29, org.apache.tomcat.embed:tomcat-embed-websocket:8.5.29, cpe:/a:apache_software_foundation:tomcat:8.5.29) : CVE-2018-11784, CVE-2018-1336, CVE-2018-8014, CVE-2018-8034, CVE-2018-8037
tomcat-embed-core-8.5.29.jar: ids:(cpe:/a:apache:tomcat:8.5.29, cpe:/a:apache_tomcat:apache_tomcat:8.5.29, org.apache.tomcat.embed:tomcat-embed-core:8.5.29, cpe:/a:apache_software_foundation:tomcat:8.5.29) : CVE-2018-11784, CVE-2018-1336, CVE-2018-8014, CVE-2018-8034, CVE-2018-8037
spring-data-commons-2.0.6.RELEASE.jar: ids:(cpe:/a:pivotal_software:spring_data_commons:2.0.6, org.springframework.data:spring-data-commons:2.0.6.RELEASE) : CVE-2018-1259
dom4j-1.6.1.jar: ids:(dom4j:dom4j:1.6.1, cpe:/a:dom4j_project:dom4j:1.6.1) : CVE-2018-1000632
jackson-databind-2.9.5.jar: ids:(com.fasterxml.jackson.core:jackson-databind:2.9.5, cpe:/a:fasterxml:jackson:2.9.5, cpe:/a:fasterxml:jackson-databind:2.9.5) : CVE-2018-1000873, CVE-2018-14718, CVE-2018-14719, CVE-2018-14720, CVE-2018-14721, CVE-2018-19360, CVE-2018-19361, CVE-2018-19362
selenium-opera-driver-3.9.1.jar: ids:(cpe:/a:opera_software:opera:3.9.1, org.seleniumhq.selenium:selenium-opera-driver:3.9.1, cpe:/a:opera:opera:3.9.1) : CVE-2003-1561, CVE-2008-1761, CVE-2008-1764, CVE-2008-3079, CVE-2008-3172, CVE-2008-4293, CVE-2008-4695, CVE-2008-4696, CVE-2008-4794, CVE-2008-4795, CVE-2008-5679, CVE-2009-0915, CVE-2009-2068, CVE-2010-5227, CVE-2015-8960, CVE-2016-7152
jarchivelib-0.7.1.jar: ids:(org.rauschig:jarchivelib:0.7.1, cpe:/a:apache:commons-compress:0.7.1) : CVE-2012-2098
guava-23.6-jre.jar: ids:(cpe:/a:google:guava:23.6, com.google.guava:guava:23.6-jre) : CVE-2018-10237
netty-all-4.1.23.Final.jar: ids:(cpe:/a:all-for-one:all_for_one:4.1.23, cpe:/a:netty_project:netty:4.1.23, io.netty:netty-all:4.1.23.Final) : CVE-2018-12056
bcprov-jdk15on-1.54.jar: ids:(cpe:/a:bouncycastle:legion-of-the-bouncy-castle-java-crytography-api:1.54, org.bouncycastle:bcprov-jdk15on:1.54) : CVE-2016-1000338, CVE-2016-1000339, CVE-2016-1000340, CVE-2016-1000341, CVE-2016-1000342, CVE-2016-1000343, CVE-2016-1000344, CVE-2016-1000345, CVE-2016-1000346, CVE-2016-1000352, CVE-2016-2427, CVE-2017-13098, CVE-2018-1000180, CVE-2018-1000613


See the dependency-check report for more details.


> Task :dependencyCheckAnalyze FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':dependencyCheckAnalyze'.
>

  Dependency-Analyze Failure:
  One or more dependencies were identified with vulnerabilities that have a CVSS score greater then '8.0': CVE-2008-3079, CVE-2008-4293, CVE-2008-1761, CVE-2008-1764, CVE-2008-4695, CVE-2008-4794, CVE-2008-5679
  See the dependency-check report for more details.
```

In addition you can also publish the report generated in `./build/reports/dependency-check-report.html`

The HTML report generated by the OWASP Dependency Checker will give you:
*	A list of all dependencies that have known vulnerabilities (CPEs)

*	For each CPE:
1. The Severity
2. The number of reported CVEs
3. Details of each CVE
4. The level of confidence that this CPE is vulnerable

*	For each CVE:
1. The Severity
2. The corresponding CWE (Common Weakness Enumeration)
3. Some details

