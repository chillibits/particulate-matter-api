@echo off
call tar cvf secrets.tar src\main\java\com\chillibits\particulatematterapi\shared\Credentials.java src\main\resources\application.yml src\main\resources\pmapi.p12
call travis encrypt-file secrets.tar secrets.tar.enc --add --pro
