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
package com.fortify.impexp.to.jira.processor;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.fortify.impexp.common.processor.AbstractProcessorFactory;
import com.fortify.impexp.common.processor.entity.source.IEntitySource;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.source.SupportedEntitySourceDescriptorHelper;
import com.fortify.impexp.common.processor.entity.type.IEntityType;
import com.fortify.impexp.common.to.annotation.ToPluginComponent;
import com.fortify.impexp.to.jira.processor.connection.ToJiraRestConnection;

@ToPluginComponent
public abstract class AbstractToJiraProcessorFactory extends AbstractProcessorFactory<Object> {
	@Autowired(required=false) private ToJiraRestConnection conn;
	
	public AbstractToJiraProcessorFactory(IEntityType entityType, Collection<? extends IEntitySource> supportedEntitySources) {
		super(SupportedEntitySourceDescriptorHelper.builder()
				.supportedEntitySources(supportedEntitySources)
				.supportedEntityType(entityType)
				.supportedEntitySourceJavaType(Object.class)
				.build());
	}
	
	@Override
	protected final boolean isEnabled(IEntitySourceDescriptor entitySourceDescriptor) {
		return conn!=null && isProcessorEnabled(entitySourceDescriptor);
	}

	protected abstract boolean isProcessorEnabled(IEntitySourceDescriptor entitySourceDescriptor);
}
