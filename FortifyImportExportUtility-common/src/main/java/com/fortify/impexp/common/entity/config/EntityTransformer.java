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
package com.fortify.impexp.common.entity.config;

import java.util.Arrays;
import java.util.Map;

import com.fortify.impexp.common.processor.IProcessor;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.wrapper.ProcessorWrapper;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.SpringExpressionUtil;
import com.fortify.util.spring.expression.TemplateExpression;

import lombok.Data;

@Data
public class EntityTransformer {
	private final EntityTransformerConfig entityTransformerConfig;
	
	public static final <E> IProcessor<E> wrapWithTransformingProcessor(IProcessor<E> wrappedProcessor, EntityTransformerConfig entityTransformerConfig, ObjectPropertyAdder<E> objectPropertyAdder, ObjectPropertyRemover<E> objectPropertyRemover) {
		return new TransformingProcessorWrapper<>(wrappedProcessor, entityTransformerConfig, objectPropertyAdder, objectPropertyRemover);
	}
	
	public static final <E extends Map<String,Object>> IProcessor<E> wrapWithTransformingProcessor(IProcessor<E> wrappedProcessor, EntityTransformerConfig entityTransformerConfig) {
		return new TransformingProcessorWrapper<>(wrappedProcessor, entityTransformerConfig, Map::put, Map::remove);
	}
	
	public static final IProcessor<JSONMap> wrapWithTransformingJSONMapProcessor(IProcessor<JSONMap> wrappedProcessor, EntityTransformerConfig entityTransformerConfig) {
		return new TransformingProcessorWrapper<>(wrappedProcessor, entityTransformerConfig, JSONMap::putPath, JSONMap::remove);
	}
	
	public JSONMap transformJSONMap(JSONMap entity) {
		return transform(entity, JSONMap::putPath, JSONMap::remove);
	}
	
	public <E extends Map<String,Object>> E transform(E entity) {
		return transform(entity, Map::put, Map::remove);
	}
	
	public <E> E transform(E entity, ObjectPropertyAdder<E> objectPropertyAdder, ObjectPropertyRemover<E> objectPropertyRemover) {
		addFields(entity, objectPropertyAdder);
		removeFields(entity, objectPropertyRemover);
		return entity;
	}
	
	private final <E> void addFields(final E entity, ObjectPropertyAdder<E> objectPropertyAdder) {
		getEntityTransformerConfig().getAdd().entrySet().forEach(entry -> addField(entity, entry.getKey(), entry.getValue(), objectPropertyAdder));
	}
	
	private final <E> void addField(E entity, String propertyName, TemplateExpression propertyTemplateExpression, ObjectPropertyAdder<E> objectPropertyAdder) {
		Object propertyValue = SpringExpressionUtil.evaluateExpression(entity, propertyTemplateExpression, Object.class);
		objectPropertyAdder.addPropertyValue(entity, propertyName, propertyValue);
	}
	
	private final <E> void removeFields(final E entity, ObjectPropertyRemover<E> objectPropertyRemover) {
		Arrays.stream(getEntityTransformerConfig().getRemove()).forEach(entry -> removeField(entity, entry, objectPropertyRemover));
	}
	
	private final <E> void removeField(E entity, String propertyName, ObjectPropertyRemover<E> objectPropertyRemover) {
		objectPropertyRemover.removePropertyValue(entity, propertyName);
	}
	
	@FunctionalInterface
	public static interface ObjectPropertyAdder<E> {
		public void addPropertyValue(E entity, String propertyName, Object propertyValue);
	}
	
	@FunctionalInterface
	public static interface ObjectPropertyRemover<E> {
		public void removePropertyValue(E entity, String propertyName);
	}
	
	public static final class TransformingProcessorWrapper<E> extends ProcessorWrapper<E> {
		private final EntityTransformer entityTransformer;
		private final ObjectPropertyAdder<E> objectPropertyAdder;
		private final ObjectPropertyRemover<E> objectPropertyRemover;

		public TransformingProcessorWrapper(IProcessor<E> wrappedProcessor, EntityTransformerConfig entityTransformerConfig, ObjectPropertyAdder<E> objectPropertyAdder, ObjectPropertyRemover<E> objectPropertyRemover) {
			super(wrappedProcessor);
			this.entityTransformer = new EntityTransformer(entityTransformerConfig);
			this.objectPropertyAdder = objectPropertyAdder;
			this.objectPropertyRemover = objectPropertyRemover;
		}
		
		@Override
		public void process(IEntitySourceDescriptor entitySourceDescriptor, E entity) {
			super.process(entitySourceDescriptor, entityTransformer.transform(entity, objectPropertyAdder, objectPropertyRemover));
		}
		
	}
}
