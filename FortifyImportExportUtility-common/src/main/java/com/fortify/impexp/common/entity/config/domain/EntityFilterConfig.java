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
package com.fortify.impexp.common.entity.config.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.fortify.util.spring.SpringExpressionUtil;

import lombok.Data;

@Data
public class EntityFilterConfig<E> {
	private final Map<String, Pattern> matchFieldRegex = new LinkedHashMap<>();
	
	public boolean isIncluded(final E entity) {
		return matchFieldRegex.entrySet().stream().allMatch(entry->isMatching(entity, entry));
	}
	
	protected boolean isMatching(E entity, Map.Entry<String, Pattern> entry) {
		return isMatching(entity, entry.getKey(), entry.getValue());
	}

	protected boolean isMatching(E entity, String propertyName, Pattern pattern) {
		return pattern.matcher(SpringExpressionUtil.evaluateExpression(entity, propertyName, String.class)).matches();
	}
}
