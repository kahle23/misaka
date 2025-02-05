package baibao.extension.ip;

import kunlun.action.AbstractAction;
import kunlun.cache.Cache;
import kunlun.cache.support.NoCache;
import kunlun.util.Assert;
import kunlun.util.StringUtils;

import java.util.concurrent.Callable;

public abstract class AbstractIpLocationAction extends AbstractAction {
    private static final String PRIVATE_ADDR = "Private Address";
    private Cache cache = new NoCache();

    protected boolean isPrivateAddr(String ipAddress) {
        if (StringUtils.isBlank(ipAddress)) { return false; }
        if (ipAddress.startsWith("192.168.")) { return true; }
        if (ipAddress.startsWith("10.")) { return true; }
        for (int i = 16; i <= 31; i++) {
            if (ipAddress.startsWith("172." + i + ".")) { return true; }
        }
        return false;
    }

    public Cache getCache() {

        return cache;
    }

    public void setCache(Cache cache) {
        Assert.notNull(cache, "Parameter \"cache\" must not null. ");
        this.cache = cache;
    }

    protected abstract IpLocation doQuery(IpQuery ipQuery);

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        // Parameter validation.
        Assert.notNull(input, "Parameter \"parameter\" must not null. ");
        Assert.isSupport(input.getClass(), Boolean.TRUE, IpQuery.class);
        final IpQuery ipQuery = (IpQuery) input;
        // Judge whether it is a private address.
//        if (isPrivateAddr(ipQuery.getIpAddress())) {
//            IpLocation location = new IpLocation(ipQuery.getIpAddress(), PRIVATE_ADDR);
//            return BeanUtils.beanToBean(location, (Class<?>) type);
//        } // todo actionUtils
        // Query the geo address of the IP (preferably from the cache).
        return getCache().get(ipQuery.getIpAddress(), new Callable<IpLocation>() {
            @Override
            public IpLocation call() {
                return doQuery(ipQuery);
            }
        });
    }

}
