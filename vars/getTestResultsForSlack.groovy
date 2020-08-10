#!/usr/bin/env groovy
import hudson.tasks.test.AbstractTestResultAction


@NonCPS
def call() {
    testResults = "No tests found."
    AbstractTestResultAction testResultAction =  currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    if (testResultAction != null) {
        def total = testResultAction.totalCount
        def failed = testResultAction.failCount
        def skipped = testResultAction.skipCount
        def passed = total - failed - skipped
        testResults = "Passed: ${passed}, Failed: ${failed} ${testResultAction.failureDiffString}, Skipped: ${skipped}"
    }
    return testResults
}