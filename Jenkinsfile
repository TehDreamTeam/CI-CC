pipeline {
  agent any
  stages {
    stage('Pre-Prepare Pipeline') {
      steps {
        script {          
          env.ProjectName = 'PROJECT_NAME_HERE'
          env.Organization = 'ORGANIZATION_NAME_HERE'
          env.AnalysisMode = 'publish'
          env.AdditionalSonarProperties = ' '
          
          env.BranchName = env.BRANCH_NAME
        }        
      }
    }
    
    stage('Prepare Pipeline') {
      parallel {
        stage('Parse Gradle settings') {
          steps {
            script {
              env.GRADLE_VERSION = sh(
                script: 'cat build.gradle | grep -m 1 version | awk \'{print $3}\' | tr -d \\\' | tr -d "\\n"',
                returnStdout: true
              )
            }
            
            script {
              env.GRADLE_JAVA_VERSION = sh(
                script: 'cat build.gradle | grep -m 1 targetCompatibility | awk \'{print $3}\' | tr -d \\\' | tr -d "\\n"',
                returnStdout: true
              )
            }
            
            script {
              env.GRADLE_MODULES = sh(
                script: 'cat settings.gradle | grep include | awk \'{printf "%s%s",separator,$2; separator=","}\' | tr -d \\\': | tr -d "\\n"',
                returnStdout: true
              )
            }
            
          }
        }
        
        stage('Prepare Code Analysis') {
          when {
                allOf {
                    expression { env.CHANGE_ID != null }
                    expression { env.CHANGE_TARGET != null }
                }
            }
          steps {
            script {
              echo 'I am a pull request.'
          
              env.BranchName = 'master'
              env.AnalysisMode = 'issues'
              env.IsAPullRequest = 'yes'

              env.AdditionalSonarProperties = "-Dsonar.analysis.mode=${AnalysisMode} " +
                "-Dsonar.sourceEncoding=UTF-8 " +
                "-Dsonar.github.repository=${Organization}/${ProjectName} " +
                "-Dsonar.github.login=\$USER " +
                "-Dsonar.github.oauth=\$TOKEN " +
                "-Dsonar.github.pullRequest=${CHANGE_ID}"
            }
          }
        }
      }
    }
    
    stage('Build & Test') {
      parallel {
        stage('Build & Test') {
          steps {
            script {
              sh 'gradle --no-daemon clean build integrationTest jacocoTestReport'
            }
            junit '**/build/test-reports/*.xml,**/build/integration-test-reports/*.xml'
          }
        }
        
        stage('Print environment') {
          steps {
            sh 'printenv'
          }
        }
      }
    }
   
    stage('Code Analysis') {
      steps {
        script {
          withCredentials([string(credentialsId: 'SONAR_GIT_TOKEN', variable: 'TOKEN'),
                          string(credentialsId: 'SONAR_GIT_USER', variable: 'USER')]) {
            withSonarQubeEnv('SonarQube - Infra') {
              sh "/opt/sonarscanner/bin/sonar-scanner " +
              "-Dsonar.projectKey=${ProjectName}:${BranchName} " +
              "-Dsonar.projectName='${ProjectName} (${BranchName})' " +
              "-Dsonar.projectVersion=${GRADLE_VERSION} " +
              "-Dsonar.language=java " +
              "-Dsonar.java.source=${GRADLE_JAVA_VERSION} " +

              "-Dsonar.modules=${GRADLE_MODULES} " +

              "-Dsonar.sources=. " +
              "-Dsonar.inclusions=src/main/** " +
              "-Dsonar.java.binaries=build/classes/java/main/** " +

              "-Dsonar.tests=. " +
              "-Dsonar.test.inclusions=src/test/**,src/integrationTest/** " +

              "-Dsonar.junit.reportPaths=build/test-reports,build/integration-test-reports " +
              "-Dsonar.jacoco.reportPaths=build/jacoco/test.exec,build/jacoco/integrationTest.exec " +

              env.AdditionalSonarProperties
            }
          }
        }
      }
    }
  }
}
