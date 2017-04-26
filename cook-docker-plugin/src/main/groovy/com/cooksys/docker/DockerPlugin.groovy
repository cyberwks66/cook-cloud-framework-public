package com.cooksys.docker

import com.bmuschko.gradle.docker.DockerExtension
import com.bmuschko.gradle.docker.DockerJavaApplicationPlugin
import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPullImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by ft4 on 2016-11-22.
 */
class DockerPlugin implements Plugin<Project> {

    void apply(Project project){

        project.configure(project) {
            project.apply plugin: DockerJavaApplicationPlugin
            project.apply plugin: DockerRemoteApiPlugin


            project.afterEvaluate {

                def dockerBaseImage = "${project.group}/${project.archivesBaseName}-base:${project.version}".toLowerCase()


                project.extensions.configure(DockerExtension) { ex ->

                    if(Os.isFamily(Os.FAMILY_UNIX)) {
                        ex.url = 'unix:///var/run/docker.sock'
                    }

                    ex.javaApplication {
                        baseImage = dockerBaseImage
                    }

                    if(project.hasProperty('dockerUser')) {
                        ex.registryCredentials {
                            username = project.dockerUser
                            password = project.dockerPass
                            email = project.dockerEmail
                        }
                    }
                }

                project.tasks.findByName('baseDevImage') ?:

                project.task(type: Dockerfile, 'baseDevImage') {
                    from 'openjdk:8-jre'
                }

                project.tasks.findByName('baseImage') ?:

                project.task(type: Dockerfile, 'baseImage') {
                    from 'openjdk:8-jre-alpine'
                    runCommand 'apk add --update bash && rm -rf /var/cache/apk/*'
                }


                def dockerPullImage = project.task(type: DockerPullImage, 'dockerPullImage') {

                    def createDockerfile = getDockerfile(project)

                    String image = createDockerfile.instructions
                            .first()
                            .command
                            .toString()

                    def split = image.split(':')

                    repository = split[0]
                    tag = split[1]

                }

                def buildBaseImage = project.task(type: DockerBuildImage, 'buildBaseImage') {

                    if(project.hasProperty('update')) {
                        dependsOn dockerPullImage
                    }

                    def createDockerfile = getDockerfile(project)

                    dependsOn createDockerfile
                    inputDir = createDockerfile.destFile.parentFile
                    tag = dockerBaseImage
                }

                project.tasks.getByName(DockerJavaApplicationPlugin.BUILD_IMAGE_TASK_NAME).dependsOn buildBaseImage
            }
        }

    }



    private Dockerfile getDockerfile(Project project) {

        if(project.hasProperty('dev')) {
            return project.tasks.getByName('baseDevImage') as Dockerfile
        } else {
            return project.tasks.getByName('baseImage') as Dockerfile
        }


    }

}
