package com.apigeesample;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.execution.spi.Execution;
import com.apigee.flow.message.MessageContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class CalloutBase {

    private static final String ERROR_FLOW_VARIABLE = "callout_error";
    private static final String EXCEPTION_FLOW_VARIABLE = "callout_exception";
    private static final String EXCEPTION_STACKTRACE_FLOW_VARIABLE = "callout_exception_stacktrace";
    private static final String LOG_FLOW_VARIABLE = "callout_log";

    // Matches two words separated by a colon or semicolon and a space, intended for common error
    // strings.
    // (e.g. matches "example: error" or "example; error")
    private static final String COMMON_ERROR_PATTERN_STRING = "^(.+?)[:;] (.+)$";
    private static final Pattern commonErrorPattern = Pattern.compile(COMMON_ERROR_PATTERN_STRING);
    // Matches any string between two curly braces, intended for flow variable references
    // (e.g. matches "{request.test}", but also matches "{asdf}")
    private static final String VARIABLE_REFERENCE_PATTERN_STRING =
            "(.*?)\\{([^\\{\\} :][^\\{\\} ]*?)\\}(.*?)";
    private static final Pattern variableReferencePattern =
            Pattern.compile(VARIABLE_REFERENCE_PATTERN_STRING);

    private final Map<String, String> properties;

    /**
     * Constructor taking in String key and value properties map
     *
     * @param properties String key and value map
     */
    public CalloutBase(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Retrieves an optional String property from properties map or empty if property does not exist
     * in properties.
     *
     * @param propertyName Name of property to retrieve value
     * @param messageContext Message Context
     * @return Optional of property value in properties map or empty
     */
    public Optional<String> getOptionalProperty(String propertyName, MessageContext messageContext) {
        if (!this.properties.containsKey(propertyName)) {
            return Optional.empty();
        }
        String value =
                resolveVariableReferences(this.properties.get(propertyName).trim(), messageContext);
        if ("".equals(value)) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    /**
     * Retrieves a required property from properties map.
     *
     * @param propertyName Name of property to retrieve value
     * @param messageContext Message Context
     * @return Value of property in properties map
     * @throws IllegalArgumentException if the propertyName does not exist in properties map or
     *     resolves to an empty string.
     */
    public String getRequiredProperty(String propertyName, MessageContext messageContext) {
        return getOptionalProperty(propertyName, messageContext)
                .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * Resolves references to flow variables (i.e. variable references between two curly braces
     * {request.example}) or the spec if not a variable reference.
     *
     * @param spec The potential flow variable reference
     * @param messageContext Message Context
     * @return Resolved flow variable reference or spec
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
            // If flow variable does not exist in messageContext, a default value can be specified using a
            // colon. (e.g. "{flow.variable:default}" will use "default" as the value if "flow.variable"
            // is not found)
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
     * Stores statement in log flow variable.
     *
     * @param logStatement Message to log
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
            if (!"getStackTrace".equals(ste.getMethodName())
                    && !"getStackTraceElement".equals(ste.getMethodName())
                    && !"log".equals(ste.getMethodName())) {
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

public class BaseclassException extends CalloutBase implements Execution {

    public BaseclassException(Map<String, String> properties) {
        super(properties);
    }

    public ExecutionResult execute(MessageContext messageContext, ExecutionContext executionContext) {
		try {
		    log("Debug Statement 1", messageContext);
		    throw new RuntimeException("test exception");
		} catch (Exception e) {
		    setExceptionVariables(e, messageContext);
			return ExecutionResult.ABORT;
		}
	}
}
