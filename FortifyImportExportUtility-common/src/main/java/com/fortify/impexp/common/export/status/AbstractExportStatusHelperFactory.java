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
package com.fortify.impexp.common.export.status;

import com.fortify.impexp.common.export.status.entity.IExportedEntityDescriptor;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.source.SupportedEntitySourceDescriptorHelper;
import com.fortify.impexp.common.processor.entity.target.IEntityTargetDescriptor;
import com.fortify.impexp.common.processor.entity.target.SupportedEntityTargetDescriptorHelper;

public abstract class AbstractExportStatusHelperFactory<S,T extends IExportedEntityDescriptor> implements IExportStatusHelperFactory<S,T> {
	private final SupportedEntitySourceDescriptorHelper supportedEntitySourceDescriptorHelper;
	private final SupportedEntityTargetDescriptorHelper supportedEntityTargetDescriptorHelper;
	
	public AbstractExportStatusHelperFactory(
			SupportedEntitySourceDescriptorHelper supportedEntitySourceDescriptorHelper,
			SupportedEntityTargetDescriptorHelper supportedEntityTargetDescriptorHelper) {
		super();
		this.supportedEntitySourceDescriptorHelper = supportedEntitySourceDescriptorHelper;
		this.supportedEntityTargetDescriptorHelper = supportedEntityTargetDescriptorHelper;
	}

	@Override
	public boolean isActive(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor) {
		return isSupportedEntity(entitySourceDescriptor, entityTargetDescriptor) && isEnabled(entitySourceDescriptor, entityTargetDescriptor);
	}
	
	protected boolean isSupportedEntity(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor) {
		return isSupportedEntity(entitySourceDescriptor) && isSupportedEntity(entityTargetDescriptor);
	}

	protected boolean isSupportedEntity(IEntitySourceDescriptor entitySourceDescriptor) {
		return supportedEntitySourceDescriptorHelper.isSupportedEntity(entitySourceDescriptor);
	}
	
	protected boolean isSupportedEntity(IEntityTargetDescriptor entityTargetDescriptor) {
		return supportedEntityTargetDescriptorHelper.isSupportedEntity(entityTargetDescriptor);
	}
	
	protected abstract boolean isEnabled(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor);
}
