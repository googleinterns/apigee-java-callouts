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

package com.google.apigee.calloutbaseclass;

import com.apigee.flow.message.MessageContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Base Class for Apigee Java Callouts containing commonly used methods across callouts.
 */
public abstract class CalloutsBase {

  private static final String ERROR_FLOW_VARIABLE = "callout_error";
  private static final String EXCEPTION_FLOW_VARIABLE = "callout_exception";
  private static final String EXCEPTION_STACKTRACE_FLOW_VARIABLE = "callout_exception_stacktrace";
  private static final String LOG_FLOW_VARIABLE = "callout_log";

  // Matches common error strings (e.g. matches "example: error")
  private static final String commonErrorPatternString = "^(.+?)[:;] (.+)$";
  private static final Pattern commonErrorPattern = Pattern.compile(commonErrorPatternString);
  // Matches flow variable reference (e.g. matches "{request.test}")
  private static final String variableReferencePatternString =
      "(.*?)\\{([^\\{\\} :][^\\{\\} ]*?)\\}(.*?)";
  private static final Pattern variableReferencePattern =
      Pattern.compile(variableReferencePatternString);

  private final Map<String, String> properties;

  /**
   * Constructor taking in String key and value properties map
   *
   * @param properties String key and value map
   */
  public CalloutsBase(Map<String, String> properties) {
    this.properties = properties;
  }

  /**
   * Retrieves an optional property from properties map. Returns null if not found in properties map.
   *
   * @param propertyName Name of property to retrieve value
   * @param messageContext Message Context
   * @return Value of property in properties map
   */
  public String getOptionalProperty(String propertyName, MessageContext messageContext) {
    if (!this.properties.containsKey(propertyName)) {
      return null;
    }
    String value = resolveVariableReferences(this.properties.get(propertyName).trim(), messageContext);
    if ("".equals(value)) {
      return null;
    }
    return value;
  }

  /**
   * Retrieves a required property from properties map.
   *
   * @param propertyName Name of property to retrieve value
   * @param messageContext Message Context
   * @return Value of property in properties map
   * @throws IllegalArgumentException if the propertyName does not exist in properties map or resolves to an empty string.
   */
  public String getRequiredProperty(String propertyName, MessageContext messageContext) {
    if (!this.properties.containsKey(propertyName)) {
      throw new IllegalArgumentException(propertyName + " does not exist in properties");
    }
    String value = resolveVariableReferences(this.properties.get(propertyName).trim(), messageContext);
    if ("".equals(value)) {
      throw new IllegalArgumentException(propertyName + " resolves to an empty string");
    }
    return value;
  }

  /**
   * Resolves references to flow variables (i.e. variable references between two curly braces
   * {request.example}).
   *
   * @param spec The potential flow variable reference
   * @param messageContext Message Context
   * @return Resolved value of flow variable reference or the spec if not a reference
   */
  public String resolveVariableReferences(String spec, MessageContext messageContext) {
    Matcher matcher = variableReferencePattern.matcher(spec);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, "");
      sb.append(matcher.group(1));
      String ref = matcher.group(2);
      String[] parts = ref.split(":", 2);
      Object v = messageContext.getVariable(parts[0]);
      if (v != null) {
        sb.append((String) v);
      } else if (parts.length > 1) {
        sb.append(parts[1]);
      }
      sb.append(matcher.group(3));
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  /**
   * Sets exception, error and stacktrace flow variables.
   *
   * @param exception Exception thrown
   * @param messageContext Message Context
   */
  public void setExceptionVariables(Exception exception, MessageContext messageContext) {
    String error = exception.toString().replaceAll("\n", " ");
    messageContext.setVariable(EXCEPTION_FLOW_VARIABLE, error);
    String stackTrace = getStackTrace(exception);
    messageContext.setVariable(EXCEPTION_STACKTRACE_FLOW_VARIABLE, stackTrace);
    Matcher matcher = commonErrorPattern.matcher(error);
    if (matcher.matches()) {
      messageContext.setVariable(ERROR_FLOW_VARIABLE, matcher.group(2));
    } else {
      messageContext.setVariable(ERROR_FLOW_VARIABLE, error);
    }
  }

  /**
   * Prints statement and stores it in log flow variable.
   *
   * @param logStatement Message to print
   * @param messageContext Message Context
   */
  public void log(Object logStatement, MessageContext messageContext) {
    StackTraceElement stackTraceElement = getStackTraceElement();
    String context =
        stackTraceElement == null
            ? ""
            : String.format(
                "%s.%s[%s:%d]",
                stackTraceElement.getClassName(),
                stackTraceElement.getMethodName(),
                stackTraceElement.getFileName(),
                stackTraceElement.getLineNumber());
    String logMessage = logStatement + "\t\t" + context;
    System.out.println(logMessage);
    String prevLogs =
        messageContext.getVariable(LOG_FLOW_VARIABLE) == null
            ? ""
            : messageContext.getVariable(LOG_FLOW_VARIABLE) + "\n";
    messageContext.setVariable(LOG_FLOW_VARIABLE, prevLogs + logMessage);
  }

  /**
   * Retrieves current stack trace element.
   *
   * @return StackTraceElement of most recent method call.
   */
  private StackTraceElement getStackTraceElement() {
    for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
      if (!ste.getMethodName().equals("getStackTrace")
          && !ste.getMethodName().equals("getStackTraceElement")
          && !ste.getMethodName().equals("log")) {
        return ste;
      }
    }
    return null;
  }

  /**
   * Gets stack trace from throwable as String. Same as getStackTrace function from apache commons
   * ExceptionUtils, but this removes the need for import.
   *
   * @param throwable Throwable to extract stack trace from
   * @return stack trace generated by exception as string
   */
  private String getStackTrace(final Throwable throwable) {
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw, true);
    throwable.printStackTrace(pw);
    return sw.getBuffer().toString();
  }
}
