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
package com.fortify.impexp.source.common.spi.loader;

import com.fortify.impexp.common.processor.invoker.AbstractProcessorInvokerProcessorFactory;
import com.fortify.impexp.common.processor.selector.IProcessorSelector;
import com.fortify.impexp.common.processor.selector.ISourceEntity;

public abstract class AbstractIntermediateLoaderFactory<I> extends AbstractProcessorInvokerProcessorFactory<I> {
	public AbstractIntermediateLoaderFactory() {
		setSupportedSourceEntities(getParentSourceEntities());
	}
	
	/**
	 * If no supported source entities are defined, then calls to {@link #isEnabled(IProcessorSelector)}
	 * may result in endless loops:
	 * <ul>
	 *  <li>This factory indirectly calls {@link #isEnabled(IProcessorSelector)} on all 
	 *      available processors to see whether they provide a valid target processor for our processor<li>
	 *  <li>However if we call {@link #isEnabled(IProcessorSelector)} on any 
	 *      {@link AbstractIntermediateLoaderFactory} (either ourselves or any other {@link AbstractIntermediateLoaderFactory}
	 *      implementation), then this factory will eventually call {@link #isEnabled(IProcessorSelector)}
	 *      on ourselves again, as we are listed as an available processor factory</li>
	 * </ul>
	 * 
	 * We assume that source entities are hierarchical in nature (i.e. application->release->vulnerability), so
	 * no endless loops are possible if each factory properly sets one or more parent entity types. If a loader 
	 * implementation doesn't specify a source entity then it will be disabled automatically by having this method 
	 * return false.
	 */
	@Override
	protected boolean isEnabledIfSupportedSourceEntitiesIsEmpty() {
		return false;
	}
	
	/**
	 * Each intermediate loader should only be invoked by other loaders from the same source 
	 * system; if a loader implementation doesn't specify a source system then it will be disabled
	 * automatically by having this method return false.
	 */
	@Override
	protected boolean isEnabledIfSupportedSourceSystemsIsEmpty() {
		return false;
	}
	
	@Override
	public boolean isEnabled(IProcessorSelector processorSelector) {
		return super.isEnabled(processorSelector) && hasEnabledProcessors(getTargetProcessorSelector());
	}
	
	protected abstract IProcessorSelector getTargetProcessorSelector();
	protected abstract ISourceEntity[] getParentSourceEntities();
}
