/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.ip.support.ipapi;

import kunlun.action.ActionUtils;
import kunlun.cache.CacheUtils;
import kunlun.cache.support.SimpleCache;
import kunlun.cache.support.SimpleCacheConfig;
import kunlun.data.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Network physical address auto configuration.
 * @author Kahle
 */
@Configuration
public class IpApiAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(IpApiAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        String actionName = "ip-query-ipapi";
        SimpleCacheConfig config = new SimpleCacheConfig(ReferenceType.SOFT, 3L, TimeUnit.DAYS);
        CacheUtils.registerCache(actionName, new SimpleCache(config));
        IpApiIpActionHandler handler = new IpApiIpActionHandler();
        handler.setCache(CacheUtils.getCache(actionName));
        ActionUtils.registerAction(actionName, handler);
    }

    @Override
    public void destroy() throws Exception {

    }

}
