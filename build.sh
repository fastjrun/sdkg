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
elif [ "local_ci" = $1 ] ; then
    mvn clean install -pl sdkg-helper -am
    mvn clean install -pl sdkg-demo/demo-api -am -Dapigc.skip=false -Dmaven.test.skip=true
    mvn clean install -pl sdkg-demo/demo-base -am -Dbasegc.skip=false
    mvn clean install -pl sdkg-demo/demo-bundle -am -Dbdgc.skip=false
    mvn clean install -pl sdkg-demo/demo-bundle-mock -am -Dbdmgc.skip=false
fi
echo "build done."