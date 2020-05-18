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

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;

import com.fortify.util.spring.boot.scheduler.ISchedulableRunner;
import com.fortify.util.spring.boot.scheduler.ISchedulableRunnerFactory;

public abstract class AbstractRootLoaderFactory<R extends ISchedulableRunner> implements ISchedulableRunnerFactory {
	/**
	 * This implementation of {@link ISchedulableRunnerFactory#getRunner()} simply returns
	 * the root loader that is returned by the {@link #getRootLoader()} method.
	 */
	@Override
	public final ISchedulableRunner getRunner() {
		return getRootLoader();
	}
	
	/**
	 * <p>Implementations of this method should return instances of the root loader associated to the 
	 * factory implementation. Usually you would have Spring provide and manage these instances,
	 * with the appropriate scope.</p>
	 * 
	 * <p>It would be easiest to simply annotate implementations of this method with the {@link Lookup}
	 * annotation, specifying the concrete type as the implementation. Unfortunately this annotation
	 * apparently doesn't take the concrete return type of the method implementation into account,
	 * but rather looks up the generic {@link ISchedulableRunner} type defined here. As there will be
	 * more than one {@link ISchedulableRunner} bean, the {@link Lookup} annotation fails unless you
	 * specify the actual bean name.</p>
	 * 
	 * <p>For now, most implementations rather use a different approach; they have an {@link ObjectFactory}
	 * field for the relevant root loader class, and this field is annotated with the {@link Autowired}
	 * annotation. These implementations then simply return the result of {@link ObjectFactory#getObject()}.</p>
	 */
	protected abstract R getRootLoader();
	
	@PostConstruct
	public final void logInitialized() {
		System.out.println("Initialized "+this);
	}
	
}
