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
import com.fortify.impexp.common.from.loader.AbstractIntermediateLoaderFactory;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.source.StandardEntitySource;
import com.fortify.impexp.common.processor.entity.source.SupportedEntitySourceDescriptorHelper;
import com.fortify.impexp.common.processor.entity.type.IEntityType;
import com.fortify.impexp.from.ssc.annotation.FromSSC;
import com.fortify.util.rest.json.JSONMap;

public abstract class AbstractFromSSCIntermediateLoaderFactory extends AbstractIntermediateLoaderFactory<JSONMap> {
	@Autowired(required=false) @FromSSC private SSCAuthenticatingRestConnection conn;
	
	public AbstractFromSSCIntermediateLoaderFactory(IEntityType entityType) {
		super(SupportedEntitySourceDescriptorHelper.builder()
				.supportedEntitySource(StandardEntitySource.SSC)
				.supportedEntityType(entityType)
				.supportedEntitySourceJavaType(JSONMap.class)
				.build());
	}
	
	@Override
	protected final boolean isEnabled(IEntitySourceDescriptor entitySourceDescriptor) {
		return conn!=null && isLoaderEnabled();
	}

	protected abstract boolean isLoaderEnabled();
}
