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
package com.fortify.impexp.to.mock.processor;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import com.fortify.impexp.common.processor.AbstractProcessorFactory;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.source.SupportedEntitySourceDescriptorHelper;
import com.fortify.impexp.common.to.annotation.ToPluginComponent;
import com.fortify.impexp.to.mock.processor.config.ToMockConfig;

@ToPluginComponent
public class ToMockProcessorFactory extends AbstractProcessorFactory<Object> {
	@Autowired private ObjectFactory<ToMockProcessor> processorFactory;
	
	public ToMockProcessorFactory(@Autowired ToMockConfig config) {
		super(SupportedEntitySourceDescriptorHelper.builder()
				.supportedEntitySources(config.getEntitySources())
				.supportedEntityTypes(config.getEntityTypes())
				.supportedEntitySourceJavaType(Object.class)
				.build());
	}

	@Override
	public ToMockProcessor getProcessor() { 
		return processorFactory.getObject(); 
	}
	
	@Override
	protected boolean isEnabled(IEntitySourceDescriptor entitySourceDescriptor) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
