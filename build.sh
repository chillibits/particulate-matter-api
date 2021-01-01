#!/bin/bash

#
# Copyright © Marc Auberer 2019-2021. All rights reserved
#

# Secret encryption for GitHub Actions
gpg --symmetric --cipher-algo AES256 secrets.tar