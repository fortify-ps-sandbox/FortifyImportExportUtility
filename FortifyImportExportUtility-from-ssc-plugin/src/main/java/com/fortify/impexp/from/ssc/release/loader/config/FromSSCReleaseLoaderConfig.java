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
package com.fortify.impexp.from.ssc.release.loader.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fortify.client.ssc.api.SSCAttributeDefinitionAPI.SSCAttributeDefinitionHelper;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionsQueryBuilder;
import com.fortify.client.ssc.api.query.builder.SSCOrderBy;
import com.fortify.impexp.common.from.annotation.FromPluginComponent;
import com.fortify.impexp.from.ssc.annotation.FromSSC;
import com.fortify.util.spring.expression.TemplateExpression;

import lombok.Data;

@Data
@FromPluginComponent @FromSSC
@ConfigurationProperties("from.ssc.load.releases")
public final class FromSSCReleaseLoaderConfig {
	private static final Logger LOG = LoggerFactory.getLogger(FromSSCReleaseLoaderConfig.class);
	private static final String EMPTY_TO_STRING = new FromSSCReleaseLoaderConfig().toString();
	@Value("${from.ssc.load.releases:undefined}") private String property = "undefined";
	
	private final FromSSCReleaseLoaderIncludeConfig include = new FromSSCReleaseLoaderIncludeConfig();
	private final FromSSCReleaseLoaderEntityTransformerConfig transform = new FromSSCReleaseLoaderEntityTransformerConfig();
	private final FromSSCReleaseLoaderEntityFilterConfig filter = new FromSSCReleaseLoaderEntityFilterConfig();
	private final SSCOrderBy orderBy = new SSCOrderBy();
	private final Map<String, TemplateExpression> overrideProperties = new LinkedHashMap<String, TemplateExpression>();
	
	/**
	 * This method indicates whether the current instance has been configured,
	 * returning false if the {@link #toString()} value equals the {@link #toString()}
	 * value of an empty instance.
	 *   
	 * @return
	 */
	public boolean isConfigured() {
		return !EMPTY_TO_STRING.equals(this.toString());
	}
	
	public void updateQueryBuilder(SSCApplicationVersionsQueryBuilder qb, SSCAttributeDefinitionHelper attributeDefinitionHelper) {
		logQBUpdate(qb, include);
		FromSSCReleaseLoaderIncludeConfigQueryBuilderUpdater.updateQueryBuilder(qb, include, attributeDefinitionHelper);
		logQBUpdate(qb, transform);
		FromSSCReleaseLoaderEntityTransformerQueryBuilderUpdater.updateQueryBuilder(qb, transform);
		logQBUpdate(qb, filter);
		FromSSCReleaseLoaderEntityFilterConfigQueryBuilderUpdater.updateQueryBuilder(qb, filter);
		logQBUpdate(qb, orderBy);
		qb.paramOrderBy(true, orderBy);
	}

	private <C> void logQBUpdate(SSCApplicationVersionsQueryBuilder qb, C config) {
		LOG.debug("Updating {} with configuration {}", qb, config);
	}
}
