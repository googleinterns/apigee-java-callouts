# Apigee Java Callouts Base Class Sample - Exception

This sample is a simple proxy that uses a Java callout policy demonstrating usage of the Java Callout Base Class.

This sample proxy raises an exception that we store in a flow variable for further inspection. It also makes use of the `log` functionality to log a message which is also stored in a flow variable for debugging purposes.


## Initial Setup

An Apigee account is required to deploy these sample proxies. You can sign up for a free trial account [here.](https://accounts.apigee.com/accounts/sign_up)

You also need to have [Python](http://python.org/getit/) on your system to use the deployment tool.

The project is configured to be compiled using [Maven](https://maven.apache.org/download.cgi), but `javac` will also work.

To begin, checkout this project to your system.

Next, you must set up your deployment environment:

1. Open the file ../../setup/setenv.sh in a text editor.

2. Edit the file with your Apigee Edge account settings and save it. For example:

```
org=myorg
username=jdoe@example.com
url="https://api.enterprise.apigee.com"
env=test
api_domain="apigee.net"
```

## Deploying the proxy

1. `cd` into the `callout` directory and compile it using Maven with the command `mvn clean package`. This compiles the project and copies the jar file into `apiproxy/resources/java/`. If compiling using `javac`, you will need to move the jar file manually.
2. Execute the `./deploy.sh` script. Note you will need to have set up your `../../setup/setenv.sh` file first.
3. Once the deployment succeeds, you can call the proxy using `./invoke.sh`. 
4. The response should return an error, which is expected.

The `setExceptionVariable` method stores the exception, error and stacktrace into flow variables named `callout_exception`, `callout_error`, and `callout_stacktrace` respectively. You can inspect these values by downloading the trace on the Apigee API Proxies portal.

The `log` method allows us to store a message in the `callout_log` flow variable for debugging. This can also be seen in the trace and should contain our message `"Debug Statement 1"` along with the line number, class and method where the message was logged. Note that subsequent calls to `log` will not overwrite previous log messages and all will be saved to the same flow variable.

## License
Copyright 2020 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
