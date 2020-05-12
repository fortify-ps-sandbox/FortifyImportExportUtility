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
package com.fortify.impexp.from.mock.loader.release;

import com.fortify.impexp.common.from.spi.annotation.FromPluginComponent;
import com.fortify.impexp.common.from.spi.loader.AbstractIntermediateLoader;
import com.fortify.impexp.common.processor.entity.IEntityDescriptor;
import com.fortify.util.rest.json.JSONMap;

@FromPluginComponent
public class FromMockReleaseFromApplicationLoader extends AbstractIntermediateLoader<JSONMap> {
	@Override
	public void process(final IEntityDescriptor entityDescriptor, final JSONMap application) {
		for ( int i = 0 ; i < 10 ; i++ ) {
			invokeActiveProcessors(FromMockReleaseFromApplicationLoaderFactory.ENTITY_DESCRIPTOR, getRelease(application, i));
		}
	}
	
	private JSONMap getRelease(JSONMap application, int i) {
		JSONMap result = new JSONMap();
		result.putPath("id", i);
		result.putPath("application", application);
		result.putPath("name", "Release "+i);
		return result;
	}
}
