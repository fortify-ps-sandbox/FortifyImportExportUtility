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

import com.fortify.impexp.common.processor.invoker.AbstractProcessorInvokerFactory;
import com.fortify.impexp.common.processor.selector.IProcessorSelector;
import com.fortify.util.spring.boot.scheduler.ISchedulableRunner;
import com.fortify.util.spring.boot.scheduler.ISchedulableRunnerFactory;

public abstract class AbstractRootLoaderFactory<R extends ISchedulableRunner> extends AbstractProcessorInvokerFactory implements ISchedulableRunnerFactory {
	@Override
	public boolean isEnabled() {
		// TODO Check for enabled configuration properties
		return hasEnabledProcessors(getTargetProcessorSelector());
	}
	
	@Override
	public final ISchedulableRunner getRunner() {
		return getRootLoader();
	}
	
	protected abstract R getRootLoader();

	protected abstract IProcessorSelector getTargetProcessorSelector();
	
}
