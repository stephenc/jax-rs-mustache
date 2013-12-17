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

/**
 * Use a {@link View} if you want to dynamically choose the template for the response.
 */
public abstract class View<C> {
    /**
     * Returns the template name, must be a valid resource path in the context classloader.
     *
     * @return The template name, must be a valid resource path in the context classloader.
     */
    public abstract String getTemplate();

    /**
     * Returns the context to evaluate the template with.
     *
     * @return the context to evaluate the template with.
     */
    public abstract C getContext();
}
