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
package com.fortify.impexp.common.processor.retriever;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fortify.impexp.common.processor.IProcessor;
import com.fortify.impexp.common.processor.IProcessorFactory;
import com.fortify.impexp.common.processor.entity.IEntityDescriptor;

@Component
public class ActiveProcessorsRetriever {
	@Autowired private Collection<IProcessorFactory<?>> allProcessorFactories;
	
	public final Collection<IProcessor<?>> getActiveProcessors(final IEntityDescriptor entityDescriptor) {
		return getActiveProcessorsStream(entityDescriptor)
				.map(factory->(IProcessor<?>)factory.getProcessor())
				.collect(Collectors.toList());
	}

	private Stream<IProcessorFactory<?>> getActiveProcessorsStream(final IEntityDescriptor entityDescriptor) {
		return allProcessorFactories
				.stream()
				.filter(factory->factory.isActive(entityDescriptor));
	}
	
	public final boolean hasActiveProcessors(final IEntityDescriptor entityDescriptor) {
		return getActiveProcessorsStream(entityDescriptor).findAny().isPresent();
	}
}
