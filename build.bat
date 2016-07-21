cd /d %~sdp0

call mvn clean:clean
call mvn -Dmaven.test.skip=true package
