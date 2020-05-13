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
package com.fortify.impexp.from.ssc.connection;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import com.fortify.impexp.from.ssc.annotation.FromSSC;
import com.fortify.impexp.from.ssc.loader.AbstractFromSSCIntermediateLoaderFactory;
import com.fortify.impexp.from.ssc.loader.AbstractFromSSCRootLoaderFactory;

/**
 * <p>This class handles configuration and instantiation of the {@link SSCAuthenticatingRestConnection}
 * bean used to communicate with SSC. The SSC connection is initialized only if the <code>from.ssc.baseUrl</code>
 * property has been configured. As such, the {@link AbstractFromSSCRootLoaderFactory#isEnabled()} and 
 * {@link AbstractFromSSCIntermediateLoaderFactory#isActive(com.fortify.impexp.common.processor.entity.IEntityDescriptor)} 
 * methods return <code>false</code> if no connection is available.</p>
 * 
 * <p><strong>IMPORTANT:</strong> To avoid autowiring conflicts with other plugins that may define
 * SSC connections (for example defining a target SSC connection), beans provided by this class should 
 * be autowired using the {@link FromSSC} {@link javax.inject.Qualifier}.</p>
 * 
 * @author Ruud Senden
 *
 */
@Configuration @FromSSC
@ConditionalOnProperty("from.ssc.baseUrl")
public class FromSSCConnectionConfiguration {
	/**
	 * Get a {@link SSCAuthenticatingRestConnectionBuilder} instance, automatically
	 * wiring all SSC connection properties defined in the configuration file. 
	 * @return
	 */
	@Bean @FromSSC
	@ConfigurationProperties("from.ssc") // TODO Add JSR validation to RestConnectionBuilders
	public SSCAuthenticatingRestConnectionBuilder fromSSCConnectionBuilder() {
		return SSCAuthenticatingRestConnection.builder().multiThreaded(true);
	}
	
	/**
	 * Instantiate and test the {@link SSCAuthenticatingRestConnection} 
	 * instance used to connect to SSC, based on the connection builder 
	 * returned by {@link #fromSSCConnectionBuilder()}. 
	 * @return
	 */
	@Bean @FromSSC
	public SSCAuthenticatingRestConnection fromSSCConnection() {
		return testSSCConnection(fromSSCConnectionBuilder().build());
	}
	
	/**
	 * Test whether we can successfully connect to SSC.
	 * 
	 * @param conn
	 */
	private final SSCAuthenticatingRestConnection testSSCConnection(SSCAuthenticatingRestConnection conn) {
		conn.api(SSCApplicationVersionAPI.class)
			.queryApplicationVersions()
			.maxResults(1)
			.paramFields("id")
			.build().getUnique();
		return conn;
	}
	
	@Bean @FromSSC
	public SSCAttributeDefinitionHelper fromSSCAttributeDefinitionHelper() {
		return fromSSCConnection().api(SSCAttributeDefinitionAPI.class).getAttributeDefinitionHelper();
	}
	
	@Bean @FromSSC
	public SSCIssueTemplateHelper fromSSCIssueTemplateHelper() {
		return fromSSCConnection().api(SSCIssueTemplateAPI.class).getIssueTemplateHelper();
	}
}
