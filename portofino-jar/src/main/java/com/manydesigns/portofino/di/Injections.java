/*
 * Copyright (C) 2005-2011 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * Unless you have purchased a commercial license agreement from ManyDesigns srl,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * There are special exceptions to the terms and conditions of the GPL
 * as it is applied to this software. View the full text of the
 * exception in file OPEN-SOURCE-LICENSE.txt in the directory of this
 * software distribution.
 *
 * This program is distributed WITHOUT ANY WARRANTY; and without the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
 * or write to:
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307  USA
 *
 */

package com.manydesigns.portofino.di;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
 * @author Angelo Lupo          - angelo.lupo@manydesigns.com
 * @author Giampiero Granatella - giampiero.granatella@manydesigns.com
 * @author Alessio Stalla       - alessio.stalla@manydesigns.com
 */
public class Injections {
    public static final String copyright =
            "Copyright (c) 2005-2011, ManyDesigns srl";

    public static final Logger logger =
            LoggerFactory.getLogger(Injections.class);

    protected static final Map<Class, Field[]> cache =
            new HashMap<Class, Field[]>();

    public static void inject(Object obj,
                              ServletContext servletContext,
                              HttpServletRequest request) {
        if (obj == null) {
            logger.debug("Object is null");
            return;
        }
        Field[] fields = findAnnotatedFields(obj.getClass());
        for (Field field : fields) {
            Inject annotation = field.getAnnotation(Inject.class);
            assert annotation != null;
            String key = annotation.value();

            logger.debug("Searching in request");
            if (request != null) {
                Object value = request.getAttribute(key);
                if (value != null) {
                    logger.debug("Found '{}' in request: {}", key, value);
                    setFieldQueitly(obj, field, value);
                    continue;
                }
            }

            logger.debug("Searching in session");
            HttpSession session = (request == null)
                    ? null
                    : request.getSession(false);
            if (session != null) {
                Object value = session.getAttribute(key);
                if (value != null) {
                    logger.debug("Found '{}' in session: {}", key, value);
                    setFieldQueitly(obj, field, value);
                    continue;
                }
            }

            logger.debug("Searching in servlet context");
            if (servletContext != null) {
                Object value = servletContext.getAttribute(key);
                if (value != null) {
                    logger.debug("Found '{}' in servlet context: {}", key, value);
                    setFieldQueitly(obj, field, value);
                    continue;
                }
            }

            logger.debug("Value not found. Setting field to null.");
            setFieldQueitly(obj, field, null);
        }
    }

    public static void setFieldQueitly(@NotNull Object obj,
                                       @NotNull Field field,
                                       @Nullable Object value) {
        try {
            field.set(obj, value);
        } catch (Throwable e) {
            logger.warn("Cannot set field", e);
        }
    }

    public static Field[] findAnnotatedFields(@NotNull Class clazz) {
        Field[] result;
        synchronized (cache) {
            result = cache.get(clazz);
        }

        if (result != null) {
            return result;
        }

        List<Field> foundFields = new ArrayList<Field>();
        Class current = clazz;
        while (current != null) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    foundFields.add(field);
                }
            }
            current = current.getSuperclass();
        }
        result = new Field[foundFields.size()];
        foundFields.toArray(result);

        synchronized (cache) {
            cache.put(clazz, result);
        }

        return result;
    }
}