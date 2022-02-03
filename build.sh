#!/bin/bash

echo "build ..."
if [ "local_plugin" = $1 ] ; then
  mvn clean install -pl sdkg-plugin,base-sdkg/base-sdkg-client,base-sdkg/base-sdkg-provider,base-sdkg/base-sdkg-sb2-test,base-sdkg/example-sdkg/example-sdkg-client,base-sdkg/example-sdkg/example-sdkg-provider,base-sdkg/example-sdkg/example-sdkg-generator,base-sdkg/eladmin-sdkg/eladmin-sdkg-client,base-sdkg/eladmin-sdkg/eladmin-sdkg-provider,base-sdkg/eladmin-sdkg/eladmin-sdkg-generator -am
elif [ "publish_plugin" = $1 ] ; then
  mvn clean deploy -Prelease -pl sdkg-plugin,base-sdkg/base-sdkg-client,base-sdkg/base-sdkg-provider,base-sdkg/base-sdkg-sb2-test,base-sdkg/example-sdkg/example-sdkg-client,base-sdkg/example-sdkg/example-sdkg-provider,base-sdkg/example-sdkg/example-sdkg-generator,base-sdkg/eladmin-sdkg/eladmin-sdkg-client,base-sdkg/eladmin-sdkg/eladmin-sdkg-provider,base-sdkg/eladmin-sdkg/eladmin-sdkg-generator -am
elif [ "package_example" = $1 ] ; then
  mvn compile -pl base-sdkg/example-sdkg/example-codeg/example-api -am -Dapigc.skip=false
  mvn compile -pl base-sdkg/example-sdkg/example-codeg/example-bundle -am -Dbdgc.skip=false
  mvn compile -pl base-sdkg/example-sdkg/example-codeg/example-bundle-mock -am -Dbdmgc.skip=false
  mvn compile -pl base-sdkg/example-sdkg/example-codeg/example-client -am -Dclientgc.skip=false
  mvn package -pl base-sdkg/example-sdkg/example-codeg/example-api,base-sdkg/example-sdkg/example-codeg/example-bundle,base-sdkg/example-sdkg/example-codeg/example-bundle-mock,base-sdkg/example-sdkg/example-codeg/example-client -am
elif [ "package_eladmin" = $1 ] ; then
  mvn compile -pl base-sdkg/eladmin-sdkg/eladmin-codeg/eladmin-api -am -Dapigc.skip=false
  mvn compile -pl base-sdkg/eladmin-sdkg/eladmin-codeg/eladmin-bundle -am -Dbdgc.skip=false
  mvn compile -pl base-sdkg/eladmin-sdkg/eladmin-codeg/eladmin-bundle-mock -am -Dbdmgc.skip=false
  mvn compile -pl base-sdkg/eladmin-sdkg/eladmin-codeg/eladmin-client -am -Dclientgc.skip=false
  mvn package -pl base-sdkg/eladmin-sdkg/eladmin-codeg/eladmin-api,base-sdkg/eladmin-sdkg/eladmin-codeg/eladmin-bundle,base-sdkg/eladmin-sdkg/eladmin-codeg/eladmin-bundle-mock,base-sdkg/eladmin-sdkg/eladmin-codeg/eladmin-client -am
fi
echo "build done."
