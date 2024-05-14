#!/bin/bash

echo "build ..."
if [ "local_plugin" = $1 ] ; then
  mvn clean install -DskipTests=true -pl sdkg-plugin,base-sdkg/base-sdkg-provider,base-sdkg/base-sdkg-sb2-test,base-sdkg/example-sdkg/example-sdkg-provider,base-sdkg/example-sdkg/example-sdkg-generator -am org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests=true
elif [ "publish_plugin" = $1 ] ; then
  mvn clean deploy -DskipTests=true -Prelease -pl sdkg-plugin,base-sdkg/base-sdkg-provider,base-sdkg/base-sdkg-sb2-test,base-sdkg/example-sdkg/example-sdkg-provider,base-sdkg/example-sdkg/example-sdkg-generator -am
elif [ "package_example" = $1 ] ; then
  mvn clean package -pl base-sdkg/example-sdkg/example-codeg/example-api -Dapigc.skip=false
  mvn clean package -pl base-sdkg/example-sdkg/example-codeg/example-mp-base -Dmpgc.skip=false
  mvn clean package -pl base-sdkg/example-sdkg/example-codeg/example-bundle -Dbdgc.skip=false
  mvn clean package -pl base-sdkg/example-sdkg/example-codeg/example-bundle-mock,base-sdkg/example-sdkg/example-codeg/example-mock-server -Dbdmgc.skip=false
elif [ "start_example_mock_server" = $1 ] ; then
  java -jar base-sdkg/example-sdkg/example-codeg/example-mock-server/target/example-mock-server.jar
fi
echo "build done."
