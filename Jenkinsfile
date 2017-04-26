node {
  stage('Checkout') {
      git 'ssh://git@github.com/cooksystemsinc/cook-cloud-framework.git'
  }
 
  stage('Build Docker containers') {
    sh("gradle buildDocker")
  }
 
  stage ('Docker push') {
    docker.withRegistry('https://302733331795.dkr.ecr.us-west-2.amazonaws.com', 'ecr:us-west-2:JenkinsAWS') {
        docker.image('configuration').push('latest')
        docker.image('router').push('latest')
        docker.image('discovery').push('latest')
        docker.image('cloud-manager').push('latest')
//        docker.image('node-manager').push('latest')
        docker.image('turbine').push('latest')
        docker.image('airlines').push('latest')
        // docker.image('greeting-service').push('latest')
        docker.image('hello-world').push('latest')
        // docker.image('persons-service').push('latest')
    }
  }
}
