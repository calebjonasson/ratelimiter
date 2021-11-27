pipeline {
    agent any

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "maven_3.8.4"
    }
    parameters {
        string(name: 'MAVEN_OPTIONS', defaultValue: '', description: 'Custom maven options that should be passed to the package command.')
        booleanParam(name: 'RUN_GENERATE_SITE_STAGE', defaultValue: false, description: 'Whether or not we want to generate the site.')
        booleanParam(name: 'RUN_INTEGRATION_STAGE', defaultValue: false, description: 'Whether we should run the integration tests or not.')
        booleanParam(name: 'RUN_DEPLOY_STAGE', defaultValue: false, description: 'Whether we should run the deploy stage or not.')
        string(name: 'CODE_COVERAGE_MINIMUM', defaultValue: '70', description: 'The minimum code coverage from jacoco reports.')
        string(name: 'CODE_COVERAGE_MAXIMUM', defaultValue: '90', description: 'The maximum code coverage from jacoco reports.')
        string(name: 'GIT_CREDENTIALS_ID', defaultValue: 'calebjonasson-github-jenkins', description: 'The git credentials id stored in jenkins.')

    }
    stages {

        stage ('Setup') {
            steps {
                echo 'PIPELINE STAGE: Setup'

                echo 'deleting workspace data'
                deleteDir()
                sh 'ls -lah'

                echo "setting up git using credentials $GIT_CREDENTIALS_ID"
                git credentialsId: "$GIT_CREDENTIALS_ID", url: 'git@github.com:calebjonasson/ratelimiter.git', branch: 'master'
            }
        }
        // No checkout stage ? That is not required for this case
        // because Jenkins will checkout whole repo that contains Jenkinsfile,
        // which is also the tip of the branch that we want to build
        stage ('Checkout') {
            steps {
                echo 'PIPELINE STAGE: Checkout'
                checkout scm
            }
        }
        stage ('Build') {
            steps {
                echo 'PIPELINE STAGE: Build'
                // For debugging purposes, it is always useful to print info
                // about build environment that is seen by shell during the build
                sh 'env'
                sh """
                SHORTREV=`git rev-parse --short HEAD`
                """

                script {
                    def nextVersion = getNextSemanticVersion();
                    println "Next version:" + nextVersion.toString();
                    env.NEXT_VERSION = nextVersion.toString();
                }
                sh """
                mvn -B org.codehaus.mojo:versions-maven-plugin:2.5:set -DprocessAllModules -DnewVersion=$NEXT_VERSION  $MAVEN_OPTIONS
                """
                sh """
                mvn -B clean compile $MAVEN_OPTIONS
                """
            }
        }

        stage('Unit Tests') {
            // We have seperate stage for tests so
            // they stand out in grouping and visualizations
            steps {
                echo 'PIPELINE STAGE: Unit Tests'
                sh """
                mvn -B test $MAVEN_OPTIONS
                """
            }
            // Note that, this requires having test results.
            // But you should anyway never skip tests in branch builds
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Integration Tests') {
            when {
                expression { RUN_INTEGRATION_STAGE == true }
            }
            steps {
                echo 'PIPELINE STAGE: Integration Tests'
                sh """
                mvn -B integration-test $MAVEN_OPTIONS
                """
            }
            post {
                always {
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }

        stage("Code coverage") {
            steps {
                echo 'PIPELINE STAGE: Code Coverage'
                script{
                    jacocoPublisher(
                        execPattern: '**/target/**.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src',
                        inclusionPattern: 'com/calebjonasson/**',
                        changeBuildStatus: true,
                        minimumInstructionCoverage: "$CODE_COVERAGE_MINIMUM",
                        maximumInstructionCoverage: "$CODE_COVERAGE_MAXIMUM"
                    )
                }
            }
        }


        stage('Generate Site') {

            when {
                expression { RUN_GENERATE_SITE_STAGE == true}
            }
            steps {
                echo 'PIPELINE STAGE: Generate Site'
                sh "mvn site:site -pl site"
            }

        }

        stage('Generate Report') {
            steps {
                echo 'PIPELINE STAGE: Generate Report'
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                //junit '**/target/surefire-reports/TEST-*.xml'
                archiveArtifacts '**/target/*.jar'

            }
        }

// These steps need to be completed. Tagging requires some investigation. Deploy will require either making the local nexus
// public or pushing release artifacts up to maven central.
//         stage('Git Tag') {
//             steps {
//                 echo 'PIPELINE STAGE: Git Tag'
//                 sh "git tag v$NEXT_VERSION"
//                 sh "git push origin v$NEXT_VERSION"
//             }
//         }
//
//         stage('Deploy') {
//             when {
//                 expression { RUN_DEPLOY_STAGE == true }
//             }
//             steps {
//                 echo 'PIPELINE STAGE: Deploy'
//                 // Finally deploy all your jars, containers,
//                 // deliverables to their respective repositories
//                 sh """
//                 mvn -B deploy
//                 """
//             }
//         }
    }
}