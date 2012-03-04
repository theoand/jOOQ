/**
 * Copyright (c) 2009-2012, Lukas Eder, lukas.eder@gmail.com
 * All rights reserved.
 *
 * This software is licensed to you under the Apache License, Version 2.0
 * (the "License"); You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * . Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * . Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * . Neither the name "jOOQ" nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jooq.impl;

import java.util.HashMap;
import java.util.Map;

import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataTypeException;

/**
 * A central {@link DataType} registry
 *
 * @author Lukas Eder
 */
public final class DataTypes {

    private static final Object                         INIT_LOCK  = new Object();
    private static final Map<Class<?>, Converter<?, ?>> CONVERTERS = new HashMap<Class<?>, Converter<?, ?>>();

    // ------------------------------------------------------------------------
    // XXX: Public API used for initialisation from generated artefacts
    // ------------------------------------------------------------------------

    /**
     * Register a <code>Converter</code> for a custom type
     * <p>
     * This registers a {@link Converter} for a custom type. This converter will
     * be used by jOOQ to recognise custom types and to transform them back to
     * well-known database types (as defined in {@link Converter#fromType()}) in
     * rendering and binding steps
     * <p>
     * A custom type can be registered only once. Duplicate registrations will
     * be ignored
     * <p>
     * The converter class must provide a default constructor.
     *
     * @see #registerConverter(Class, Converter)
     */
    public static final synchronized <U> void registerConverter(Class<U> customType,
        Class<? extends Converter<?, U>> converter) {

        try {
            converter.getConstructor().setAccessible(true);
            registerConverter(customType, converter.newInstance());
        }
        catch (Exception e) {
            throw new DataTypeException("Cannot register converter", e);
        }
    }

    /**
     * Register a <code>Converter</code> for a custom type
     * <p>
     * This registers a {@link Converter} for a custom type. This converter will
     * be used by jOOQ to recognise custom types and to transform them back to
     * well-known database types (as defined in {@link Converter#fromType()}) in
     * rendering and binding steps
     * <p>
     * A custom type can be registered only once. Duplicate registrations will
     * be ignored
     */
    public static final synchronized <U> void registerConverter(Class<U> customType, Converter<?, U> converter) {

        // A converter can be registered only once
        if (!CONVERTERS.containsKey(customType)) {
            CONVERTERS.put(customType, converter);
        }
    }

    /**
     * Register a <code>Converter</code> for a field
     * <p>
     * This registers a converter for a specific field in your generated schema.
     * This converter will be used by jOOQ to convert database types to your own
     * custom types and deliver those types in {@link Record} and {@link Result}
     */
    public static final synchronized <T> void registerConverter(Field<T> field, Converter<T, ?> converter) {

    }

    // ------------------------------------------------------------------------
    // XXX: Internal API
    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    static final <U> Converter<?, U> converter(Class<U> customType) {
        return (Converter<?, U>) CONVERTERS.get(customType);
    }

    /**
     * No instances
     */
    private DataTypes() {}
}
