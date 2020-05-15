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
package com.fortify.impexp.to.jira.processor.connection;

import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fortify.util.rest.connection.AbstractRestConnection;
import com.fortify.util.rest.connection.AbstractRestConnectionConfig;
import com.fortify.util.rest.connection.IRestConnectionBuilder;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;

public final class ToJiraRestConnection extends AbstractRestConnection {
	private static final Log LOG = LogFactory.getLog(ToJiraRestConnection.class);
	
	public ToJiraRestConnection(ToJiraRestConnectionConfig<?> config) {
		super(config);
	}
	
	@Override
	protected boolean doPreemptiveBasicAuthentication() {
		return true;
	}
	
	public String submitIssue(Map<String, Object> issueFields) {
		LOG.trace(String.format("[Jira] Submitting issue: %s", issueFields));
		WebTarget target = getBaseResource().path("/rest/api/latest/issue");
		JSONMap request = getIssueRequestData(issueFields);
		JSONMap submitResult = executeRequest(HttpMethod.POST, target, Entity.entity(request, "application/json"), JSONMap.class);
		
		String submittedIssueKey = submitResult.get("key", String.class);
		String submittedIssueBrowserURL = getIssueDeepLinkForIssueKey(submittedIssueKey);
		return submittedIssueBrowserURL;
	}

	private JSONMap getIssueRequestData(Map<String, Object> issueFields) {
		JSONMap request = new JSONMap();
		request.getOrCreateJSONMap("fields").putPaths(issueFields);
		return request;
	}
	
	public void updateIssueData(String issueId, Map<String, Object> issueFields) {
		LOG.trace(String.format("[Jira] Updating issue data for %s: %s", issueId, issueFields)); 
		WebTarget target = getBaseResource().path("/rest/api/latest/issue").path(issueId);
		executeRequest(HttpMethod.PUT, target, Entity.entity(getIssueRequestData(issueFields), "application/json"), null);
	}

	public String getTransitionId(String issueId, String transitionName) {
		if ( transitionName==null ) { return null; }
		WebTarget target = getBaseResource().path("/rest/api/latest/issue").path(issueId).path("transitions");
		JSONMap result = executeRequest(HttpMethod.GET, target, JSONMap.class);
		JSONList transitions = result.get("transitions", JSONList.class);
		String transitionId = transitions.mapValue("name", transitionName, "id", String.class);
		if ( transitionId==null ) {
			LOG.warn(String.format("[Jira] Transition %s does not exist in list of available transitions %s", transitionName, transitions.getValues("name", String.class)));
		}
		return transitionId;
	}
	
	public boolean transition(String issueId, String transitionName, String comment) {
		String transitionId = getTransitionId(issueId, transitionName);
		if ( transitionId == null ) { return false; }
		
		WebTarget target = getBaseResource().path("/rest/api/latest/issue").path(issueId).path("transitions");

		// { "transition" : { "id" : <transitionId> } }
		JSONMap request = new JSONMap();
		request.putPath("transition.id", transitionId);
		
		if ( StringUtils.isNotBlank(comment)) {
			// { "update" : { "comment" : [ { "add" : { "body" : <comment> } } ] }
			request.putPath("update.comment[].add.body", comment);
		}

		executeRequest(HttpMethod.POST, target, Entity.entity(request, "application/json"), JSONMap.class);
		return true;
	}
	
	public String getIssueKeyForJql(String jql) {
		WebTarget target = getBaseResource()
				.path("/rest/api/latest/search")
				.queryParam("jql", jql) // TODO Use post instead to support long queries?
				.queryParam("maxResults", 2);
		JSONList issues = executeRequest(HttpMethod.GET, target, JSONMap.class).getOrCreateJSONList("issues");
		if ( issues.size() > 1 ) { throw new IllegalStateException("Multiple JIRA tickets found matching jql '"+jql+"'"); }
		return issues.size()==0 ? null : issues.getValues("key", String.class).get(0);
	}
	
	public final JSONMap getIssueFields(String issueId, String... fields) {
		WebTarget target = getBaseResource().path("/rest/api/latest/issue").path(issueId);
		if ( fields!=null ) {
			target = target.queryParam("fields", StringUtils.join(fields, ","));
		}
		return executeRequest(HttpMethod.GET, target, JSONMap.class).getOrCreateJSONMap("fields");
	}
	
	private String getIssueDeepLinkForIssueKey(String issueKey) {
		return getBaseResource().path("/browse/").path(issueKey).getUri().toString();
	}

	
	public static final String getIssueKeyForIssueDeepLink(String deepLink) {
		return deepLink.substring(deepLink.lastIndexOf('/'));
	}
	
	/**
	 * This method returns an {@link JiraRestConnectionBuilder} instance
	 * that allows for building {@link ToJiraRestConnection} instances.
	 * @return
	 */
	public static final JiraRestConnectionBuilder builder() {
		return new JiraRestConnectionBuilder();
	}
	
	/**
	 * This class provides a builder pattern for configuring an {@link ToJiraRestConnection} instance.
	 * It re-uses builder functionality from {@link AbstractRestConnectionConfig}, and adds a
	 * {@link #build()} method to build an {@link ToJiraRestConnection} instance.
	 * 
	 * @author Ruud Senden
	 */
	public static final class JiraRestConnectionBuilder extends ToJiraRestConnectionConfig<JiraRestConnectionBuilder> implements IRestConnectionBuilder<ToJiraRestConnection> {
		@Override
		public ToJiraRestConnection build() {
			return new ToJiraRestConnection(this);
		}
	}
}
