#!/usr/bin/env groovy
import java.net.HttpURLConnection;
import groovy.json.*

def call(String token, String language, String file) {
	String url = "https://api.codacy.com/2.0/coverage"
	//def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
	def commit = '91dba5606fe316d1fa211f3ca973b67d7b5fd00f'
	// def commit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()

	def cover_file = JsonOutput.toJson(file)
	url = url + "/" + commit + "/" + language

	echo "The url is ${url}"
	URL new_url = new URL(url)
	HttpURLConnection post = (HttpURLConnection) new_url.openConnection();
	// .openConnection();
	post.setRequestMethod("POST")
	post.setDoOutput(true)
	post.setRequestProperty("project_token", token)
	post.setRequestProperty("Content-Type", "application/json")


	post.getOutputStream().write(cover_file.getBytes("UTF-8"));
	return post.getResponseCode();
}