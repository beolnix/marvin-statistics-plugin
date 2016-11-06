package org.apache.felix.framework;

import org.osgi.framework.Bundle;

import java.net.URL;

/**
 * Created by beolnix on 11/02/16.
 */
public class BundlePathExtractor {

    public static String getBundlePath(Bundle bundle, URL url) throws Exception {
        return ((BundleImpl) bundle).getArchive().getLocation() + "!" + url.getPath();
    }
}
