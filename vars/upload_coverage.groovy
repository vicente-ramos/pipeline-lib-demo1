#!/usr/bin/env groovy

import groovy.json.*

def call(String token, String language, String file) {
	String url = "https://api.codacy.com/2.0/coverage"
	def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
	// def commit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()

	def cover_file = JsonOutput.toJson(file)
	url = url + "/" + commit + "/" + language

	post = url.openConnection()
	post.setRequestMethod("POST")
	post.setRequestProperty("project_token", token)
	post.setRequestProperty("Content-Type", "application/json")


	post.getOutputStream().write(cover_file.getBytes("UTF-8"));
	return post.getResponseCode();
}