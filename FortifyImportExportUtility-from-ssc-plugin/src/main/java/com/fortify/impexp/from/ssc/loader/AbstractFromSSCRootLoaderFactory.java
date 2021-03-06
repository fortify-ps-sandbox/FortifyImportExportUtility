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
package com.fortify.impexp.from.ssc.loader;

import org.springframework.beans.factory.annotation.Autowired;

import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.impexp.common.from.loader.AbstractRootLoaderFactory;
import com.fortify.impexp.from.ssc.annotation.FromSSC;
import com.fortify.util.spring.boot.scheduler.ISchedulableRunner;

public abstract class AbstractFromSSCRootLoaderFactory<R extends ISchedulableRunner> extends AbstractRootLoaderFactory<R> {
	@Autowired(required=false) @FromSSC private SSCAuthenticatingRestConnection conn;
	
	public AbstractFromSSCRootLoaderFactory() {}
	
	@Override
	public final boolean isEnabled() {
		return conn!=null && isLoaderEnabled();
	}

	protected abstract boolean isLoaderEnabled();
}
