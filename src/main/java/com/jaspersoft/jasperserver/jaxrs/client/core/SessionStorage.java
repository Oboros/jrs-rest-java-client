/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights  reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.jaxrs.client.core;

import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling.DefaultErrorHandler;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.JobExtension;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.JobSource;
import com.jaspersoft.jasperserver.jaxrs.client.filters.SessionOutputFilter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SessionStorage {

    private RestClientConfiguration configuration;
    private AuthenticationCredentials credentials;
    private WebTarget rootTarget;
    private String sessionId;

    public SessionStorage(RestClientConfiguration configuration, AuthenticationCredentials credentials) {
        this.configuration = configuration;
        this.credentials = credentials;
        init();
    }

    private void init() {
        Client client = ClientBuilder.newClient();
        rootTarget = client.target(configuration.getJasperReportsServerUrl());
        login();
        rootTarget.register(new SessionOutputFilter(sessionId));
    }

    private void login() {
        Form form = new Form();
        form
                .param("j_username", credentials.getUsername())
                .param("j_password", credentials.getPassword());

        WebTarget target = rootTarget.path("/rest/login");
        Response response = target.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        if (response.getStatus() == ResponseStatus.OK)
            this.sessionId = response.getCookies().get("JSESSIONID").getValue();
        else
            new DefaultErrorHandler().handleError(response);
    }

    public RestClientConfiguration getConfiguration() {
        return configuration;
    }

    public AuthenticationCredentials getCredentials() {
        return credentials;
    }

    public String getSessionId() {
        return sessionId;
    }

    public WebTarget getRootTarget() {
        return rootTarget;
    }

    /*public static void main(String[] args) throws InterruptedException {
        RestClientConfiguration configuration1 = new RestClientConfiguration("http://localhost:4444/jasperserver");
        configuration1.setContentMimeType(MimeType.XML);
        configuration1.setAcceptMimeType(MimeType.XML);
        JasperserverRestClient client = new JasperserverRestClient(configuration1);

        Session session = client.authenticate("jasperadmin", "jasperadmin");

        OperationResult<JobExtension> result = session
                .jobsService()
                .job(21281)
                .get();

        JobExtension job = result.getEntity();

        job.setLabel("NewScheduledReport");
        job.setDescription("blablabla");
        JobSource source = job.getSource();
        source.setReportUnitURI("/reports/samples/Employees");

        OperationResult<JobExtension> result1 = session
                .jobsService()
                .scheduleReport(job);

        job = result.getEntity();

        System.out.println(job);

    }*/
}
