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
package com.fortify.impexp.common.processor.invoker;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import com.fortify.impexp.common.processor.INotifyStartAndEnd;
import com.fortify.impexp.common.processor.IProcessor;
import com.fortify.impexp.common.processor.IProcessorFactory;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.util.spring.boot.env.ModifyablePropertySource;
import com.fortify.util.spring.expression.TemplateExpression;

/**
 * This class allows for invoking all active {@link IProcessor} instances 
 * for a given {@link IEntitySourceDescriptor}. An instance of this class 
 * is available as a singleton bean through the {@link Component} annotation. 
 * The actual {@link IProcessorFactory} instances are retrieved using an 
 * {@link ObjectFactory} though, in order to allow for properly scoped 
 * factories. {@link IProcessor} instances are invoked based on the ordering
 * specified by {@link IProcessorFactory#getOrder()}, allowing for 
 * {@link IProcessor} instances generated by an {@link IProcessorFactory} with 
 * lower order to execute before instances generated by an {@link IProcessorFactory}
 * with higher order.
 * 
 * @author Ruud Senden
 *
 */
@Component
public class ActiveProcessorsInvoker {
	@Autowired private ObjectFactory<Collection<IProcessorFactory<?>>> allProcessorFactoriesFactory;
	
	private Stream<IProcessorFactory<?>> getActiveProcessorFactoriesStream(final IEntitySourceDescriptor entitySourceDescriptor) {
		return allProcessorFactoriesFactory
				.getObject()
				.stream()
				.filter(factory->factory.isActive(entitySourceDescriptor))
				.sorted(new OrderComparator());
	}
	
	private final Stream<IProcessor<?>> getActiveProcessorsStream(final IEntitySourceDescriptor entitySourceDescriptor) {
		return getActiveProcessorFactoriesStream(entitySourceDescriptor)
				.map(factory->(IProcessor<?>)factory.getProcessor());
	}
	
	private final Stream<INotifyStartAndEnd> getNotifyStartAndStopProcessorsStream(final IEntitySourceDescriptor entitySourceDescriptor) {
		return getActiveProcessorsStream(entitySourceDescriptor)
				.filter(INotifyStartAndEnd.class::isInstance)
				.map(INotifyStartAndEnd.class::cast);
	}
	
	public <S> void start(IEntitySourceDescriptor entitySourceDescriptor) {
		getNotifyStartAndStopProcessorsStream(entitySourceDescriptor)
			.forEach(processor->processor.notifyStart(entitySourceDescriptor));
	}
	
	public <S> void processWithPropertyTemplates(IEntitySourceDescriptor entitySourceDescriptor, S entity, Map<String, TemplateExpression> propertyExpressions) {
		// TODO Evaluate template expressions on entity
		processWithProperties(entitySourceDescriptor, entity, null);
	}
	
	public <S> void processWithProperties(IEntitySourceDescriptor entitySourceDescriptor, S entity, Map<String,Object> properties) {
		String previousScopeId = ModifyablePropertySource.getCurrentScopeId();
		try ( ModifyablePropertySource mps = ModifyablePropertySource.withProperties(properties) ) {
			String currentScopeId = ModifyablePropertySource.getCurrentScopeId();
			boolean isSameScope = currentScopeId.equals(previousScopeId);
			getActiveProcessorsStream(entitySourceDescriptor)
				// This cast should be safe based on IEntitySourceDescriptor#getJavaType()
				.forEach(processor->process(entitySourceDescriptor, entity, processor, isSameScope));
		}
	}

	@SuppressWarnings("unchecked")
	private <S> void process(IEntitySourceDescriptor entitySourceDescriptor, S entity, IProcessor<?> processor, boolean isSameScope) {
		if ( !isSameScope && processor instanceof INotifyStartAndEnd) {
			throw new IllegalStateException(String.format("%s does not support parent processor to specify customized properties", processor));
		}
		((IProcessor<S>)processor).process(entitySourceDescriptor, entity);
	}
	
	public <S> void end(IEntitySourceDescriptor entitySourceDescriptor) {
		getNotifyStartAndStopProcessorsStream(entitySourceDescriptor)
		.forEach(processor->processor.notifyEnd(entitySourceDescriptor));
	}
}
