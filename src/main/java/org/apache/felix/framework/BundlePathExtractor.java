package org.apache.felix.framework;

import com.google.gag.annotation.remark.Hack;
import org.osgi.framework.Bundle;

import java.net.URL;

/**
 * Created by beolnix on 11/02/16.
 */

// this hack is needed to let spring component scan work in OSGi env.

@Hack
public class BundlePathExtractor {

    public static String getBundlePath(Bundle bundle, URL url) throws Exception {
        return ((BundleImpl) bundle).getArchive().getLocation() + "!" + url.getPath();
    }
}
