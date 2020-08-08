#!/usr/bin/env groovy
import java.net.HttpURLConnection;
import groovy.json.*

def call(String projectName, String username , String language, String file) {
	// def commitUuid = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
	// def commitUuid = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
	def commitUuid = '578659cdd56e2dc511f13fde05b4969190cbaeb5'

	println('Parte 1')
	String new_file = new File(file).getText('UTF-8')
	
	println('the file was found and read')
	println(new_file)
	
	def cover_file = JsonOutput.toJson(new_file)
	String url = "https://api.codacy.com/2.0/${username}/${projectName}/commit/${commitUuid}/coverage/${language}"
	
	println(url)
	println(cover_file)

	URL new_url = new URL(url)
	HttpURLConnection post = (HttpURLConnection) new_url.openConnection();
	post.setRequestMethod("POST")
	post.setDoOutput(true)
	// post.setRequestProperty("project_token", token)
	post.setRequestProperty("Content-Type", "application/json")

	post.getOutputStream().write(cover_file.getBytes("UTF-8"))
	
	def postRC = post.getResponseCode()

	if(postRC.equals(200)) {
		println(post.getInputStream().getText())
	}
	return postRC
}
