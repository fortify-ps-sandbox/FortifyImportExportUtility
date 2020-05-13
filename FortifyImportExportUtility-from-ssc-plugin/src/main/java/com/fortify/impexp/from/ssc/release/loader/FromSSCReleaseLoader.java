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
package com.fortify.impexp.from.ssc.release.loader;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.fortify.client.ssc.api.SSCApplicationVersionAPI;
import com.fortify.client.ssc.api.SSCAttributeDefinitionAPI.SSCAttributeDefinitionHelper;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionsQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.impexp.common.from.spi.annotation.FromPluginComponent;
import com.fortify.impexp.common.from.spi.loader.AbstractRootLoader;
import com.fortify.impexp.common.processor.entity.source.IEntitySourceDescriptor;
import com.fortify.impexp.common.processor.entity.type.StandardEntityType;
import com.fortify.impexp.from.ssc.annotation.FromSSC;
import com.fortify.impexp.from.ssc.processor.entity.source.FromSSCSourceEntityDescriptor;
import com.fortify.impexp.from.ssc.release.loader.config.FromSSCReleaseLoaderConfig;
import com.fortify.util.rest.json.JSONMap;

@FromPluginComponent @FromSSC @Lazy
public class FromSSCReleaseLoader extends AbstractRootLoader<JSONMap> {
	public static final IEntitySourceDescriptor ENTITY_DESCRIPTOR = new FromSSCSourceEntityDescriptor().entity(StandardEntityType.RELEASE);
	@Autowired @FromSSC private SSCAuthenticatingRestConnection conn;
	@Autowired @FromSSC private SSCAttributeDefinitionHelper attributeDefinitionHelper;
	@Autowired @FromSSC private FromSSCReleaseLoaderConfig config;
	
	@Override
	public void run() {
		SSCApplicationVersionsQueryBuilder queryBuilder = 
			conn.api(SSCApplicationVersionAPI.class).queryApplicationVersions();
		config.updateQueryBuilder(queryBuilder, attributeDefinitionHelper);
		queryBuilder.build().processAll(this::processRelease);
	}

	private final void processRelease(JSONMap release) {
		invokeEnabledProcessors(ENTITY_DESCRIPTOR, release);
	}
	
	@Override
	protected Map<String, Object> getOverrideProperties(JSONMap input) {
		// TODO Evaluate expressions in config#getOverrideProperties
		return super.getOverrideProperties(input);
	}

}
