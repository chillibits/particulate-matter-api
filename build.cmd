@echo off
call travis encrypt-file src/main/java/com/chillibits/particulatematterapi/shared/Credentials.java src/main/java/com/chillibits/particulatematterapi/shared/Credentials.java.enc --add --pro
call travis encrypt-file src/main/resources/application.yml src/main/resources/application.yml.enc --add --pro
call travis encrypt-file src/main/resources/pmapi.p12 src/main/resources/pmapi.p12.enc --add --pro