#!/usr/bin/env groovy

import groovy.json.*

def call(String token) {
    def get = new URL("https://api.codacy.com/2.0/project").openConnection();
    get.setRequestProperty("Accept", "application/json")
    get.setRequestProperty("project_token", token)
    def getRC = get.getResponseCode();
    if(getRC.equals(200)) {
        def json_str = get.getInputStream().getText()
        def json_beauty = JsonOutput.prettyPrint(json_str)
        def grade = new JsonSlurper().parseText(json_beauty).commit.commit.grade
        return grade
    }
}