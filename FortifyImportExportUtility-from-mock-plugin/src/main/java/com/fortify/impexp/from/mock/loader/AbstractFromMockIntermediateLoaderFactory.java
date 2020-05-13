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
package com.fortify.impexp.from.mock.loader;

import com.fortify.impexp.common.from.spi.loader.AbstractIntermediateLoaderFactory;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.source.StandardEntitySource;
import com.fortify.impexp.common.processor.entity.source.SupportedEntitySourceDescriptorHelper;
import com.fortify.impexp.common.processor.entity.type.IEntityType;
import com.fortify.util.rest.json.JSONMap;

public abstract class AbstractFromMockIntermediateLoaderFactory extends AbstractIntermediateLoaderFactory<JSONMap> {
	public AbstractFromMockIntermediateLoaderFactory(IEntityType entityType) {
		super(SupportedEntitySourceDescriptorHelper.builder()
				.supportedEntitySource(StandardEntitySource.MOCK)
				.supportedEntityType(entityType)
				.supportedEntitySourceJavaType(JSONMap.class)
				.build());
	}
	
	@Override
	protected final boolean isEnabled(IEntitySourceDescriptor entitySourceDescriptor) {
		// For consistency with other plugins, we delegate to the abstract isLoaderEnabled() method
		return isLoaderEnabled();
	}

	protected abstract boolean isLoaderEnabled();
}
