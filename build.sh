#!/bin/bash
# Secret encryption for Travis
tar cvf secrets.tar src/main/java/com/chillibits/particulatematterapi/shared/Credentials.java src/main/resources/application.yml src/main/resources/pmapi.p12
travis encrypt-file secrets.tar secrets.tar.enc --add --pro
# Secret encryption for GitHub Actions
gpg --symmetric --cipher-algo AES256 secrets.tar