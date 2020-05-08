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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import com.fortify.impexp.common.processor.selector.IProcessorSelector;
import com.fortify.impexp.common.processor.selector.ISourceEntity;
import com.fortify.impexp.common.processor.selector.ISourceSystem;

public abstract class AbstractProcessorFactory<I> implements IProcessorFactory<I> {
	@Autowired private StandardEnvironment environment;
	private final Set<ISourceSystem> supportedSourceSystems = new HashSet<>();
	private final Set<ISourceEntity> supportedSourceEntities = new HashSet<>();
	private Class<?> supportedProcessorInputType;
	private String propertyPrefix;
	
	public final void setSupportedSourceSystems(ISourceSystem... sourceSystems) {
		supportedSourceSystems.clear();
		if ( sourceSystems!=null ) {
			this.supportedSourceSystems.addAll(Arrays.asList(sourceSystems));
		}
	}
	
	public final void setSupportedSourceEntities(ISourceEntity... sourceEntities) {
		supportedSourceEntities.clear();
		if ( sourceEntities!=null ) {
			this.supportedSourceEntities.addAll(Arrays.asList(sourceEntities));
		}
	}
	
	public final void setSupportedProcessorInputType(Class<?> supportedProcessorInputType) {
		this.supportedProcessorInputType = supportedProcessorInputType;
	}
	
	public final void setPropertyPrefix(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
	}
	
	@Override
	public boolean isEnabled(IProcessorSelector descriptor) {
		return isEnabledPropertyNotFalse(isEnabledIfPropertyPrefixIsEmpy())
				&& isSupportedSourceSystem(descriptor.getSourceSystem(), isEnabledIfSupportedSourceSystemsIsEmpty())
				&& isSupportedSourceEntity(descriptor.getSourceEntity(), isEnabledIfSupportedSourceEntitiesIsEmpty())
				&& isSupportedProcessorInputType(descriptor.getProcessorInputType(), isEnabledIfSupportedProcessorInputTypeIsEmpty())
				&& propertyTreeWithPrefixExists(isEnabledIfPropertyPrefixIsEmpy());
	}

	protected boolean isEnabledIfSupportedSourceSystemsIsEmpty() {
		return true;
	}
	
	protected boolean isEnabledIfSupportedSourceEntitiesIsEmpty() {
		return true;
	}
	
	protected boolean isEnabledIfSupportedProcessorInputTypeIsEmpty() {
		return true;
	}
	
	protected boolean isEnabledIfPropertyPrefixIsEmpy() {
		return true;
	}
	
	protected final boolean isSupportedSourceSystem(final ISourceSystem sourceSystem, final boolean defaultIfEmpty) {
		return supportedSourceSystems.size()==0 ? defaultIfEmpty : supportedSourceSystems.contains(sourceSystem);
	}
	
	protected final boolean isSupportedSourceEntity(final ISourceEntity sourceEntity, final boolean defaultIfEmpty) {
		return supportedSourceEntities.size()==0 ? defaultIfEmpty : supportedSourceEntities.contains(sourceEntity);
	}
	
	protected final boolean isSupportedProcessorInputType(final Class<?> inputType, final boolean defaultIfEmpty) {
		return supportedProcessorInputType==null 
				? defaultIfEmpty 
				: supportedProcessorInputType.isAssignableFrom(inputType);
			
	}

	protected final boolean isEnabledPropertyNotFalse(boolean defaultIfPropertyPrefixEmpty) {
		return StringUtils.isBlank(propertyPrefix) 
				? defaultIfPropertyPrefixEmpty 
				: environment.getProperty(propertyPrefix+".enabled", Boolean.class)!=Boolean.FALSE;
	}

	protected final boolean propertyTreeWithPrefixExists(boolean defaultIfPropertyPrefixEmpty) {
		if ( propertyPrefix==null || propertyPrefix.isEmpty() ) {return defaultIfPropertyPrefixEmpty;}
		MutablePropertySources propertySources = environment.getPropertySources();
		boolean hasPropertyTree = propertySources.stream()
			.filter(ps->ps instanceof EnumerablePropertySource<?>)
			.map(ps->((EnumerablePropertySource<?>)ps).getPropertyNames())
			.flatMap(Arrays::stream)
			.anyMatch(propertyName->propertyName.startsWith(propertyPrefix));
		return hasPropertyTree;
	}
}
