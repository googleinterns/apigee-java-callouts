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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CalloutBaseTest {

  private static final String ERROR_FLOW_VARIABLE = "callout_error";
  private static final String EXCEPTION_FLOW_VARIABLE = "callout_exception";
  private static final String EXCEPTION_STACKTRACE_FLOW_VARIABLE = "callout_exception_stacktrace";
  private static final String LOG_FLOW_VARIABLE = "callout_log";
  private static final String NONEXISTENT_VARIABLE_REFERENCE = "{fake.variable}";
  private static final String OPTIONAL_VARIABLE_KEY = "optionalVar";
  private static final String OPTIONAL_VARIABLE_VALUE = "optionalValue";
  private static final String OPTIONAL_VARIABLE_EMPTY_VALUE_KEY = "optionalVarEmpty";
  private static final String REQUIRED_VARIABLE_KEY = "requiredVar";
  private static final String REQUIRED_VARIABLE_VALUE = "requiredValue";
  private static final String REQUIRED_VARIABLE_EMPTY_VALUE_KEY = "requiredVarEmpty";
  private static final String TEST_FLOW_VALUE = "test123";
  private static final String TEST_FLOW_VARIABLE = "request.queryparam.testFlowVar";
  private static final String TEST_FLOW_VARIABLE_REFERENCE = "{request.queryparam.testFlowVar}";
  private static final String TEST_LOG_STATEMENT = "asdf123";
  private static final String TEST_LOG_STATEMENT2 = "defgh5678";
  @Spy FakeMessageContext messageContext;
  private CalloutBaseTestImpl calloutsBase;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    Map<String, String> properties = new HashMap<>();
    properties.put(OPTIONAL_VARIABLE_KEY, OPTIONAL_VARIABLE_VALUE);
    properties.put(OPTIONAL_VARIABLE_EMPTY_VALUE_KEY, "");
    properties.put(REQUIRED_VARIABLE_KEY, REQUIRED_VARIABLE_VALUE);
    properties.put(REQUIRED_VARIABLE_EMPTY_VALUE_KEY, "");
    calloutsBase = new CalloutBaseTestImpl(properties);

    messageContext.getVariables().clear();
    messageContext.setVariable(TEST_FLOW_VARIABLE, TEST_FLOW_VALUE);
  }

  @Test
  public void testGetOptionalProperty() {
    Optional<String> prop = calloutsBase.getOptionalProperty(OPTIONAL_VARIABLE_KEY, messageContext);

    Assert.assertTrue(prop.isPresent());
    Assert.assertEquals(Optional.of(OPTIONAL_VARIABLE_VALUE), prop);
  }

  @Test
  public void testGetOptionalPropertyThatDoesNotExist() {
    Optional<String> prop = calloutsBase.getOptionalProperty("", messageContext);

    Assert.assertTrue(prop.isEmpty());
  }

  @Test
  public void testGetOptionalPropertyEmptyString() {
    Optional<String> prop =
        calloutsBase.getOptionalProperty(OPTIONAL_VARIABLE_EMPTY_VALUE_KEY, messageContext);

    Assert.assertTrue(prop.isEmpty());
  }

  @Test
  public void testGetRequiredProperty() {
    String actual = calloutsBase.getRequiredProperty(REQUIRED_VARIABLE_KEY, messageContext);

    Assert.assertEquals(REQUIRED_VARIABLE_VALUE, actual);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetRequiredPropertyEmptyString() {
    calloutsBase.getRequiredProperty(REQUIRED_VARIABLE_EMPTY_VALUE_KEY, messageContext);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetRequiredPropertyThatDoesNotExist() {
    calloutsBase.getRequiredProperty("", messageContext);
  }

  @Test
  public void testResolveVariableReferences() {
    String expected = (String) messageContext.getVariable(TEST_FLOW_VARIABLE);
    String actual =
        calloutsBase.resolveVariableReferences(TEST_FLOW_VARIABLE_REFERENCE, messageContext);

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testResolveVariableReferenceNonExistentReference() {
    String actual =
        calloutsBase.resolveVariableReferences(NONEXISTENT_VARIABLE_REFERENCE, messageContext);

    Assert.assertEquals("", actual);
  }

  @Test
  public void testResolveVariableReferenceNotAReference() {
    String actual = calloutsBase.resolveVariableReferences(TEST_FLOW_VARIABLE, messageContext);

    Assert.assertEquals(TEST_FLOW_VARIABLE, actual);
  }

  @Test
  public void testSetExceptionVariables() {
    String expected_exception;
    String expected_error;
    String expected_stacktrace;

    try {
      throw new Exception("test");
    } catch (Exception exception) {
      expected_exception = exception.toString().replaceAll("\n", " ");
      expected_error = "test";
      final StringWriter sw = new StringWriter();
      final PrintWriter pw = new PrintWriter(sw, true);
      exception.printStackTrace(pw);
      expected_stacktrace = sw.getBuffer().toString();
      calloutsBase.setExceptionVariables(exception, messageContext);
    }

    String actual_exception = (String) messageContext.getVariable(EXCEPTION_FLOW_VARIABLE);
    String actual_error = (String) messageContext.getVariable(ERROR_FLOW_VARIABLE);
    String actual_stacktrace =
        (String) messageContext.getVariable(EXCEPTION_STACKTRACE_FLOW_VARIABLE);

    Assert.assertEquals(expected_exception, actual_exception);
    Assert.assertEquals(expected_error, actual_error);
    Assert.assertEquals(expected_stacktrace, actual_stacktrace);
  }

  @Test
  public void testLog() {
    calloutsBase.log(TEST_LOG_STATEMENT, messageContext);
    StackTraceElement ste = Thread.currentThread().getStackTrace()[1];
    String context =
        String.format(
            "%s.%s[%s:%d]",
            ste.getClassName(), ste.getMethodName(), ste.getFileName(), ste.getLineNumber() - 1);
    String expected = TEST_LOG_STATEMENT + "\t\t" + context;

    Assert.assertEquals(expected, messageContext.getVariable(LOG_FLOW_VARIABLE));
  }

  @Test
  public void testMultipleLogStatements() {
    calloutsBase.log(TEST_LOG_STATEMENT, messageContext);
    calloutsBase.log(TEST_LOG_STATEMENT2, messageContext);
    StackTraceElement ste = Thread.currentThread().getStackTrace()[1];
    String context1 =
        String.format(
            "%s.%s[%s:%d]",
            ste.getClassName(), ste.getMethodName(), ste.getFileName(), ste.getLineNumber() - 2);
    String context2 =
        String.format(
            "%s.%s[%s:%d]",
            ste.getClassName(), ste.getMethodName(), ste.getFileName(), ste.getLineNumber() - 1);
    String expected =
        String.format(
            "%s\t\t%s\n%s\t\t%s", TEST_LOG_STATEMENT, context1, TEST_LOG_STATEMENT2, context2);

    Assert.assertEquals(expected, messageContext.getVariable(LOG_FLOW_VARIABLE));
  }

  private static class CalloutBaseTestImpl extends CalloutBase {
    public CalloutBaseTestImpl(Map properties) {
      super(properties);
    }
  }

  abstract static class FakeMessageContext implements MessageContext {
    private Map<String, Object> variables;

    private Map<String, Object> getVariables() {
      if (variables == null) {
        variables = new HashMap<>();
      }
      return variables;
    }

    @Override
    public Object getVariable(final String name) {
      return getVariables().get(name);
    }

    @Override
    public boolean setVariable(final String name, final Object value) {
      getVariables().put(name, value);
      return true;
    }

    @Override
    public boolean removeVariable(final String name) {
      if (getVariables().containsKey(name)) {
        variables.remove(name);
      }
      return true;
    }
  }
}
