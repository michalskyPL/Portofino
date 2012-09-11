/*
* Copyright (C) 2005-2012 ManyDesigns srl.  All rights reserved.
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

package com.manydesigns.portofino;

import com.manydesigns.portofino.application.Application;
import com.manydesigns.portofino.database.platforms.DatabasePlatformsManager;
import com.manydesigns.portofino.dispatcher.Dispatch;
import com.manydesigns.portofino.dispatcher.Dispatcher;
import com.manydesigns.portofino.dispatcher.DispatcherLogic;
import com.manydesigns.portofino.dispatcher.PageInstance;
import com.manydesigns.portofino.i18n.ResourceBundleManager;
import com.manydesigns.portofino.model.Model;
import com.manydesigns.portofino.model.database.ConnectionProvider;
import com.manydesigns.portofino.pageactions.safemode.SafeModeAction;
import com.manydesigns.portofino.pages.ChildPage;
import com.manydesigns.portofino.pages.Layout;
import com.manydesigns.portofino.pages.Page;
import com.manydesigns.portofino.reflection.TableAccessor;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.hibernate.Session;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
 * @author Angelo Lupo          - angelo.lupo@manydesigns.com
 * @author Giampiero Granatella - giampiero.granatella@manydesigns.com
 * @author Alessio Stalla       - alessio.stalla@manydesigns.com
 */
public class TestApplication implements Application {
    public static final String copyright =
            "Copyright (c) 2005-2012, ManyDesigns srl";

    protected final String appId;
    protected final File rootDir;
    protected final File pagesDir;

    protected final BaseConfiguration portofinoProperties;

    protected final List<File> resourcesToDelete = new ArrayList<File>();

    public TestApplication(String appId) throws Exception {
        this.appId = appId;
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        if(tmpDir.isDirectory()) {
            rootDir = new File(tmpDir, "portofino-test-app-" + appId);
            ensureDirectoryExists(rootDir);
            rememberToDelete(rootDir);

            pagesDir = new File(rootDir, "pages");
            ensureDirectoryExists(pagesDir);
            rememberToDelete(pagesDir);

            Page rootPage = new Page();
            File rootPageFile = DispatcherLogic.savePage(pagesDir, rootPage);
            rememberToDelete(rootPageFile);
        } else {
            throw new Error("Not a directory: " + tmpDir.getAbsolutePath());
        }

        portofinoProperties = new BaseConfiguration();
        portofinoProperties.addProperty(PortofinoProperties.FALLBACK_ACTION_CLASS, SafeModeAction.class.getName());
    }

    private void rememberToDelete(File rootPageFile) {
        resourcesToDelete.add(0, rootPageFile);
    }

    private void ensureDirectoryExists(File dir) {
        if(!dir.mkdir() && !dir.isDirectory()) {
            throw new Error("Not a directory: " + dir.getAbsolutePath());
        }
    }

    public File addPage(String path, String name) throws Exception {
        Page rootPage;
        File rootDir;
        File childrenDir;
        Layout rootLayout;
        if(path == null || path.isEmpty() || path.equals("/")) {
            rootDir = childrenDir = pagesDir;
            rootPage = DispatcherLogic.getPage(rootDir);
            rootLayout = rootPage.getLayout();
        } else {
            Dispatcher dispatcher = new Dispatcher(this);
            Dispatch dispatch = dispatcher.getDispatch("", path);
            if(dispatch == null) {
                throw new Error("Invalid path: " + path);
            }
            PageInstance pageInstance = dispatch.getLastPageInstance();
            rootPage = pageInstance.getPage();
            rootLayout = pageInstance.getLayout();
            rootDir = pageInstance.getDirectory();
            childrenDir = pageInstance.getChildrenDirectory();
            ensureDirectoryExists(childrenDir);
            rememberToDelete(childrenDir);
        }
        File child = new File(childrenDir, name);
        ensureDirectoryExists(child);
        rememberToDelete(child);

        Page page = new Page();
        File pageFile = DispatcherLogic.savePage(child, page);
        rememberToDelete(pageFile);

        ChildPage childPage = new ChildPage();
        childPage.setName(name);
        rootLayout.getChildPages().add(childPage);
        DispatcherLogic.savePage(rootDir, rootPage);

        return pageFile;
    }

    public String getAppId() {
        return appId;
    }

    public String getName() {
        return appId;
    }

    public File getAppDir() {
        return rootDir;
    }

    public File getAppBlobsDir() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public File getPagesDir() {
        return pagesDir;
    }

    public File getAppDbsDir() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public File getAppModelFile() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public File getAppScriptsDir() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public File getAppStorageDir() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public File getAppWebDir() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void loadXmlModel() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void saveXmlModel() throws IOException, JAXBException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectionProvider getConnectionProvider(String databaseName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public BaseConfiguration getPortofinoProperties() {
        return portofinoProperties;
    }

    public DatabasePlatformsManager getDatabasePlatformsManager() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Model getModel() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void syncDataModel(String databaseName) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Session getSession(String databaseName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void closeSession(String databaseName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void closeSessions() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public TableAccessor getTableAccessor(String database, String entityName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResourceBundle getBundle(Locale locale) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResourceBundleManager getResourceBundleManager() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Configuration getAppConfiguration() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void initModel() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void shutdown() {
        for(File f : resourcesToDelete) {
            f.delete();
        }
    }
}
