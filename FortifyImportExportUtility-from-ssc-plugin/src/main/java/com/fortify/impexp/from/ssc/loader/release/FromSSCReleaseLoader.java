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
package com.fortify.impexp.from.ssc.loader.release;

import org.springframework.beans.factory.annotation.Autowired;

import com.fortify.client.ssc.api.SSCApplicationVersionAPI;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.impexp.source.common.spi.annotation.SourceComponent;
import com.fortify.impexp.source.common.spi.loader.AbstractRootLoader;
import com.fortify.util.rest.json.JSONMap;

@SourceComponent
public class FromSSCReleaseLoader extends AbstractRootLoader<JSONMap> {
	@Autowired private SSCAuthenticatingRestConnection conn;
	
	@Override
	public void run() {
		conn.api(SSCApplicationVersionAPI.class)
			.queryApplicationVersions()
			.maxResults(10)
			.build()
			.processAll(this::processRelease);
	}
	
	private final void processRelease(JSONMap release) {
		invokeEnabledProcessors(FromSSCReleaseLoaderFactory.TARGET_PROCESSOR_SELECTOR, release);
	}

}
