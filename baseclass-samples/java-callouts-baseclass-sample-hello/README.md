# Apigee Java Callouts Base Class Sample - Hello

This sample is a simple proxy that uses a Java callout policy demonstrating
usage of the Java Callout Base Class.

This sample proxy produces a message using the given `"name"` and `"message"`
properties. Take a look at the Java source code to see how we get these property
values. 

Note that since `"name"` is an optional property, the proxy will still work
without a `"name"` value whereas the `"message"` is required and will throw an
exception if it is missing. 

Also note that the initial value for `"name"` is a flow variable reference and
that the base class allows us to automatically resolve this reference. 

## Initial Setup

An Apigee account is required to deploy these sample proxies. You can sign up
for a free trial account [here.](https://accounts.apigee.com/accounts/sign_up)

You also need to have [Python](http://python.org/getit/) on your system to use
the deployment tool.

The project is configured to be compiled using
[Maven](https://maven.apache.org/download.cgi), but `javac` will also work.

To begin, checkout this project to your system.

Next, you must set up your deployment environment:

1. Open the file `../../setup/setenv.sh` in a text editor.

2. Edit the file with your Apigee Edge account settings and save it. For
example:

```
org=myorg
username=jdoe@example.com
url="https://api.enterprise.apigee.com"
env=test
api_domain="apigee.net"
```

## Deploying the proxy

1. `cd` into the `callout` directory and compile it using Maven with the command
`mvn clean package`. This compiles the project and copies the jar file into
`apiproxy/resources/java/`. If compiling using `javac`, you will need to move
the jar file manually. 

2. Execute the `./deploy.sh` script. Note you will need
to have set up your `../../setup/setenv.sh` file first. 

3. Once the deployment
succeeds, you can call the proxy using `./invoke.sh`.  

4. The response should
look like this: `Hi Alice! Happy Birthday`.

## Further Details

The name `Alice` is taken from the `"name"` query parameter. You can change this
in `invoke.sh` to something else or even remove the query parameter entirely.
Inside the proxy properties fields the value for `"name"` is actually
`{request.queryparam.name}` which is a flow variable reference that our callout
is able to resolve. 

The other property we have is the `"message"` which contains `Happy Birthday`.
You can change this in
`apiproxy/policies/java-callouts-baseclass-sample-hello.xml` to modify the
response.

## License Copyright 2020 Google LLC

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.