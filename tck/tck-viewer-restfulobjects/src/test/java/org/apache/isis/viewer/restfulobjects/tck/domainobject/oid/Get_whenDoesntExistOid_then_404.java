/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.viewer.restfulobjects.tck.domainobject.oid;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.webserver.WebServer;
import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.LinkRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulClient;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulResponse;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulResponse.HttpStatusCode;
import org.apache.isis.viewer.restfulobjects.applib.domainobjects.DomainObjectResource;
import org.apache.isis.viewer.restfulobjects.tck.IsisWebServerRule;
import org.apache.isis.viewer.restfulobjects.tck.Util;

public class Get_whenDoesntExistOid_then_404 {

    @Rule
    public IsisWebServerRule webServerRule = new IsisWebServerRule();

    protected RestfulClient client;

    @Before
    public void setUp() throws Exception {
        final WebServer webServer = webServerRule.getWebServer();
        client = new RestfulClient(webServer.getBase());
    }

    @Test
    public void usingClientFollow() throws Exception {

        // given
        final LinkRepresentation link = Util.serviceActionListInvokeFirstReference(client, "PrimitiveValuedEntities");
        link.withHref("http://localhost:39393/objects/PRMV/nonExistent");
        
        // when
        final RestfulResponse<JsonRepresentation> restfulResp = client.follow(link);
        
        // then
        then(restfulResp);
        
    }

    @Test
    public void usingResourceProxy() throws Exception {

        // when
        final DomainObjectResource objectResource = client.getDomainObjectResource();

        final Response response = objectResource.object("PRMV", "nonExistent");
        RestfulResponse<JsonRepresentation> restfulResp = RestfulResponse.of(response);

        then(restfulResp);
        
    }
    
    private void then(final RestfulResponse<JsonRepresentation> restfulResp) {
        assertThat(restfulResp.getStatus(), is(HttpStatusCode.NOT_FOUND));
    }


}
