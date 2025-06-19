#!/bin/bash

echo "build ..."
if [ "local_plugin" = $1 ] ; then
  mvn clean install -DskipTests=true
elif [ "publish_plugin" = $1 ] ; then
  mvn -U clean deploy -Prelease -DskipTests=true
fi
echo "build done."
