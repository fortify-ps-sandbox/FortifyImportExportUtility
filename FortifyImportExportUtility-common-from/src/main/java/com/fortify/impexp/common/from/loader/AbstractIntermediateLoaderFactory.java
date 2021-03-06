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

import javax.annotation.PostConstruct;

import org.springframework.core.Ordered;

import com.fortify.impexp.common.processor.AbstractProcessorFactory;
import com.fortify.impexp.common.processor.entity.source.SupportedEntitySourceDescriptorHelper;

public abstract class AbstractIntermediateLoaderFactory<S> extends AbstractProcessorFactory<S> {
	
	public AbstractIntermediateLoaderFactory(SupportedEntitySourceDescriptorHelper supportedEntitySourceDescriptorHelper) {
		super(supportedEntitySourceDescriptorHelper);
	}

	/**
	 * By default, this method returns {@link Ordered#LOWEST_PRECEDENCE} to allow target processors
	 * to execute before intermediate loaders/processors. For example, this allows a target to 
	 * export/create a release before exporting/creating individual vulnerabilities for that release.
	 */
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
	
	@PostConstruct
	public final void logInitialized() {
		System.out.println("Initialized "+this);
	}
}
