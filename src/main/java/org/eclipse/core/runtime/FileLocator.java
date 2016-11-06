package org.eclipse.core.runtime;

import com.google.gag.annotation.remark.Hack;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import java.net.URL;

// this hack is needed to let spring component scan work in OSGi env.
// spring literally checks if there is a org.eclipse.core.runtime.FileLocator class in the
// class path and uses it if it does exist
//
// https://github.com/spring-projects/spring-framework/blob/bc14c5ba83e1f211628456bbccce7b2531aac58c/spring-core/src/main/java/org/springframework/core/io/support/PathMatchingResourcePatternResolver.java#L189
//
@Hack
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
