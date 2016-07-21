#!/bin/sh

dirname $0
cd `dirname $0`

mvn clean:clean
mvn -Dmaven.test.skip=true package