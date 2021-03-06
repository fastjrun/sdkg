#!/bin/bash

echo "build ..."
if [ "local_helper" = $1 ] ; then
    mvn clean install -pl
elif [ "publish_helper" = $1 ] ; then
    mvn -U clean deploy -Prelease
fi
echo "build done."