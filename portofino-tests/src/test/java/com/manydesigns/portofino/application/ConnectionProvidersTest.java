/*
 * Copyright (C) 2005-2013 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.manydesigns.portofino.application;

import com.manydesigns.portofino.AbstractPortofinoTest;
import com.manydesigns.portofino.model.database.Database;
import com.manydesigns.portofino.model.database.JdbcConnectionProvider;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.List;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla       - alessio.stalla@manydesigns.com
*/
public class ConnectionProvidersTest extends AbstractPortofinoTest {
    public static final String copyright =
            "Copyright (c) 2005-2013, ManyDesigns srl";

    @Override
    public void setUp() throws Exception {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testConnectionProvider() throws IOException, JAXBException {
        List<Database> databases = model.getDatabases();
        assertEquals(3, databases.size());

        JdbcConnectionProvider conn = new JdbcConnectionProvider();
        conn.setDriver("org.h2.Driver");
        conn.setUrl("jdbc:h2:mem:test");
        conn.setUsername("manydesigns");
        conn.setPassword("manydesigns");
        Database database = new Database();
        database.setDatabaseName("test");
        database.setConnectionProvider(conn);
        conn.setDatabase(database);

        databases.add(database);
        application.saveXmlModel();

//        File connectionsFile = application.getAppConnectionsFile();
        File connectionsFile = null;

        assertEquals(4, databases.size());
        InputStream is = null;
        try {
            is = new FileInputStream(connectionsFile);
            String file = convertStreamToString(is);
            assertTrue(file.contains("<jdbcConnection driver=\"org.h2.Driver\" password=\"manydesigns\" url=\"jdbc:h2:mem:test\" username=\"manydesigns\" databaseName=\"test\"/>"));
        } finally {
            IOUtils.closeQuietly(is);
        }

        conn = new JdbcConnectionProvider();
        conn.setDriver("org.h2.Driver");
        conn.setUrl("jdbc:h2:mem:test2");
        conn.setUsername("manydesigns2");
        conn.setPassword("manydesigns2");

        databases.remove(database);
        application.saveXmlModel();
        assertEquals(3, databases.size());

        database = new Database();
        database.setDatabaseName("test");
        database.setConnectionProvider(conn);
        conn.setDatabase(database);

        databases.add(database);
        application.saveXmlModel();
        assertEquals(4, databases.size());
        is = null;
        try {
            is = new FileInputStream(connectionsFile);
            String file = convertStreamToString(is);
            assertTrue(file.contains("<jdbcConnection driver=\"org.h2.Driver\" password=\"manydesigns2\" url=\"jdbc:h2:mem:test2\" username=\"manydesigns2\" databaseName=\"test\"/>"));
        } finally {
            IOUtils.closeQuietly(is);
        }

        databases.remove(database);
        application.saveXmlModel();
        assertEquals(3, databases.size());
        is = null;
        try {
            is = new FileInputStream(connectionsFile);
            String file = convertStreamToString(is);
            assertFalse(file.contains("<jdbcConnection driver=\"org.h2.Driver\" password=\"manydesigns\" url=\"jdbc:h2:mem:test\" username=\"manydesigns\" databaseName=\"test\"/>"));
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private String convertStreamToString(InputStream is)
            throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}