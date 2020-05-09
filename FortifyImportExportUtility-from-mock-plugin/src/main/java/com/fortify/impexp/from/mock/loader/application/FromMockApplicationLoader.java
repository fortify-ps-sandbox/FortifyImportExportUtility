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

import com.fortify.impexp.common.from.spi.annotation.FromPluginComponent;
import com.fortify.impexp.common.from.spi.loader.AbstractRootLoader;
import com.fortify.util.rest.json.JSONMap;

@FromPluginComponent
public class FromMockApplicationLoader extends AbstractRootLoader<JSONMap> {
	@Override
	public void run() {
		for ( int i = 0 ; i < 10 ; i++ ) {
			invokeEnabledProcessors(FromMockApplicationLoaderFactory.ENTITY_DESCRIPTOR, getApplication(i));
		}
	}

	private JSONMap getApplication(int i) {
		JSONMap result = new JSONMap();
		result.putPath("id", i);
		result.putPath("name", "Application "+i);
		return result;
	}
	
	@Override
	protected Map<String, Object> getProperties(JSONMap application) {
		Map<String, Object> result = new HashMap<>();
		result.put("to.mock.enabled", application.get("id",Integer.class)%2==0);
		result.put("to.mock.simple", application.get("id",String.class));
		return result;
	}
	

}
