package baibao.extension.ip;

import kunlun.action.support.AbstractClassicActionHandler1;
import kunlun.cache.Cache;
import kunlun.cache.support.NoCache;
import kunlun.data.bean.BeanUtils;
import kunlun.util.Assert;
import kunlun.util.StringUtils;

import java.lang.reflect.Type;
import java.util.concurrent.Callable;

public abstract class AbstractIpLocationHandler extends AbstractClassicActionHandler1 {
    private static final String PRIVATE_ADDR = "Private Address";

    protected boolean isPrivateAddr(String ipAddress) {
        if (StringUtils.isBlank(ipAddress)) { return false; }
        if (ipAddress.startsWith("192.168.")) { return true; }
        if (ipAddress.startsWith("10.")) { return true; }
        for (int i = 16; i <= 31; i++) {
            if (ipAddress.startsWith("172." + i + ".")) { return true; }
        }
        return false;
    }

    protected Cache getCache() {

        return new NoCache();
    }

    protected abstract IpLocation doQuery(IpQuery ipQuery);

    @Override
    public Object execute(Object input, String strategy, Type type) {
        // Parameter validation.
        Assert.notNull(input, "Parameter \"parameter\" must not null. ");
        Assert.isSupport((Class<?>) type, Boolean.TRUE, IpLocation.class);
        Assert.isSupport(input.getClass(), Boolean.TRUE, IpQuery.class);
        final IpQuery ipQuery = (IpQuery) input;
        // Judge whether it is a private address.
        if (isPrivateAddr(ipQuery.getIpAddress())) {
            IpLocation location = new IpLocation(ipQuery.getIpAddress(), PRIVATE_ADDR);
            return BeanUtils.beanToBean(location, (Class<?>) type);
        }
        // Query the geo address of the IP (preferably from the cache).
        return getCache().get(ipQuery.getIpAddress(), new Callable<IpLocation>() {
            @Override
            public IpLocation call() {
                return doQuery(ipQuery);
            }
        });
    }

}
