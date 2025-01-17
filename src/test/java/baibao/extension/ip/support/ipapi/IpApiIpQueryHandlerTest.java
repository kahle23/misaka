package baibao.extension.ip.support.ipapi;

import baibao.extension.ip.IpQuery;
import cn.hutool.json.JSONUtil;
import kunlun.action.ActionUtils;
import kunlun.cache.support.SimpleCache;
import kunlun.cache.support.SimpleCacheConfig;
import kunlun.data.ReferenceType;
import kunlun.data.json.JsonUtils;
import kunlun.data.json.support.FastJsonHandler;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Ignore
public class IpApiIpQueryHandlerTest {
    private static final Logger log = LoggerFactory.getLogger(IpApiIpQueryHandlerTest.class);
    private static final String IP_QUERY_NAME = "ip-query-ipapi";

    static {
        JsonUtils.registerHandler(JsonUtils.getDefaultHandlerName(), new FastJsonHandler());
        IpApiIpActionHandler handler = new IpApiIpActionHandler();
        handler.setCache(new SimpleCache(new SimpleCacheConfig(ReferenceType.SOFT, 3L, TimeUnit.DAYS)));
        ActionUtils.registerHandler(IP_QUERY_NAME, handler);
    }

    @Test
    public void test1() {
        IpApiIpLocation ipLocation = ActionUtils.execute(IP_QUERY_NAME
                , new IpQuery("223.98.40.191"), IpApiIpLocation.class);
        log.info("{}", JSONUtil.toJsonPrettyStr(ipLocation));

        ipLocation = ActionUtils.execute(IP_QUERY_NAME
                , new IpQuery("106.57.23.1"), IpApiIpLocation.class);
        log.info("{}", JSONUtil.toJsonPrettyStr(ipLocation));

        /*
        ipLocation = ActionUtils.execute(IP_QUERY_NAME
                , new IpQuery("117.136.7.206"), IpApiIpLocation.class);
        log.info("{}", JSONUtil.toJsonPrettyStr(ipLocation));

        ipLocation = ActionUtils.execute(IP_QUERY_NAME
                , new IpQuery("220.243.135.165"), IpApiIpLocation.class);
        log.info("{}", JSONUtil.toJsonPrettyStr(ipLocation));

        ipLocation = ActionUtils.execute(IP_QUERY_NAME
                , new IpQuery("122.238.172.155"), IpApiIpLocation.class);
        log.info("{}", JSONUtil.toJsonPrettyStr(ipLocation));*/

        ipLocation = ActionUtils.execute(IP_QUERY_NAME
                , new IpQuery("192.168.23.23"), IpApiIpLocation.class);
        log.info("{}", JSONUtil.toJsonPrettyStr(ipLocation));
    }

    @Test
    public void test2() {
        IpApiIpLocation ipLocation = ActionUtils.execute(IP_QUERY_NAME
                , new IpQuery("120.231.22.221"), IpApiIpLocation.class);
        log.info("{}", JSONUtil.toJsonPrettyStr(ipLocation));

        ipLocation = ActionUtils.execute(IP_QUERY_NAME
                , new IpQuery("120.231.22.221"), IpApiIpLocation.class);
        log.info("{}", JSONUtil.toJsonPrettyStr(ipLocation));
    }

}
