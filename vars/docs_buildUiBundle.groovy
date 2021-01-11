#!groovy

/*************************************
 *                                   *
 *     shared-pipeline-ui-bundle     *
 *                                   *
 *************************************/

def call(body) {
    
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    // the complete declarative pipeline
    pipeline {
        agent { label "S00260435UV" }

    	environment {
    		remoteRepoUrl = "http://artifactory:8081/artifactory/libs-release-local"
    		remoteRepoPath = "com/provinzial/itdocs/ui"
    		credentials = "-u jenkins:AP4mqRCmZeogs1mS7RNHBMYwdS9"
    	}

    	options {
    		buildDiscarder(logRotator(numToKeepStr: '8', artifactNumToKeepStr: '8'))
            // timestamps ()
    	}

    	triggers {
    		// each monday at 6am
            cron("0 6 * * 1 ")
        }

    	stages {
            stage("prepare") {
    			steps {
    				sh "export SASS_BINARY_SITE=http://artifactory/artifactory/github/sass/node-sass/releases/download"
    			}
    		}

    		stage("install npm packages") {
                steps {
    				dir("src/ui") {
    					sh "node --version"
    					sh "npm --version"
    					sh "npm install"
    					sh "npm install gulp-cli"
    					sh "npm install antora-site-generator-lunr"
    				}
                }
            }

    		stage("build ui bundle") {
    			steps {
    				dir("src/ui") {
    					sh "./node_modules/.bin/gulp bundle"
    				}
    			}
    		}

    		stage("deploy ui bundle to artifactory") {
    			steps {
    				dir("target") {
    					sh "curl -i $credentials -X PUT $remoteRepoUrl/$remoteRepoPath/$pipelineParams.bundlename/ui-bundle.zip --data-binary @build/ui-bundle.zip"
    				}
    			}
    		}

    		// stage ("Invoke follow-up pipelines") {
    		// 	steps {
    		// 		build job: "antora-cep3-docs-pipeline"
    		// 	}
    		// }
    	}

        post {
            failure {
                // SLACK
            }
        }
    }
}
