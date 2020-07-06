/*
 * Copyright 2020 Google LLC
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>https://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apigeesample;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.execution.spi.Execution;
import com.apigee.flow.message.MessageContext;
import com.google.apigee.calloutbaseclass.CalloutBase;

import java.util.Map;

/**
 * Sample Java Callout utilizing the setExceptionVariable and log methods of Java Callout Base
 * Class.
 */
public class BaseclassSampleDebugging extends CalloutBase implements Execution {

  public BaseclassSampleDebugging(Map<String, String> properties) {
    super(properties);
  }

  /**
   * The main function executed by the Java Callout. In this case, the function only logs a message
   * and throws an exception which are both stored in flow variables.
   *
   * @param messageContext Object allowing access entities inside the flow
   * @param executionContext Object allowing access to proxy execution context
   * @return Aborted execution result
   */
  public ExecutionResult execute(MessageContext messageContext, ExecutionContext executionContext) {
    try {
      log("Log statement before exception thrown", messageContext);
      throw new RuntimeException("test exception");
    } catch (Exception e) {
      setExceptionVariables(e, messageContext);
      return ExecutionResult.ABORT;
    }
  }
}
