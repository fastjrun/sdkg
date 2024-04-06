#!/bin/bash

echo "build ..."
if [ "local_plugin" = $1 ] ; then
  mvn clean install -DskipTests=true -pl sdkg-plugin,base-sdkg/base-sdkg-provider,base-sdkg/base-sdkg-sb2-test,base-sdkg/example-sdkg/example-sdkg-provider,base-sdkg/example-sdkg/example-sdkg-generator -am org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests=true
elif [ "publish_plugin" = $1 ] ; then
  mvn clean deploy -DskipTests=true -Prelease -pl sdkg-plugin,base-sdkg/base-sdkg-provider,base-sdkg/base-sdkg-sb2-test,base-sdkg/example-sdkg/example-sdkg-provider,base-sdkg/example-sdkg/example-sdkg-generator -am
elif [ "package_example" = $1 ] ; then
  mvn compile -pl base-sdkg/example-sdkg/example-codeg/example-api -am -Dapigc.skip=false
  mvn compile -pl base-sdkg/example-sdkg/example-codeg/example-mp-base -am -Dmpgc.skip=false
  mvn compile -pl base-sdkg/example-sdkg/example-codeg/example-bundle -am -Dbdgc.skip=false
  mvn compile -pl base-sdkg/example-sdkg/example-codeg/example-bundle-mock -am -Dbdmgc.skip=false
  mvn package -pl base-sdkg/example-sdkg/example-codeg/example-api,base-sdkg/example-sdkg/example-codeg/example-mp-base,base-sdkg/example-sdkg/example-codeg/example-bundle-mock -am
fi
echo "build done."
