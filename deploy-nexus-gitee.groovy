node {
    stage('git chekout') {
        git branch: "master", url: 'https://gitee.com/fastjrun/sdkg.git'
    }
    stage('mvn deploy') {
        sh 'sh build.sh publish_plugin'
    }
}
