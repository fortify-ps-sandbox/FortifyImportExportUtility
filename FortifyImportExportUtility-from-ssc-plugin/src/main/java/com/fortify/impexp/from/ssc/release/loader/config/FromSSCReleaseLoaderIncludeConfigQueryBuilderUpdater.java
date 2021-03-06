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

import com.fortify.client.ssc.api.SSCAttributeDefinitionAPI.SSCAttributeDefinitionHelper;
import com.fortify.client.ssc.api.query.builder.EmbedType;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionsQueryBuilder;
import com.fortify.impexp.common.from.loader.config.LoaderIncludeConfig.LoaderIncludeSubEntityConfig;

public final class FromSSCReleaseLoaderIncludeConfigQueryBuilderUpdater {
	public static final SSCApplicationVersionsQueryBuilder updateQueryBuilder(SSCApplicationVersionsQueryBuilder qb, FromSSCReleaseLoaderIncludeConfig config, SSCAttributeDefinitionHelper attributeDefinitionHelper) {
		addSubEntities(qb, config, attributeDefinitionHelper);
		return qb.paramFields(config.getFields());
	}
	
	private static final void addSubEntities(SSCApplicationVersionsQueryBuilder qb, FromSSCReleaseLoaderIncludeConfig config, SSCAttributeDefinitionHelper attributeDefinitionHelper) {
		LoaderIncludeSubEntityConfig[] subEntities = config.getSubEntities();
		if ( subEntities!=null ) {
			for ( LoaderIncludeSubEntityConfig subEntity : subEntities ) {
				if ( "attributeValuesByName".equals(subEntity.getName()) ) {
					qb.embedAttributeValuesByName(attributeDefinitionHelper);
				} else {
					qb.embedSubEntity(subEntity.getName(), EmbedType.PRELOAD, subEntity.getFields());
				}
			}
		}
	}
}
