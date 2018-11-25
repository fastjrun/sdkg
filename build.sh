#!/bin/bash

echo "build ..."
if [ "local_helper" = $1 ] ; then
    mvn clean install -pl sdkg-helper -am
elif [ "publish_helper" = $1 ] ; then
    mvn -U clean deploy -pl sdkg-helper -am
elif [ "local_mock" = $1 ] ; then
    mvn clean package -pl sdkg-demo/demo-provider-mock -am -Dbdmgc.skip=false
    cp sdkg-demo/demo-provider-mock/target/demo-provider-mock.jar ~/output/demo-provider-mock.jar
elif [ "local_test" = $1 ] ; then
    mvn clean package -pl sdkg-demo/demo-api -Dapigc.skip=false
elif [ "local_app_provider" = $1 ] ; then
    mvn clean install -pl sdkg-demo/demo-base -am -Dbasegc.skip=false
    mvn clean install -pl sdkg-demo/demo-app-bundle,sdkg-demo/demo-service-impl,sdkg-demo/demo-app-provider -Dbdgc.skip=false
    cp sdkg-demo/demo-app-provider/target/demo-app-provider.jar ~/output/demo-app-provider.jar
elif [ "local_app_test" = $1 ] ; then
    mvn clean package -pl sdkg-demo/demo-app-api -Dapigc.skip=false
elif [ "local_ci" = $1 ] ; then
    mvn clean install -pl sdkg-helper -am
    mvn clean install -pl sdkg-demo/demo-api -Dapigc.skip=false -Dmaven.test.skip=true
    mvn clean install -pl sdkg-demo/demo-base -Dbasegc.skip=false
    mvn clean install -pl sdkg-demo/demo-bundle -Dbdgc.skip=false
    mvn clean install -pl sdkg-demo/demo-bundle-mock -Dbdmgc.skip=false
elif [ "service_ut" = $1 ] ; then
    mvn clean install -pl sdkg-demo/demo-base -am -Dbasegc.skip=false
    mvn clean install -pl sdkg-demo/demo-bundle -Dbdgc.skip=false
    mvn clean verify -pl sdkg-demo/demo-service-impl,sdkg-test,sdkg-demo/demo-unit-test -PunitTest
fi
echo "build done."