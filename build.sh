#!/bin/bash

echo "build ..."
if [ "local_helper" = $1 ] ; then
    mvn clean install -pl sdkg-helper -am
elif [ "publish_helper" = $1 ] ; then
    mvn -U clean deploy -pl sdkg-helper -am
fi
echo "build done."