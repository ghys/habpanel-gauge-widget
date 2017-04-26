/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.habpanel.widget.gauge.internal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Dictionary;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a gauge widget for HABPanel along with
 * its associated iconset (icons from the KNX forum) and other necessary files.
 *
 * @author Yannick Schaus - Initial contribution
 */
public class GaugeWidgetProvider {

    private final Logger logger = LoggerFactory.getLogger(GaugeWidgetProvider.class);

    protected HttpService httpService;

    private ConfigurationAdmin configurationAdmin;

    /**
     * The service PID for altering HABPanel configuration. Don't change.
     */
    private static final String HABPANEL_CONFIG_FILTER = "(service.pid=org.openhab.habpanel)";

    /**
     * The widget ID to add to HABPanel's configuration.
     * It must begin with "widget." or it will be ignored.
     */
    private static final String WIDGET_ID = "widget.gauge";

    /**
     * The path to the JSON file containing the widget description in the bundle.
     */
    private static final String WIDGET_FILE = "widget/gauge.widget.json";

    /**
     * The path where static resources will be served.
     * Widget templates may include those files using relative URLs, including
     * stylesheets and scripts (using the oc-lazy-load directive).
     */
    private static final String STATIC_RESOURCES_ALIAS = "/habpanel-resources/gauge";

    private String getWidgetJSON(BundleContext context) throws Exception {
        URL widgetURL = context.getBundle().getEntry(WIDGET_FILE);
        BufferedReader in = new BufferedReader(new InputStreamReader(widgetURL.openStream()));
        return in.lines().collect(Collectors.joining("\n"));
    }

    protected void activate(BundleContext context) {

        // Register the static resources
        try {
            httpService.registerResources(STATIC_RESOURCES_ALIAS, "static", null);
        } catch (NamespaceException e) {
            logger.error("Error during static resources mapping", e);
        }

        // Register the widget
        try {
            Configuration[] configurations = configurationAdmin.listConfigurations(HABPANEL_CONFIG_FILTER);
            if (configurations != null) {
                Dictionary<String, Object> properties = configurations[0].getProperties();
                properties.put(WIDGET_ID, getWidgetJSON(context));
                configurations[0].update(properties);
            }
        } catch (Exception e) {
            logger.error("Error during widget provisioning", e);
        }

    }

    protected void deactivate(BundleContext context) {
        // Unregister the static resources
        httpService.unregister(STATIC_RESOURCES_ALIAS);

        // Unregister the widget
        try {
            Configuration[] configurations = configurationAdmin.listConfigurations(HABPANEL_CONFIG_FILTER);
            if (configurations != null) {
                Dictionary<String, Object> properties = configurations[0].getProperties();
                properties.remove(WIDGET_ID);
                configurations[0].update(properties);
            }
        } catch (Exception e) {
            logger.error("Error during widget unprovisioning: {}", e.getMessage());
        }
    }

    protected void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    protected void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

    protected void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    protected void unsetConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = null;
    }

}
