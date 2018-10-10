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

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.joshcummings.codeplay.terracotta.testng.CrlfCheatSheet;
import com.joshcummings.codeplay.terracotta.testng.TestConstants;
import com.joshcummings.codeplay.terracotta.testng.XssCheatSheet;

public class TransferMoneyFunctionalTest extends AbstractEmbeddedTomcatSeleniumTest {
	@BeforeClass(alwaysRun=true)
	public void doLogin(ITestContext ctx) {
		System.out.println("Logging in b4 trying to transfer money");
		login("john.coltraine", "j0hn");
	}

	@AfterClass(alwaysRun=true)
	public void doLogout() {
		System.out.println("Logging out After trying to transfer money");
		logout();
	}
	
	@Test(groups="web")
	public void testTransferMoneyForXSS() {
		for (String template : new XssCheatSheet(true)) {
			goToPage("/");
			
			try {
				String fromAccountNumber = String.format(template, "fromAccountNumber");
				String toAccountNumber = String.format(template, "toAccountNumber");
				String transferAmount = String.format(template, "transferAmount");

				driver.findElement(By.name("fromAccountNumber")).sendKeys(fromAccountNumber);
				driver.findElement(By.name("toAccountNumber")).sendKeys(toAccountNumber);
				driver.findElement(By.name("transferAmount")).sendKeys(transferAmount);
				driver.findElement(By.name("transfer")).submit();
				
				Alert alert = switchToAlertEventually(driver, 2000);
				Assert.fail(getTextThenDismiss(alert));
			} catch (NoAlertPresentException e) {
				// awesome!
			}
		}
	}
	
	@Test(groups="http")
	public void testTransferMoneyForCRLF() {
		for ( String template : new CrlfCheatSheet() ) {
			goToPage("/");
			
			try {
				String c = String.format(template, "c");
				String fromAccountNumber = String.format(template, "fromAccountNumber");
				String toAccountNumber = String.format(template, "toAccountNumber");
				String transferAmount = String.format(template, "transferAmount");
				
				try ( CloseableHttpResponse response = 
					honest.post("/transferMoney?c=" + c,
						new BasicNameValuePair("c", c),
						new BasicNameValuePair("fromAccountNumber", fromAccountNumber),
						new BasicNameValuePair("toAccountNumber", toAccountNumber),
						new BasicNameValuePair("transferAmount", transferAmount)) ) {
					Header[] headers = response.getHeaders("X-Evil-Header");
					Assert.assertTrue(headers.length == 0, Arrays.toString(headers));					
				}
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
	}
	
	@Test(groups="http")
	public void testTransferMoneyForCSRF() throws Exception {
		goToPage("/");
		
		// read what is in John's account right now
		String originalTotal = driver.findElement(By.id("accountBalance-987654321")).getText();
		
		driver.get("http://" + TestConstants.evilHost + "/evilsite/csrf.html");
		
		// wait for the stealing script to take effect
		Thread.sleep(5000);
		
		goToPage("/");
		
		// refresh the page and see how much John has now that the csrf script has run
		String newTotal = driver.findElement(By.id("accountBalance-987654321")).getText();
		
		// if they are equal, no csrf vulnerability!
		Assert.assertEquals(Double.parseDouble(originalTotal), Double.parseDouble(newTotal));
	}
}
