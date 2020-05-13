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
package com.fortify.impexp.common.status.export;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.target.IEntityTargetDescriptor;

@Component
public class ActiveExportStatusHelperInvoker {
	@Autowired private ObjectFactory<Collection<IExportStatusHelperFactory<?, ?>>> allExportStatusHelperFactoriesFactory;
	
	private final IExportStatusHelper<?,?> getActiveExportStatusHelper(final IEntitySourceDescriptor entitySourceDescriptor, final IEntityTargetDescriptor entityTargetDescriptor) {
		List<IExportStatusHelper<?,?>> exportStatusHelpers = getActiveExportStatusHelpersStream(entitySourceDescriptor, entityTargetDescriptor)
				.map(factory->(IExportStatusHelper<?,?>)factory.getExportStatusHelper())
				.collect(Collectors.toList());
		if ( exportStatusHelpers.size()>1 ) {
			throw new IllegalStateException(String.format("More than one IExportStatusHelper found for entity source descriptor %s and entity target descriptor %s", entitySourceDescriptor, entityTargetDescriptor));
		}
		return exportStatusHelpers.size()==0 ? null : exportStatusHelpers.get(0);
	}

	private Stream<IExportStatusHelperFactory<?,?>> getActiveExportStatusHelpersStream(final IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor) {
		return allExportStatusHelperFactoriesFactory
				.getObject()
				.stream()
				.filter(factory->factory.isActive(entitySourceDescriptor, entityTargetDescriptor))
				.sorted(new OrderComparator());
	}
	
	@SuppressWarnings("unchecked")
	public <S,T> void notifyExported(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, Collection<S> sourceEntities, T targetEntity) {
		// This cast should be safe based on IEntitySourceDescriptor#getJavaType() and IEntityTargetDescriptor#getJavaType()
		((IExportStatusHelper<S,T>)getActiveExportStatusHelper(entitySourceDescriptor, entityTargetDescriptor))
			.notifyExported(entitySourceDescriptor, entityTargetDescriptor, sourceEntities, targetEntity);
	}
	
	@SuppressWarnings("unchecked")
	public <S> boolean isExported(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, S sourceEntity) {
		// This cast should be safe based on IEntitySourceDescriptor#getJavaType()
		return ((IExportStatusHelper<S,?>)getActiveExportStatusHelper(entitySourceDescriptor, entityTargetDescriptor))
			.isExported(entitySourceDescriptor, entityTargetDescriptor, sourceEntity);
	}
}
