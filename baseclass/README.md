# Apigee Java Callouts Base Class

This class contains common logic for use in creating Apigee Java Callouts.

For more information about Java Callouts, visit:
https://docs.apigee.com/api-platform/reference/policies/java-callout-policy

## Usage

This github repository contains the baseclass folder which houses the callout
baseclass source code in addition to the baseclass-samples folder which
houses usage examples for the baseclass.

To use these functions, simply extend the base class in your Java callout.

## Method Summary

These are the methods currently included with this base class:

#### getOptionalProperty
  Retrieves an optional property from properties map.
#### getRequiredProperty
  Retrieves a required property from properties map.  
  Throws an exception if the property does not exist or resolves to nothing.
#### resolveVariableReference
  Resolves flow variable references to their actual values.  
  e.g. {request.queryparam.message} is resolved to the value of the message  
  query parameter.
#### setExceptionVariable
  Stores exceptions, error codes and the exception stack trace in a flow  
  variable.
#### log
  Stores a log message along with context (line number, class, method) in a  
  flow variable for debugging.

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