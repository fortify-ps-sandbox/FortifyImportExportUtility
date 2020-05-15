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
package com.fortify.impexp.common.from.loader.config;

import org.slf4j.Logger;

import com.fortify.util.rest.query.AbstractRestConnectionQueryBuilder;

public class QueryBuilderLogHelper {
	private final Logger logger;
	private final String qbName;
	
	public QueryBuilderLogHelper(Logger logger, AbstractRestConnectionQueryBuilder<?,?> qb) {
		this.logger = logger;
		this.qbName = qb.getClass().getSimpleName()+"@"+qb.hashCode();
	}

	public final <T> T logApplyFilter(String name, T value) {
		if ( hasValue(value) ) {
			logger.debug("{}: Adding filter {}: {}", qbName, name, value);
		}
		return value;
	}

	protected <T> boolean hasValue(T value) {
		return value!=null; // TODO Check for empty map, ...
	}
}
