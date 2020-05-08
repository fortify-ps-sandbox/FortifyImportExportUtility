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
package com.fortify.impexp.to.file.processor.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fortify.impexp.common.processor.selector.ISourceEntity;
import com.fortify.impexp.common.processor.selector.ISourceSystem;
import com.fortify.impexp.target.common.spi.annotation.TargetComponent;
import com.fortify.impexp.to.file.processor.ToFileProcessorFactory;

import lombok.Data;

@Data
@TargetComponent
@ConfigurationProperties(ToFileProcessorFactory.PROPERTY_PREFIX)
public class ToFileConfig {
	private String simple;
	private ToFileConfigSource source;
	
	public final ISourceSystem[] getSourceSystems() {
		return source==null ? null : source.getSystems();
	}
	
	public final ISourceEntity[] getSourceEntities() {
		return source==null ? null : source.getEntities();
	}
	
	@Data
	public static final class ToFileConfigSource {
		private ISourceSystem[] systems;
		private ISourceEntity[] entities;
	}
	
	@PostConstruct
	public void initialized() {
		System.out.println("Initialized "+this);
	}
}