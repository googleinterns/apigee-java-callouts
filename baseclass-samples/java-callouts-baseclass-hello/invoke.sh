#!/bin/bash

echo Using org and environment configured in /setup/setenv.sh

source ../../setup/setenv.sh

curl http://$org-$env.$api_domain/java-callouts-baseclass-hello?name="Alice"




