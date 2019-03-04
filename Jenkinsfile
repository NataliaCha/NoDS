String getSpace(String branchName) {
    branchName = branchName.split('_')[0]
    String space
    // Add more spaces if you need
    switch(branchName) {
        case 'develop':
        case 'integration':
        case 'qa':
            space = 'dev'
            break
        default:
            space = 'dev'
            //error("Unknown branch. Add support for your branch: ${branchName}")
    }
    space
}

String getPredixEnv(predixEnv) {
    predixEnv
}


String getPoolSchedule(String branchName) {
    branchName = branchName.split('_')[0]
    String schedule = ''
    // Add more branches if you need
    // switch(branchName) {
    //     case 'develop':
    //         schedule = '* * * * *'
    //         break
    // }
    schedule
}

pipeline {
    agent none
    parameters {
        // Cannot use array because of the https://issues.jenkins-ci.org/browse/JENKINS-38995
        choice(choices: "dev\nqa\nint", description: 'Set predix env?', name: 'predixEnv')
        choice(choices: "all\nmapper\nmapped\nunmapped\nasset", description: 'Set group of tests', name: 'testGroup')
    }
    triggers {
        pollSCM(getPoolSchedule(env.BRANCH_NAME))
        cron('1 8 * * *')
    }
    stages {
        stage('Integration testing') {
            agent {
                docker {
                    image 'registry.gear.ge.com/smartdigit/maven:3.5.3-jdk-8'
                    alwaysPull true
                    label 'dind'
                }
            }
            environment {
                MAVEN_SETTINGS = credentials('settings.xml')
                TEST_ENVIRONMENT = getPredixEnv(params.predixEnv)
                TEST_GROUP = getPredixEnv(params.testGroup)
            }
            steps {
                sh '''
                    mvn -s $MAVEN_SETTINGS clean -Dtest=RunCucumberTest test -Denv=${TEST_ENVIRONMENT} "-Dcucumber.options=--tags @${TEST_GROUP}"
                '''
            }
        }
    }
}