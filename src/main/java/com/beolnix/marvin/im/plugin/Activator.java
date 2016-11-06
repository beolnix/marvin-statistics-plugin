package com.beolnix.marvin.im.plugin;

import com.beolnix.marvin.im.plugin.statistics.StatisticsIMPlugin;
import com.beolnix.marvin.plugins.api.IMPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 * Created by beolnix on 11/9/2015.
 */
public class Activator implements BundleActivator {

    private StatisticsIMPlugin statisticsIMPlugin = new StatisticsIMPlugin();

    @Override
    public void start(BundleContext bundleContext) {
        bundleContext.registerService(IMPlugin.class.getName(), statisticsIMPlugin, null);
    }

    @Override
    public void stop(BundleContext context) {
        statisticsIMPlugin.shutdown();
    }
}
