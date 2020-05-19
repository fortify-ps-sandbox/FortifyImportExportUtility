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
package com.fortify.impexp.common.export.helper;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fortify.impexp.common.entity.config.EntityFilter;
import com.fortify.impexp.common.entity.config.EntityFilterConfig;
import com.fortify.impexp.common.entity.config.EntityTransformer;
import com.fortify.impexp.common.entity.config.EntityTransformerConfig;
import com.fortify.impexp.common.export.status.ActiveExportStatusHelpersInvoker;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.target.IEntityTargetDescriptor;
import com.fortify.util.grouping.Grouping;
import com.fortify.util.spring.SpringExpressionUtil;
import com.fortify.util.spring.expression.TemplateExpression;

import lombok.Builder;

public class ExportHelper<E> {
	private final ActiveExportStatusHelpersInvoker activeExportStatusHelpersInvoker;
	private final IEntitySourceDescriptor entitySourceDescriptor;
	private final IEntityTargetDescriptor entityTargetDescriptor;

	private final EntityTransformer entityTransformer;
	private final EntityFilter entityFilter;
	private final EntityFilter exportNewEntityFilter;
	private final EntityFilter updateExistingEntityFilter;
	
	private final Grouping<E> toBeExportedGrouping;
	private final Grouping<E> previouslyExportedGrouping;
	
	@Builder
	protected ExportHelper(ActiveExportStatusHelpersInvoker activeExportStatusHelpersInvoker,
			IEntitySourceDescriptor entitySourceDescriptor, IEntityTargetDescriptor entityTargetDescriptor,
			EntityTransformerConfig transformerConfig, EntityFilterConfig filterConfig,
			TemplateExpression exportNewGroupBy, EntityFilterConfig exportNewFilterConfig,
			EntityFilterConfig updateExistingFilterConfig,
			IGroupProcessor<E> exportNewGroupProcessor, IGroupProcessor<E> updateExistingGroupProcessor) {
		this.activeExportStatusHelpersInvoker = activeExportStatusHelpersInvoker;
		this.entitySourceDescriptor = entitySourceDescriptor;
		this.entityTargetDescriptor = entityTargetDescriptor;
		this.entityTransformer = new EntityTransformer(transformerConfig);
		this.entityFilter = new EntityFilter(filterConfig);
		this.exportNewEntityFilter = new EntityFilter(exportNewFilterConfig);
		this.updateExistingEntityFilter = new EntityFilter(updateExistingFilterConfig);
		this.toBeExportedGrouping = Grouping.<E>builder()
				.groupNameFunction(SpringExpressionUtil.expressionAsFunction(exportNewGroupBy, String.class))
				.blankGroupNameConsumer(Grouping::directInvokeOnBlankGroupName)
				.groupConsumer((groupName, entities)->exportNewGroupProcessor.processGroup(entitySourceDescriptor, groupName, entities))
				.build();
		this.previouslyExportedGrouping = Grouping.<E>builder()
				.groupConsumer((groupName, entities)->updateExistingGroupProcessor.processGroup(entitySourceDescriptor, groupName, entities))
				.build();
	}
	
	public static final <E> ExportHelperBuilder<E> fromConfig(IExportConfig exportConfig) {
		return ExportHelper.<E>builder()
				.transformerConfig(exportConfig.getTransformerConfig())
				.filterConfig(exportConfig.getFilterConfig())
				.exportNewGroupBy(exportConfig.getExportNewGroupByExpression())
				.exportNewFilterConfig(exportConfig.getExportNewFilterConfig())
				.updateExistingFilterConfig(exportConfig.getUpdateExistingFilterConfig());
	}
	
	public final void add(IEntitySourceDescriptor currentEntitySourceDescriptor, E entity) {
		checkEntitySourceDescriptor(currentEntitySourceDescriptor);
		entity = entityTransformer.transform(entity);
		if ( entityFilter.isIncluded(entity) ) {
			String exportedEntityLocation = activeExportStatusHelpersInvoker.getExportedEntityLocation(entitySourceDescriptor, entityTargetDescriptor, entity);
			if ( StringUtils.isNotBlank(exportedEntityLocation) ) {
				if ( updateExistingEntityFilter.isIncluded(entity) ) {
					previouslyExportedGrouping.add(exportedEntityLocation, entity);
				}
			} else {
				if ( exportNewEntityFilter.isIncluded(entity) ) {
					toBeExportedGrouping.add(entity);
				}
			}
		}
	}
	
	public final void runAndClose(IEntitySourceDescriptor currentEntitySourceDescriptor) {
		checkEntitySourceDescriptor(currentEntitySourceDescriptor);
		toBeExportedGrouping.runAndClose();
		previouslyExportedGrouping.runAndClose();
	}

	private void checkEntitySourceDescriptor(IEntitySourceDescriptor currentEntitySourceDescriptor) {
		if ( !currentEntitySourceDescriptor.equals(entitySourceDescriptor) ) {
			throw new IllegalStateException(
				String.format("Given EntitySourceDescriptor %s does not match configured EntitySourceDescriptor %s", 
					currentEntitySourceDescriptor, entitySourceDescriptor));
		}
		
	}
	
	@FunctionalInterface
	public interface IGroupProcessor<E> {
		public void processGroup(IEntitySourceDescriptor entitySourceDescriptor, String groupName, List<E> entities);
	}
	
}
