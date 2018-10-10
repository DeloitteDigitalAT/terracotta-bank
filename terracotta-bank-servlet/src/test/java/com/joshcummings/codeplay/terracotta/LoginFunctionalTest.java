/*
 * Copyright 2015-2018 Josh Cummings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joshcummings.codeplay.terracotta;

import com.joshcummings.codeplay.terracotta.testng.TestConstants;
import com.joshcummings.codeplay.terracotta.testng.XssCheatSheet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.http.client.methods.RequestBuilder.post;
import static org.junit.Assert.assertEquals;

public class LoginFunctionalTest extends AbstractEmbeddedTomcatSeleniumTest {
	@AfterMethod(alwaysRun=true)
	public void doLogout() {
		logout();
	}
	
	@Test(groups="web")
	public void testLoginForXss() throws InterruptedException {
		for ( String template : new XssCheatSheet() ) {
			goToPage("/");
			
			try {
				String usernameXss = String.format(template, "username");
				
				driver.findElement(By.name("username")).sendKeys(usernameXss);
				driver.findElement(By.name("login")).submit();
				
			   	 Alert alert = switchToAlertEventually(driver, 2000);
			   	 Assert.fail(getTextThenDismiss(alert) + " using " + template);
			} catch ( NoAlertPresentException e ) {
				// okay!
			}
		}
	}
	
	@Test(groups="http")
	public void testLoginForOpenRedirect() throws InterruptedException {
		goToPage("/?relay=http://" + TestConstants.evilHost);

		driver.findElement(By.name("username")).sendKeys("admin");
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.name("login")).submit();
		
		Thread.sleep(2000);
		
		Assert.assertEquals(driver.getCurrentUrl(), "http://honestsite.com/", "You got redirected to: " + driver.getCurrentUrl());
	}

	@Test(groups="data", expectedExceptions=NoSuchElementException.class)
	public void testLoginForSQLi() {
		goToPage("/");
			
		String usernameSQLi = "' OR 1=1 --";
			
		driver.findElement(By.name("username")).sendKeys(usernameSQLi);
		driver.findElement(By.name("login")).submit();

		findElementEventually(driver, By.id("deposit"), 2000);
		Assert.fail("Successful login with SQLi!");
	}

	@Test(groups="enumeration")
	public void testLoginForEnumeration() throws Exception {
		try (
				CloseableHttpResponse exists = http.post(post("/login")
					.addParameter("username", "admin")
					.addParameter("password", "theincorrectpassword"));

				CloseableHttpResponse notsomuch = http.post(post("/login")
					.addParameter("username", "anonexistentuser")
					.addParameter("password", "doesntmatter")) ) {

			List<String> existHeaderNames = Stream.of(exists.getAllHeaders())
					.map(header -> header.getName())
					.collect(Collectors.toList());

			List<String> notsomuchHeaderNames = Stream.of(notsomuch.getAllHeaders())
					.map(header -> header.getName())
					.collect(Collectors.toList());

			assertEquals(exists.getStatusLine().toString(), notsomuch.getStatusLine().toString());
			assertEquals(existHeaderNames, notsomuchHeaderNames);
			assertStreamEquals(exists.getEntity().getContent(), notsomuch.getEntity().getContent());
		}
	}

	@BeforeTest(groups="bruteforce")
	public void clearIpSoftLockoutCache() {
		http.postForContent(
				post("/login")
						.addParameter("username", "josh.cummings")
						.addParameter("password", "j0sh"));
	}

	@Test(groups="bruteforce")
	public void testLoginForAdminBackdoor() throws Exception {
		String content = http.postForContent(post("/login")
								.addParameter("username", "admin")
								.addParameter("password", "admin"));

		Assert.assertTrue(content.contains("provided is incorrect"));
	}

	@Test(groups="bruteforce")
	public void testLoginForSingleAccountBruteForce() throws Exception {
		Assert.assertFalse(detectBruteForce("upton.sinclair"));
		Assert.assertFalse(detectBruteForce("upton.sinclair"));
		Assert.assertFalse(detectBruteForce("upton.sinclair"));
		Assert.assertFalse(detectBruteForce("upton.sinclair"));
		Assert.assertFalse(detectBruteForce("upton.sinclair"));
		Assert.assertTrue(detectBruteForce("upton.sinclair"));
		Assert.assertTrue(detectBruteForce("upton.sinclair"));
		Assert.assertTrue(detectBruteForce("upton.sinclair"));
		Assert.assertTrue(detectBruteForce("upton.sinclair"));
	}

	@Test(groups="bruteforce")
	public void testLoginForSingleAccountBruteForceAndEnumeration() throws Exception {
		Assert.assertFalse(detectBruteForce("unknown.user"));
		Assert.assertFalse(detectBruteForce("unknown.user"));
		Assert.assertFalse(detectBruteForce("unknown.user"));
		Assert.assertFalse(detectBruteForce("unknown.user"));
		Assert.assertFalse(detectBruteForce("unknown.user"));
		Assert.assertTrue(detectBruteForce("unknown.user"));
		Assert.assertTrue(detectBruteForce("unknown.user"));
		Assert.assertTrue(detectBruteForce("unknown.user"));
		Assert.assertTrue(detectBruteForce("unknown.user"));
	}

	@Test(groups="bruteforce")
	public void testLoginForCrossAccountBruteForce() throws Exception {
		for ( int i = 0; i < 25; i++ ) {
			Assert.assertFalse(detectBruteForce("username" + i));
		}

		Assert.assertTrue(detectBruteForce("some.other.username"));
	}

	@Test(groups="bruteforce")
	public void testLoginForTwoFactorBackdoor() {
		String content =
				http.postForContent(
						post("/login")
								.addParameter("username", "needstwofactor")
								.addParameter("password", "notagreatpasswordbutbetterthanthecompetition")
								.addParameter("code", "123456"));

		Assert.assertTrue(content.contains("Something about the login provided is incorrect."));
	}

	@Test(groups="bruteforce")
	public void testLoginForTwoFactorBruteForce() {
		String content =
				http.postForContent(
					post("/login")
						.addParameter("username", "needstwofactor")
						.addParameter("password", "notagreatpasswordbutbetterthanthecompetition"));

		// even though the username and password were correct, this account requires a second factor, too.

		Assert.assertTrue(content.contains("Something about the login provided is incorrect."));
	}

	private Map.Entry<String, String> attemptLogin(String username) {
		String content = http.postForContent(
				post("/login")
						.addParameter("username", username)
						.addParameter("password", "oi12bu34ci 123h 4dp2i3h4 234jn"));

		return new AbstractMap.SimpleImmutableEntry<>(content, username);
	}

	private boolean detectBruteForce(String username) throws Exception {
		String content = http.postForContent(
				post("/login")
						.addParameter("username", username)
						.addParameter("password", "wrongpassword"));

		return content.contains("This account has been temporarily locked");
	}

	private static void assertStreamEquals(InputStream expectedInput, InputStream actualInput)
		throws IOException {

		int expectedRead = -1;
		int actualRead = -1;

		ByteArrayOutputStream expectedOutput = new ByteArrayOutputStream();
		ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();

		while ( ( expectedRead = expectedInput.read() ) != -1 &&
				( actualRead = actualInput.read() ) != -1 ) {
			expectedOutput.write(expectedRead);
			actualOutput.write(actualRead);

			if ( expectedRead != actualRead ) {
				break;
			}
		}

		if ( expectedRead != actualRead ) {
			String expected = new String(expectedOutput.toByteArray());
			String actual = new String(actualOutput.toByteArray());
			assertEquals(expected, actual);
		}
	}
}
