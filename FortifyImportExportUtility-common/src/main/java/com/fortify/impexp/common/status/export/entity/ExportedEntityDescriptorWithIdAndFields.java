/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates, a Micro Focus company
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
package com.fortify.impexp.common.status.export.entity;

import java.util.function.Function;

import lombok.ToString;

/**
 * This class holds the export location, id and arbitrary (extra) fields for an exported entity. 
 * 
 * @author Ruud Senden
 */
@ToString(callSuper=true)
public class ExportedEntityDescriptorWithIdAndFields<I, F> extends ExportedEntityDescriptorWithId<I> implements IExportedEntityDescriptorWithFields<F> {
	private F fields;
	private final Function<I, F> getFieldsForId;
	
	public ExportedEntityDescriptorWithIdAndFields(String location, ExportedEntityStatus status, Function<String, I> convertlocationToId, Function<I, F> getFieldsForId) {
		super(location, status, convertlocationToId);
		this.getFieldsForId = getFieldsForId;
	}
	
	@Override
	public F getFields() {
		if ( fields==null ) {
			fields = getFieldsForId.apply(getId());
		}
		return fields;
	}
	
	public void resetFields() {
		fields = null;
	}
}
