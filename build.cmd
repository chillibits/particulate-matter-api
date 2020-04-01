@echo off
travis encrypt-file src\main\java\com\chillibits\particulatematterapi\shared\Credentials.java --add \src\main\java\com\chillibits\particulatematterapi\shared\Credentials.java.enc --add

travis encrypt-file src\main\resources\application.yml src\main\resources\application.yml.enc

travis encrypt-file src\main\resources\pmapi.p12 src\main\resources\pmapi.p12.enc

exit