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
package com.fortify.impexp.from.mock.loader.release;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fortify.impexp.common.from.spi.annotation.FromPluginComponent;
import com.fortify.impexp.common.processor.entity.IEntityDescriptor;
import com.fortify.impexp.common.processor.entity.StandardEntityType;
import com.fortify.impexp.from.mock.loader.AbstractFromMockRootLoaderFactory;
import com.fortify.impexp.from.mock.processor.entity.FromMockEntityDescriptor;

@FromPluginComponent
public class FromMockReleaseLoaderFactory extends AbstractFromMockRootLoaderFactory<FromMockReleaseLoader> {
	@Autowired private ObjectFactory<FromMockReleaseLoader> rootLoaderFactory;
	
	public static final IEntityDescriptor ENTITY_DESCRIPTOR = 
			new FromMockEntityDescriptor().entity(StandardEntityType.RELEASE);

	@Override
	public String getCronSchedule() {
		return null;
	}
	
	@Override
	protected IEntityDescriptor getEntityDescriptor() {
		return ENTITY_DESCRIPTOR;
	}

	@Override
	public FromMockReleaseLoader getRootLoader() { 
		return rootLoaderFactory.getObject(); 
	}
	
	@Override
	public boolean isLoaderEnabled() {
		// TODO Check whether config is available
		return false;
	}
}
