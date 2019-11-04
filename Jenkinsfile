#!/usr/bin/env groovy

pipeline {
    agent any

    environment {
        IMAGEM_DOCKER = 'dockerhub.camara.gov.br/sepop/servico-indexacao'
        APROVADORES = 'P_7192,P_7006,P_7274,P_7181,P_6992,P_6977,P_915179,P_999259'
        CANAL_CHATOPS = 'sepop'
        EMAIL_SECAO = 'sepop.cenin@camara.leg.br'
        URL_RANCHER = 'https://rancher.camara.gov.br/v2-beta'
        NOME_SERVICO = 'servico-indexacao/servico-indexacao'
    }

    tools {
        maven 'Maven 3.3.9'
    }

    stages {
        stage('Git checkout') {
            steps {
                checkout scm
                script {
                    VERSAO_POM = readMavenPom().getVersion()
                    DEPLOY_EM_PRODUCAO = false
                }
            }
        }
        stage('Compilação e testes') {
            steps {
                sh "mvn -B -U -e -V clean package"
            }
        }
        stage('Arquivar') {
            steps {
                junit '**/target/surefire-reports/TEST-*.xml'
                archive 'target/servico-indexacao.jar'
            }
        }
        stage('Imagem Docker') {
            when {
                branch 'master'
            }
            steps {
                echo "Versão do pom.xml: ${VERSAO_POM}"
                withDockerRegistry([credentialsId: 'c34117dc-5fa1-46f8-8ebb-f1cf0b2254c4', url: 'https://dockerhub.camara.gov.br/']) {
                    script {
                        imagem = docker.build("${IMAGEM_DOCKER}:${VERSAO_POM}")
                        imagem.push()
                    }
                }
            }
        }
        stage('Deploy Homologação') {
            when {
                branch 'master'
            }
            steps {
                rancher confirm: true, credentialId: 'rancher-server', endpoint: 'https://rancher.camara.gov.br/v2-beta',
                    environmentId: '1a408', environments: '', image: "${env.IMAGEM_DOCKER}:${VERSAO_POM}", ports: '',
                    service: "${env.NOME_SERVICO}", startFirst: true, timeout: 50

                rocketSend channel: "${env.CANAL_CHATOPS}", message: "Deploy feito no ambiente de homologação: ${currentBuild.absoluteUrl}"
            }
        }
        stage('Promover para Produção?') {
            agent none
            when {
                branch 'master'
                expression {
                    return ! VERSAO_POM.endsWith('SNAPSHOT')
                }
            }
            steps {
                timeout(10) {
                    rocketSend channel: "${env.CANAL_CHATOPS}", message: "Deploy em produção aguardando aprovação. Job: ${currentBuild.absoluteUrl}"
                    script {
                        aprovador = input message: 'ATENÇÃO! Deseja fazer o deploy em produção?', ok: 'Sim',
                        submitter: "${env.APROVADORES}", submitterParameter: 'aprovador'
                        echo "Deploy em produção aprovado por ${aprovador}"
                        DEPLOY_EM_PRODUCAO = true
                    }
                }
            }
        }
        stage('Git tag e Deploy em produção') {
            when {
                expression {
                    return DEPLOY_EM_PRODUCAO
                }
            }
            environment {
                NOME_TAG = "v${VERSAO_POM}"
            }
            steps {
                sshagent(['ssh_git']) {
                    sh "git config user.email \"${env.EMAIL_SECAO}\""
                    sh "git config user.name \"Jenkins\""
                    sh "git tag -a ${NOME_TAG} -m \"Tag criada pelo Jenkins\""
                    sh('git push --tags')
                }
                rancher confirm: true, credentialId: 'rancher-server', endpoint: "${env.URL_RANCHER}",
                    environmentId: '1a278', environments: '', image: "${env.IMAGEM_DOCKER}:${VERSAO_POM}", ports: '',
                    service: "${env.NOME_SERVICO}", startFirst: true, timeout: 50

                rocketSend channel: "${env.CANAL_CHATOPS}", message: "${env.IMAGEM_DOCKER}:${VERSAO_POM} enviado para ambiente de produção. Aprovado por ${aprovador}"

                mail to: "${env.EMAIL_SECAO},sesap.cenin@camara.leg.br",
                     subject: "Deploy em produção rancher - Job ${env.JOB_NAME}-${env.BUILD_DISPLAY_NAME}",
                     body: """
                              Foi feito deploy em produção da imagem ${env.IMAGEM_DOCKER}:${VERSAO_POM} no serviço ${env.NOME_SERVICO}.
                              Deploy aprovado por ${aprovador}
                              Url do job: ${currentBuild.absoluteUrl}"""
            }
        }
        stage('Atualização da versão no pom.xml') {
            when {
                expression {
                    return DEPLOY_EM_PRODUCAO
                }
            }
            steps {
                script {
                    def indiceUltimoPonto = VERSAO_POM.lastIndexOf('.')
                    def versaoMinor = VERSAO_POM.substring(indiceUltimoPonto + 1) as Integer
                    def proximaVersaoMinor = versaoMinor + 1
                    PROXIMA_VERSAO = VERSAO_POM.substring(0, indiceUltimoPonto + 1) + proximaVersaoMinor + '-SNAPSHOT'
                }

                echo "Atualizando pom.xml para versão ${PROXIMA_VERSAO}"
                sshagent(['ssh_git']) {
                    sh "git checkout master"
                    sh "git pull"
                    sh "mvn org.codehaus.mojo:versions-maven-plugin:2.5:set -DnewVersion=${PROXIMA_VERSAO} -DgenerateBackupPoms=false"
                    sh "git add pom.xml && git commit -m \"Versão do pom atualizada para próximo SNAPSHOT\""
                    sh "git push origin master"
                }
            }
        }
    }

    post {
        changed {
            mail body: """
                O job ${env.JOB_NAME} no branch ${env.BRANCH_NAME} mudou de status para: ${currentBuild.currentResult}

                ${currentBuild.absoluteUrl}
                """,
                subject: "Job ${env.JOB_NAME}-${env.BUILD_DISPLAY_NAME} - ${currentBuild.currentResult}",
                to: "${env.EMAIL_SECAO}"
        }
    }
}
