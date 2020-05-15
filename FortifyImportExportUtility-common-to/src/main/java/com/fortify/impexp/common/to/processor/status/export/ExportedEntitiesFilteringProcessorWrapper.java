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
package com.fortify.impexp.common.to.processor.status.export;

import java.util.Arrays;
import java.util.Collection;

import com.fortify.impexp.common.processor.IProcessor;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.target.IEntityTargetDescriptor;
import com.fortify.impexp.common.processor.wrapper.AbstractProcessorWrapper;
import com.fortify.impexp.common.status.export.ActiveExportStatusHelpersInvoker;

public class ExportedEntitiesFilteringProcessorWrapper<S> extends AbstractProcessorWrapper<S> {
	private final Collection<IProcessor<S>> exportedEntityProcessors;
	private final Collection<IProcessor<S>> notExportedEntityProcessors;
	private final IEntityTargetDescriptor entityTargetDescriptor;
	private final ActiveExportStatusHelpersInvoker exportStatusHelper;
	
	public ExportedEntitiesFilteringProcessorWrapper(IExportedEntityProcessor<S> exportedEntityProcessor,
			INotExportedEntityProcessor<S> notExportedEntityProcessor,
			IEntityTargetDescriptor entityTargetDescriptor,
			ActiveExportStatusHelpersInvoker exportStatusHelper) {
		super();
		this.exportedEntityProcessors = Arrays.asList(exportedEntityProcessor);
		this.notExportedEntityProcessors = Arrays.asList(notExportedEntityProcessor);
		this.entityTargetDescriptor = entityTargetDescriptor;
		this.exportStatusHelper = exportStatusHelper;
	}

	@Override
	protected Collection<IProcessor<S>> getProcessors(IEntitySourceDescriptor entitySourceDescriptor, S entity) {
		if ( exportStatusHelper.isPreviouslyExported(entitySourceDescriptor, entityTargetDescriptor, entity) ) {
			return exportedEntityProcessors;
		} else {
			return notExportedEntityProcessors;
		}
	}
}
