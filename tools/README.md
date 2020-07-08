# Apigee Platform Samples Tools

### API Proxy Deploy Tool

>Note: Most of the samples use the Python-based deploy tool described below, and
you can use this tool for deploying proxies to Edge. However, be aware that
another deployment tool option exists:
[apigeetool](https://www.npmjs.com/package/apigeetool), a Node.js module for
deploying proxies and Node.js apps to Apigee Edge. For now, most of the examples
in `api-platform-samples` use the Python deploy tool. In the future, the samples
may be revised to use `apigeetool`. 

Requires [Python](http://python.org/getit/) to be installed. 

This tool provides a simple command for importing and deploying an API proxy
from your local machine to an environment on the Apigee API Platform.

You require an account in an organization at enterprise.apigee.com. [Register
for an account for free.](https://accounts.apigee.com/accounts/sign_up)

#### Usage

    deploy.py -n {apiName} -u {myname:mypass} -o {myorg} -e {environment} -p
    {basePath} -d {path to /apiproxy directory}

* `-n` The name of the API that will be created when this API proxy is uploaded
to Apigee.  The name will be the display name in the Apigee API Platform UI. If
the API already exists, then the deploy tool will import this as a new revision
of the existing API.

* `-u` Your username and password for your account on enterprise.apigee.com.
([Register for an account for 
free.](https://accounts.apigee.com/accounts/sign_up))

* `-o` The Apigee organization in which youhave an account. To obtain this
information, login to enterprise.apigee.com  and view account settings.

* `-e` The environment in your organization where you would like this API Proxy
to be  deployed. Note that this script will automatically import and deploy the
API proxy  so that it is immediately available.  Trial/cloud accounts be default
have two  environments: `test` and `prod`. Usually, you will set this to be the
`test` environment.

    To get a list of available environments: `curl -u myname:mypass
    https://api.enterprise.apigee.com/v1/o/{org_name}/environments/`

* `-p` The URI path used as a pattern match to route incoming message to this
API proxy deployment.  (Note that this is the deployment path. In most cases,
you can set this value to '/',  unless you have advanced deployment and routing
requirements. The primary path used for API proxy routing is defined in the API
proxy's ProxyEndpoint configuration.) 

* `-d` The path to the local directory where your API proxy files reside. Note
that your  API proxy files must be stored under a directory called `/apiproxy`.
This script will  ZIP your API proxy into a bundle, and import the bundle to
Apigee API Platform  environment for your organization. Note that this value
points to the directory that  contains the directory `/apiproxy`, and not to the
`/apiproxy` directory itself.

* `-h` **Optional** The base URL for the Apigee API. You do not need to specify
or modify this for cloud/trial accounts. Defaults to 
`https://api.enterprise.apigee.com`

---

Copyright © 2015 Apigee Corporation

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
