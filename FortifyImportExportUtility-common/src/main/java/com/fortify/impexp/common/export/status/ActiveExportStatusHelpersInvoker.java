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

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import com.fortify.impexp.common.export.status.entity.IExportedEntityDescriptor;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.target.IEntityTargetDescriptor;

@Component
public class ActiveExportStatusHelpersInvoker {
	@Autowired private ObjectFactory<Collection<IExportStatusHelperFactory<?, ?>>> allExportStatusHelperFactoriesFactory;
	
	private final Collection<IExportStatusHelper<?,?>> getActiveExportStatusHelpers(final IEntitySourceDescriptor entitySourceDescriptor, final IEntityTargetDescriptor entityTargetDescriptor) {
		return getActiveExportStatusHelpersStream(entitySourceDescriptor, entityTargetDescriptor)
				.map(factory->(IExportStatusHelper<?,?>)factory.getExportStatusHelper())
				.collect(Collectors.toList());
	}

	private Stream<IExportStatusHelperFactory<?,?>> getActiveExportStatusHelpersStream(final IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor) {
		return allExportStatusHelperFactoriesFactory
				.getObject()
				.stream()
				.filter(factory->factory.isActive(entitySourceDescriptor, entityTargetDescriptor))
				.sorted(new OrderComparator());
	}
	
	@SuppressWarnings("unchecked")
	public <S,T extends IExportedEntityDescriptor> void updateSourceEntity(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, Collection<S> sourceEntities, T exportedEntityDescriptor) {
		getActiveExportStatusHelpers(entitySourceDescriptor, entityTargetDescriptor)
			// This cast should be safe based on IEntitySourceDescriptor#getJavaType() and IEntityTargetDescriptor#getJavaType()
			.forEach(helper -> 
				((IExportStatusHelper<S,T>)helper).updateSourceEntity(entitySourceDescriptor, entityTargetDescriptor, sourceEntities, exportedEntityDescriptor));
	}
	
	@SuppressWarnings("unchecked")
	public <S> String getExportedEntityLocation(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, S sourceEntity) {
		 return getActiveExportStatusHelpers(entitySourceDescriptor, entityTargetDescriptor)
			.stream()
			.map(helper->
				((IExportStatusHelper<S,?>)helper).getExportedEntityLocation(entitySourceDescriptor, entityTargetDescriptor, sourceEntity))
			.filter(StringUtils::isNotBlank)
			.findAny().orElse(null);
	}
	
	public <S> boolean isPreviouslyExported(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, S sourceEntity) {
		return getExportedEntityLocation(entitySourceDescriptor, entityTargetDescriptor, sourceEntity) != null;
	}
}
