/*
 * Copyright 2013 Stephen Connolly.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.stephenc.jaxrsmustache;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;

/**
 * @author Stephen Connolly
 */
public class SmokeTest {

    private Server server;
    private LocalConnector connector;

    @Before
    public void startServer() throws Exception {
        server = new Server();
        connector = new LocalConnector(server);
//        final HttpConfiguration config = new HttpConfiguration();
//        final ServerConnector serverConnector = new ServerConnector(server, new HttpConnectionFactory(config));
//        serverConnector.setPort(8080);
        server.setConnectors(new Connector[]{connector});

        ServletHolder jersey = new ServletHolder(
                new ServletContainer(new ResourceConfig(BasicResource.class, MustacheMessageBodyWriter.class)));
        jersey.setName("Jersey");

        ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.addServlet(jersey, "/");
        servletContext.setContextPath("");
        server.setHandler(servletContext);
        server.start();
    }

    @After
    public void stopServer() throws Exception {
        server.stop();
        connector = null;
        server = null;
    }

    @Test
    public void smokes() throws Exception {
        final String response = connector.getResponses("GET / HTTP/1.0\r\n\r\n");
        assertThat(response, CoreMatchers.containsString("<title>Hello world</title>"));
    }

}
