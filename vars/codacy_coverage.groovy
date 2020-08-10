#!/usr/bin/env groovy
import java.net.HttpURLConnection;
import groovy.json.*

def call(String projectName, String username , String language, String file, String token) {
	def commitUuid = getLastCommitid()

	// Get Absolute path in Workspace.
	def filePath = "${env.WORKSPACE}" + "/" + file

	// Parse the file contents.
    def jsonSlurper = new JsonSlurper()
    File src = new File(filePath)

    def data_raw = jsonSlurper.parse(src)

	// Convert to json.
    def data = JsonOutput.toJson(data_raw)

	String url = "https://api.codacy.com/2.0/${username}/${projectName}/commit/${commitUuid}/coverage/${language}"


	URL new_url = new URL(url)
	HttpURLConnection post = (HttpURLConnection) new_url.openConnection();
	post.setRequestMethod("POST")
	post.setDoOutput(true)
	post.setRequestProperty("project_token", token)
    post.setRequestProperty("Accept", "application/json");
	post.setRequestProperty("Content-Type", "application/json")

	// Post the json content to api.
	post.getOutputStream().write(data.toString().getBytes("UTF-8"))
	
	def postRC = post.getResponseCode()

	if(postRC.equals(200)) {
		println(post.getInputStream().getText())
	}

	return postRC
}

