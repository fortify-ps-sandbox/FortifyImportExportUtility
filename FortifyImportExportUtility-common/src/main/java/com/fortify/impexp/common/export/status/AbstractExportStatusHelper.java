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
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fortify.impexp.common.export.status.entity.ExportedEntityStatus;
import com.fortify.impexp.common.export.status.entity.IExportedEntityDescriptor;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.target.IEntityTargetDescriptor;

public abstract class AbstractExportStatusHelper<S,T extends IExportedEntityDescriptor> implements IExportStatusHelper<S,T> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractExportStatusHelper.class);
	
	@Override
	public final void updateSourceEntity(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, Collection<S> sourceEntities, T exportedEntityDescriptor) {
		if ( exportedEntityDescriptor.getStatus()==ExportedEntityStatus.NEW ) {
			updateSourceEntityForNewExportedEntity(entitySourceDescriptor, entityTargetDescriptor, sourceEntities, exportedEntityDescriptor);
		} else {
			_updateSourceEntityForExistingExportedEntity(entitySourceDescriptor, entityTargetDescriptor, sourceEntities, exportedEntityDescriptor);
		}
	}
	
	private final void _updateSourceEntityForExistingExportedEntity(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, Collection<S> sourceEntities, T exportedEntityDescriptor) {
		if ( entityTargetDescriptor.isLocationChangeable() ) {
			updateExportLocationForExistingExportedEntity(entitySourceDescriptor, entityTargetDescriptor, sourceEntities, exportedEntityDescriptor);
		}
		updateSourceEntityForExistingExportedEntity(entitySourceDescriptor, entityTargetDescriptor, sourceEntities, exportedEntityDescriptor);
	}

	protected abstract void updateSourceEntityForNewExportedEntity(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, Collection<S> sourceEntities, T exportedEntityDescriptor);
	protected void updateSourceEntityForExistingExportedEntity(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, Collection<S> sourceEntities, T exportedEntityDescriptor) {
		LOG.trace("Source entity update not supported by this ExportStatusHelper implementation");
	}
	
	protected void updateExportLocationForExistingExportedEntity(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, Collection<S> sourceEntities, T exportedEntityDescriptor) {
		String currentLocation = exportedEntityDescriptor.getLocation();
		Stream<S> sourceEntitiesToUpdateStream = sourceEntities.stream()
			.filter(getExportLocationChangedPredicate(entitySourceDescriptor, entityTargetDescriptor, currentLocation));
		updateExportLocation(entitySourceDescriptor, entityTargetDescriptor, sourceEntitiesToUpdateStream, exportedEntityDescriptor);
	}
	
	protected void updateExportLocation(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, Stream<S> sourceEntitiesToUpdateStream, T exportedEntityDescriptor) {
		LOG.warn("Not updating export locations; new location: {}, old location(s): {}", 
				exportedEntityDescriptor.getLocation(),
				getOldExportLocations(entitySourceDescriptor, entityTargetDescriptor, sourceEntitiesToUpdateStream));
	}

	private List<String> getOldExportLocations(IEntitySourceDescriptor entitySourceDescriptor,
			IEntityTargetDescriptor entityTargetDescriptor, Stream<S> sourceEntitiesToUpdateStream) {
		return sourceEntitiesToUpdateStream.map(sourceEntity->getExportedEntityLocation(entitySourceDescriptor, entityTargetDescriptor, sourceEntity)).collect(Collectors.toList());
	}

	private final Predicate<S> getExportLocationChangedPredicate(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, String currentLocation) {
		return sourceEntity -> !isExportLocationMatching(entitySourceDescriptor, entityTargetDescriptor, sourceEntity, currentLocation);
	}

	private final boolean isExportLocationMatching(IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor, S sourceEntity, String updatedLocation) {
		String previousLocation = getExportedEntityLocation(entitySourceDescriptor, entityTargetDescriptor, sourceEntity);
		return StringUtils.equals(updatedLocation, previousLocation);
	}
}
