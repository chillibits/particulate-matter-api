#!/bin/bash
# Secret encryption for Travis
tar cvf secrets.tar src/main/resources/pmapi.p12
# Secret encryption for GitHub Actions
gpg --symmetric --cipher-algo AES256 secrets.tar