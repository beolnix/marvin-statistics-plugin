package org.eclipse.core.runtime;

import java.net.URL;

import org.apache.felix.framework.BundlePathExtractor;
import org.osgi.framework.Bundle;

import org.osgi.framework.FrameworkUtil;

public class FileLocator {
    private FileLocator() {}

    public static URL resolve(URL url) {
        if(url == null) {
            return null;
        }
        Bundle bundle = FrameworkUtil.getBundle(FileLocator.class).getBundleContext().getBundle();
        try {
            return new URL("jar:" + bundle.getLocation() + "!" + url.getPath());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
