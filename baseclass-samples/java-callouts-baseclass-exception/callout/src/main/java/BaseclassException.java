package com.apigeesample;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.execution.spi.Execution;
import com.apigee.flow.message.MessageContext;
import com.google.apigee.calloutbaseclass.CalloutBase;

import java.util.Map;

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
