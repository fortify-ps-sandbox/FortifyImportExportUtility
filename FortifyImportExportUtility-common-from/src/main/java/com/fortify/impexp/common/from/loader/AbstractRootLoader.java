/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.impexp.common.from.loader;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.fortify.impexp.common.processor.ActiveProcessorsInvoker;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.util.spring.SpringExpressionUtil;
import com.fortify.util.spring.boot.scheduler.ISchedulableRunner;
import com.fortify.util.spring.expression.TemplateExpression;

public abstract class AbstractRootLoader<S> implements ISchedulableRunner {
	private final IEntitySourceDescriptor entitySourceDescriptor;
	@Autowired private ActiveProcessorsInvoker activeProcessors;
	
	protected AbstractRootLoader(IEntitySourceDescriptor entitySourceDescriptor) {
		this.entitySourceDescriptor = entitySourceDescriptor;
	}
	
	@Override
	public final void run() {
		activeProcessors.start(entitySourceDescriptor);
		supplyEntities(this::processEntity);
		activeProcessors.end(entitySourceDescriptor);
	}

	private final void processEntity(S entity) {
		activeProcessors.processWithProperties(entitySourceDescriptor, entity, getProperties(entity));
	}
	
	protected abstract void supplyEntities(Consumer<S> entityConsumer);
	
	protected Map<String, Object> getProperties(S entity) {
		return getPropertyTemplates(entity).entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> 
			SpringExpressionUtil.evaluateExpression(entity, e.getValue(), Object.class)));
	}
	
	protected Map<String, TemplateExpression> getPropertyTemplates(S entity) {
		// TODO default implementation: get directly from property
		return null;
	}
	
	@PostConstruct
	public final void logInitialized() {
		System.out.println("Initialized "+this);
	}
}
