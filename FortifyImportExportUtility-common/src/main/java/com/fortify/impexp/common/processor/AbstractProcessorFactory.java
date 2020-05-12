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
package com.fortify.impexp.common.processor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fortify.impexp.common.processor.entity.IEntityDescriptor;
import com.fortify.impexp.common.processor.entity.IEntitySource;
import com.fortify.impexp.common.processor.entity.IEntityType;

public abstract class AbstractProcessorFactory<E> implements IProcessorFactory<E> {
	private final Set<IEntitySource> supportedEntitySources = new HashSet<>();
	private final Set<IEntityType> supportedEntityTypes = new HashSet<>();
	private Class<E> supportedEntityJavaType;
	
	public final void setSupportedEntitySources(IEntitySource... supportedEntitySources) {
		this.supportedEntitySources.clear();
		if ( supportedEntitySources!=null ) {
			this.supportedEntitySources.addAll(Arrays.asList(supportedEntitySources));
		}
	}
	
	public final void setSupportedEntityTypes(IEntityType... supportedEntityTypes) {
		this.supportedEntityTypes.clear();
		if ( supportedEntityTypes!=null ) {
			this.supportedEntityTypes.addAll(Arrays.asList(supportedEntityTypes));
		}
	}
	
	public final void setSupportedEntityJavaType(Class<E> supportedEntityJavaType) {
		this.supportedEntityJavaType = supportedEntityJavaType;
	}
	
	@Override
	public boolean isActive(IEntityDescriptor entityDescriptor) {
		return isSupportedEntity(entityDescriptor) && isEnabled();
	}
	
	protected boolean isSupportedEntity(IEntityDescriptor entityDescriptor) {
		return isSupportedEntitySource(entityDescriptor.getSource())
				&& isSupportedEntityType(entityDescriptor.getType())
				&& isSupportedEntityJavaType(entityDescriptor.getJavaType());
	}
	
	protected abstract boolean isEnabled();

	protected boolean isSupportedIfSupportedEntitySourcesIsEmpty() {
		return true;
	}
	
	protected boolean isSupportedIfSupportedEntityTypesIsEmpty() {
		return true;
	}
	
	protected boolean isSupportedIfSupportedEntityJavaTypeIsEmpty() {
		return true;
	}
	
	protected boolean isEnabledIfPropertyPrefixIsEmpy() {
		return true;
	}
	
	protected final boolean isSupportedEntitySource(final IEntitySource entitySource) {
		return supportedEntitySources.size()==0 
				? isSupportedIfSupportedEntitySourcesIsEmpty() 
				: supportedEntitySources.contains(entitySource);
	}
	
	protected final boolean isSupportedEntityType(final IEntityType entityType) {
		return supportedEntityTypes.size()==0 
				? isSupportedIfSupportedEntityTypesIsEmpty() 
				: supportedEntityTypes.contains(entityType);
	}
	
	protected final boolean isSupportedEntityJavaType(final Class<?> entityJavaType) {
		return supportedEntityJavaType==null 
				? isSupportedIfSupportedEntityJavaTypeIsEmpty() 
				: supportedEntityJavaType.isAssignableFrom(entityJavaType);
			
	}
}
