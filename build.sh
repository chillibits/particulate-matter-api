#!/bin/bash

# Secret encryption for GitHub Actions
gpg --symmetric --cipher-algo AES256 secrets.tar