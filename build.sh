#!/bin/sh

cd /Users/john/URLyBird

rm -rf classes

mkdir -p classes

cd src

javac -d ../classes suncertify/db/*.java

javac -d ../classes suncertify/network/*.java

javac -d ../classes suncertify/presentation/*.java

javac -d ../classes suncertify/application/*.java

cd ../

touch MANIFEST.mf

echo 'Manifest-Version: 1.0' > MANIFEST.mf

echo 'Main-Class: suncertify.application.Application' >> MANIFEST.mf

cd classes

rmic suncertify.network.DataRemoteAdapterImpl

cd ../

jar -cfm runme.jar MANIFEST.mf -C classes .

