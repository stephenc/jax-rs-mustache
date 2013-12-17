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

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Stephen Connolly
 */
@Provider
@Produces
public class MustacheMessageBodyWriter implements MessageBodyWriter<Object> {

    private final MustacheFactory factory;

    public MustacheMessageBodyWriter() {
        this.factory = new DefaultMustacheFactory();
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (View.class.isAssignableFrom(type)) {
            return true;
        }
        for (Annotation a : annotations) {
            if (a instanceof Template) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getSize(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated and ignored in JAX-RS 2.0
        return 0;
    }

    @Override
    public void writeTo(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        View<?> view = o instanceof View ? (View<?>) o : null;
        Template template = null;
        for (Annotation a : annotations) {
            if (a instanceof Template) {
                template = (Template) a;
                break;
            }
        }
        if (view == null && template == null) {
            throw new WebApplicationException(
                    new IllegalStateException("Cannot call writeTo with no @Template annotation"),
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
        final String name = template == null ? view.getTemplate() : template.value();
        if (name == null) {
            throw new WebApplicationException(
                    new IllegalStateException("Cannot call writeTo with a null template name"),
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
        Mustache mustache;
        try {
            mustache = factory.compile(name);
        } catch (RuntimeException e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
        OutputStreamWriter writer = new OutputStreamWriter(entityStream, charset == null ? "UTF-8" : charset);
        try {
            mustache.execute(writer, view == null ? o : view.getContext());
        } finally {
            writer.flush();
        }
    }
}
