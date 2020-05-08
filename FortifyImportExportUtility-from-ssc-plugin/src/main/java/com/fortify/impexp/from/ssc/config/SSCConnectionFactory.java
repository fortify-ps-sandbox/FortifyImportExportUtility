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
package com.fortify.impexp.from.ssc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fortify.client.ssc.api.SSCApplicationVersionAPI;
import com.fortify.client.ssc.api.SSCAttributeDefinitionAPI;
import com.fortify.client.ssc.api.SSCAttributeDefinitionAPI.SSCAttributeDefinitionHelper;
import com.fortify.client.ssc.api.SSCIssueTemplateAPI;
import com.fortify.client.ssc.api.SSCIssueTemplateAPI.SSCIssueTemplateHelper;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection.SSCAuthenticatingRestConnectionBuilder;

@Configuration
public class SSCConnectionFactory {
	/**
	 * Get a {@link SSCAuthenticatingRestConnectionBuilder} instance, automatically
	 * wiring all SSC connection properties defined in the configuration file. 
	 * @return
	 */
	@Bean
	@ConfigurationProperties("ssc.conn") 
	public SSCAuthenticatingRestConnectionBuilder sscConnectionBuilder() {
		return SSCAuthenticatingRestConnection.builder().multiThreaded(true);
	}
	
	/**
	 * Instantiate and test the {@link SSCAuthenticatingRestConnection} 
	 * instance used to connect to SSC, based on the connection builder 
	 * returned by {@link #sscConnectionBuilder()}. 
	 * @return
	 */
	@Bean
	public SSCAuthenticatingRestConnection sscConnection() {
		return testSSCConnection(sscConnectionBuilder().build());
	}
	
	/**
	 * Test whether the necessary application attributes have been defined on SSC.
	 * This also implicitly tests whether we can successfully connect to SSC.
	 * 
	 * @param conn
	 */
	public final SSCAuthenticatingRestConnection testSSCConnection(SSCAuthenticatingRestConnection conn) {
		conn.api(SSCApplicationVersionAPI.class)
			.queryApplicationVersions()
			.maxResults(1)
			.paramFields("id")
			.build().getUnique();
		return conn;
	}
	
	@Bean 
	public SSCAttributeDefinitionHelper sscAttributeDefinitionHelper() {
		return sscConnection().api(SSCAttributeDefinitionAPI.class).getAttributeDefinitionHelper();
	}
	
	@Bean 
	public SSCIssueTemplateHelper sscIssueTemplateHelper() {
		return sscConnection().api(SSCIssueTemplateAPI.class).getIssueTemplateHelper();
	}
}
