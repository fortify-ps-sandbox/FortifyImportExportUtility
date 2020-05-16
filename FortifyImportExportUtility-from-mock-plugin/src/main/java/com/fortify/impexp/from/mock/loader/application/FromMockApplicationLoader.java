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
package com.fortify.impexp.from.mock.loader.application;

import java.util.HashMap;
import java.util.Map;

import com.fortify.impexp.common.from.annotation.FromPluginComponent;
import com.fortify.impexp.common.from.loader.AbstractRootLoader;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.type.StandardEntityType;
import com.fortify.impexp.from.mock.processor.entity.source.FromMockEntitySourceDescriptor;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.SpringExpressionUtil;
import com.fortify.util.spring.expression.TemplateExpression;

@FromPluginComponent
public class FromMockApplicationLoader extends AbstractRootLoader<JSONMap> {
	private static final IEntitySourceDescriptor ENTITY_DESCRIPTOR = new FromMockEntitySourceDescriptor().entity(StandardEntityType.APPLICATION);
	
	@Override
	public void run() {
		for ( int i = 0 ; i < 10 ; i++ ) {
			invokeProcessOnActiveProcessors(ENTITY_DESCRIPTOR, getApplication(i));
		}
	}

	private JSONMap getApplication(int i) {
		JSONMap result = new JSONMap();
		result.putPath("id", i);
		result.putPath("name", "Application "+i);
		return result;
	}
	
	@Override
	protected Map<String, TemplateExpression> getPropertyTemplates() {
		Map<String, TemplateExpression> result = new HashMap<>();
		result.put("to.mock.enabled", SpringExpressionUtil.parseTemplateExpression("${id=='2'}"));
		result.put("to.mock.simple", SpringExpressionUtil.parseTemplateExpression("${id}"));
		return result;
	}
	

}
