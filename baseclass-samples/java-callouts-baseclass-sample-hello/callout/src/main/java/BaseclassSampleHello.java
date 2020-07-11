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
 * Sample Java Callout utilizing the {@link CalloutBase#getOptionalProperty(String, MessageContext)}
 * and {@link CalloutBase#getRequiredProperty(String, MessageContext)} methods of Java Callout Base
 * Class.
 */
public class BaseclassSampleHello extends CalloutBase implements Execution {

  public BaseclassSampleHello(Map<String, String> properties) {
    super(properties);
  }

  /**
   * The main function executed by the Java Callout. In this case, the function uses the properties
   * map to construct a message and returns that string.
   *
   * @param messageContext Object allowing access to entities inside the flow
   * @param executionContext Object allowing access to proxy execution context
   * @return A message string constructed using the properties map
   */
  public ExecutionResult execute(MessageContext messageContext, ExecutionContext executionContext) {
    try {
      messageContext.getMessage().setContent(constructMessage(messageContext));
      return ExecutionResult.SUCCESS;
    } catch (Exception e) {
      setExceptionVariables(e, messageContext);
      return ExecutionResult.ABORT;
    }
  }

  /**
   * Creates a message string using the name and message property values.
   *
   * @param messageContext Object allowing access to entities inside the flow
   * @return A string consisting of "Hi" followed by the name and message values from the properties
   *     map
   */
  private String constructMessage(MessageContext messageContext) {
    String name = getOptionalProperty("name", messageContext).orElse("Stranger");
    String message = getRequiredProperty("message", messageContext);

    return String.format("Hi %s! %s", name, message);
  }
}
