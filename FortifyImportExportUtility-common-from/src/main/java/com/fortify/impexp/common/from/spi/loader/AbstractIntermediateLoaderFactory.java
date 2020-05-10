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
package com.fortify.impexp.common.from.spi.loader;

import javax.annotation.PostConstruct;

import com.fortify.impexp.common.processor.entity.IEntityDescriptor;
import com.fortify.impexp.common.processor.entity.IEntityType;
import com.fortify.impexp.common.processor.invoker.AbstractProcessorInvokerProcessorFactory;

public abstract class AbstractIntermediateLoaderFactory<E> extends AbstractProcessorInvokerProcessorFactory<E> {
	public AbstractIntermediateLoaderFactory() {
		setSupportedEntityTypes(getSupportedEntityTypes());
	}
	
	/**
	 * If no supported entity types are defined, then calls to {@link #isActive(IEntityDescriptor)}
	 * may result in endless loops:
	 * <ul>
	 *  <li>This factory indirectly calls {@link #isActive(IEntityDescriptor)} on all 
	 *      available processors to see whether they provide a valid target processor for our processor<li>
	 *  <li>However if we call {@link #isActive(IEntityDescriptor)} on any 
	 *      {@link AbstractIntermediateLoaderFactory} (either ourselves or any other {@link AbstractIntermediateLoaderFactory}
	 *      implementation), then that factory will eventually call {@link #isActive(IEntityDescriptor)}
	 *      on ourselves again, as we are listed as an available processor factory</li>
	 * </ul>
	 * 
	 * We assume that entity types are hierarchical in nature (for example application->release->vulnerability), so
	 * no endless loops are possible if each factory properly sets one or more supported entity types. If a loader 
	 * implementation doesn't specify an entity type then the loader will be disabled automatically by having this method 
	 * return false.
	 */
	@Override
	protected boolean isSupportedIfSupportedEntityTypesIsEmpty() {
		return false;
	}
	
	/**
	 * Each intermediate loader should only be invoked by loaders that have the same entity source; 
	 * if a loader implementation doesn't specify an entity source then it will be disabled automatically 
	 * by having this method return false.
	 */
	@Override
	protected boolean isSupportedIfSupportedEntitySourcesIsEmpty() {
		return false;
	}
	
	@Override
	public boolean isActive(IEntityDescriptor entityDescriptor) {
		return super.isActive(entityDescriptor) && hasEnabledProcessors(getEntityDescriptor());
	}
	
	protected abstract IEntityDescriptor getEntityDescriptor();
	protected abstract IEntityType[] getSupportedEntityTypes();
	
	@PostConstruct
	public final void logInitialized() {
		System.out.println("Initialized "+this);
	}
}
