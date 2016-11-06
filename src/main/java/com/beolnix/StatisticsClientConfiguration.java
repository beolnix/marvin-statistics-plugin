package com.beolnix;

import com.beolnix.marvin.plugins.api.PluginConfig;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.Collections;
import java.util.List;

import static com.beolnix.marvin.im.plugin.statistics.configuration.Constants.PROP_SERVICE_URL;

/**
 * Created by beolnix on 12/02/16.
 */

@Configuration
public class StatisticsClientConfiguration extends RibbonClientConfiguration {

    @Autowired
    private PluginConfig pluginConfig;

    @Autowired
    private Logger logger;

    @Override
    public ServerList<Server> ribbonServerList(IClientConfig config) {
        disableCertificates();
        String baseUrl = pluginConfig.getPropertyByName(PROP_SERVICE_URL);
        logger.info("Got statistics service url: " + baseUrl + " for statistics plugin.");

        final Server service = new Server(baseUrl, 443);

        return new ServerList<Server>() {
            @Override
            public List<Server> getInitialListOfServers() {
                return Collections.singletonList(service);
            }

            @Override
            public List<Server> getUpdatedListOfServers() {
                return Collections.singletonList(service);
            }
        };
    }

    public void disableCertificates() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }



}
