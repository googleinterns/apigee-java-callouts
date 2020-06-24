# Apigee Java Callouts Base Class

This class contains common logic for use in creating Apigee Java Callouts.

For more information about Java Callouts, visit: https://docs.apigee.com/api-platform/reference/policies/java-callout-policy

## Usage

This github repository contains sample Java callout source code which shows how this base class can be used.

To use these functions, simply extend the base class in your Java callout.

## Method Summary

These are the methods currently included with this base class:


#### getOptionalProperty
&nbsp;&nbsp;&nbsp;&nbsp;Retrieves an optional property from properties map.
#### getRequiredProperty
&nbsp;&nbsp;&nbsp;&nbsp;Retrieves a required property from properties map.  
&nbsp;&nbsp;&nbsp;&nbsp;Throws an exception if the property does not exist or resolves to nothing.
#### resolveVariableReference
&nbsp;&nbsp;&nbsp;&nbsp;Resolves flow variable references to their actual values.  
&nbsp;&nbsp;&nbsp;&nbsp;e.g. {request.queryparam.message} is resolved to the value of the message  
&nbsp;&nbsp;&nbsp;&nbsp;query parameter.
#### setExceptionVariable
&nbsp;&nbsp;&nbsp;&nbsp;Stores exceptions, error codes and the exception stack trace in a flow variable.
#### log
&nbsp;&nbsp;&nbsp;&nbsp;Stores a log message along with context (line number, class, method) in a flow variable 
&nbsp;&nbsp;&nbsp;&nbsp;for debugging.



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


