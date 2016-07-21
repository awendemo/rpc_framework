#!/bin/sh

dirname $0
cd `dirname $0`

mvn clean:clean
mvn idea:idea
