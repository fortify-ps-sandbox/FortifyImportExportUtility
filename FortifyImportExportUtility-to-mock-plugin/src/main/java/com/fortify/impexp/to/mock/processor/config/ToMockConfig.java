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
package com.fortify.impexp.to.mock.processor.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fortify.impexp.common.processor.entity.IEntitySource;
import com.fortify.impexp.common.processor.entity.IEntityType;
import com.fortify.impexp.common.to.spi.annotation.ToPluginComponent;

import lombok.Data;

@Data
@ToPluginComponent
@ConfigurationProperties("to.mock")
public class ToMockConfig {
	private String simple;
	private ToMockConfigEntity entity;
	
	public final IEntitySource[] getEntitySources() {
		return entity==null ? null : entity.getSources();
	}
	
	public final IEntityType[] getEntityTypes() {
		return entity==null ? null : entity.getTypes();
	}
	
	@Data
	public static final class ToMockConfigEntity {
		private IEntitySource[] sources;
		private IEntityType[] types;
	}
	
	@PostConstruct
	public void initialized() {
		System.out.println("Initialized "+this);
	}
}