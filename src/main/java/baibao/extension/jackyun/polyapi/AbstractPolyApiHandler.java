/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.jackyun.polyapi;

import baibao.extension.jackyun.AbstractJackYunHandler;
import kunlun.data.Dict;
import kunlun.data.json.JsonUtils;
import kunlun.net.http.HttpMethod;
import kunlun.time.DateUtils;
import kunlun.util.Assert;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 菠萝派平台的相关处理器.
 * @see <a href="http://login.jackyun.com/wkdoc/share/doc/doc.html?id=86A230D1E4D6912230E0EEB12249D276">菠萝派自建商城接口文档</a>
 * @see <a href="https://api.jackyun.com/wkdoc/share/doc/doc.html?id=5EEF82702986F8B5903E4E49EDB494D9">菠萝派自建商城消息推送接口</a>
 * @author Kahle
 */
public abstract class AbstractPolyApiHandler extends AbstractJackYunHandler {
    private static final Logger log = LoggerFactory.getLogger(AbstractPolyApiHandler.class);

    public AbstractPolyApiHandler(String actionName) {

        super(actionName);
    }

    /**
     * 获取要调用的 URL 地址.
     *
     * 管家推送接口地址（需要进行拼接）
     * 拼接逻辑：http://api.polyapi.com/OrderNotify/Notify/Business_GetNotifyOrder_PolyMall + _ + username
     *
     * 吉客云推送接口地址
     * 地址信息：https://polyapi.jackyun.com/openapi/do/notify/STANDARD/32/198
     *
     * @param config 菠萝派的配置信息
     * @return 要调用的 URL 地址
     */
    protected String getApiAddress(PolyApiConfig config) {
        // 为什么不要求使用者直接把拼接好的 URL 写在配置文件中呢？
        // 地址应该不会改，但其他参数都可能会改，拼接好得话就得多修改了一个字段了。
        //return String.format("%s_%s", config.getAddress(), config.getUsername());
        return config.getAddress();
    }

    protected void handleConfig(PolyApiConfig config) {
        Assert.notNull(config, "Parameter \"config\" must not null. ");
        Assert.notBlank(config.getAddress(), "Parameter \"config.address\" must not blank. ");
        Assert.notBlank(config.getAppKey(), "Parameter \"config.appKey\" must not blank. ");
        Assert.notBlank(config.getAppSecret(), "Parameter \"config.appSecret\" must not blank. ");
        Assert.notBlank(config.getToken(), "Parameter \"config.token\" must not blank. ");
        Assert.notBlank(config.getUsername(), "Parameter \"config.username\" must not blank. ");
        Assert.notBlank(config.getTargetType(), "Parameter \"config.targetType\" must not blank. ");
    }

    protected boolean validateSign(PolyApiReq polyApiReq) {
        String token = polyApiReq.getToken();
        String sign = polyApiReq.getSign();
        // 获取菠萝派平台的配置信息
        PolyApiConfig config = getInvokeConfig(polyApiReq, "validateSign", Boolean.class);
        // 校验并处理配置
        handleConfig(config);
        // 签名参数
        SortedMap<String, String> sortedMap = new TreeMap<String, String>();
        sortedMap.put("method", polyApiReq.getMethod());
        sortedMap.put("appkey", config.getAppKey());
        sortedMap.put("token",  token);
        sortedMap.put("bizcontent", polyApiReq.getBizContent());
        // 生成签名
        String calcSign = createSign(config.getAppSecret(), sortedMap);
        polyApiReq.setCalcSign(calcSign);
        // 判断签名是否相等
        return StringUtils.equals(sign, calcSign);
    }

    /**
     * 正常情况下，此方法的 input、operation 和 clazz 应该都是用不到的。
     * 一般都是直接从请求的上下文中获取具体的标识，然后从数据库查询对应的配置信息。
     *
     * 不过菠萝派自建商城的大部分接口都是它来调用我方接口，它可不会把某种我方标识放在请求头，
     * 因此在比如验签时，则是根据参数中的标识，比如“token”去数据库查询对应的配置对象。
     *
     * @param input 调用 action 方法时的入参对象
     * @param operation 调用 action 方法时的 操作标识，相当于内部的方法名
     * @param clazz 调用 action 方法时的返回值类型
     * @return 获取到的菠萝派平台的配置对象，或者直接报错
     */
    @Override
    protected abstract PolyApiConfig getInvokeConfig(Object input, String operation, Class<?> clazz);

    @Override
    protected Object invokeApi(Object input, String method, Class<?> clazz) {
        // 参数校验
        Assert.notBlank(method, "Parameter \"method\" must not blank. ");
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        Assert.notNull(clazz, "Parameter \"clazz\" must not null. ");
        // 获取要调用的菠萝派平台的配置信息
        PolyApiConfig config = getInvokeConfig(input, method, clazz);
        // 校验并处理配置
        handleConfig(config);
        // 从入参中转换出 业务数据（返回的 bizData 大概率都是 String）
        Object bizData = convertInput(input, method, clazz);
        // 构造数据对象
        Dict dataDict = Dict.of("uerName", config.getUsername())
                .set("messageType", method)
                .set("targetType", config.getTargetType())
                .set("messageContent", String.valueOf(bizData))
        ;
        // 构造请求参数(name1=value1&name2=value2格式)
        SortedMap<String, String> sortedMap = new TreeMap<String, String>();
        sortedMap.put("appkey", config.getAppKey());
        sortedMap.put("token", config.getToken());
        sortedMap.put("data", JsonUtils.toJsonString(dataDict));
        sortedMap.put("timestamp", DateUtils.format("yyyy-MM-dd HH:mm:ss"));
        // 生成签名
        String sign = createSign(config.getAppSecret(), sortedMap);
        sortedMap.put("sign", sign);
        log.info("[PolyApi]调用菠萝派平台接口“{}”时生成的签名为“{}”。", method, sign);
        // 进行 http 调用（注意需要对 URL 进行拼接，来此历史代码，文档中没有）
        String result = http(method, HttpMethod.POST, getApiAddress(config), null, sortedMap);
        // 转换成输出结果
        return convertOutput(input, method, clazz, result);
    }

    @Override
    public Object execute(Object input, String strategy, Type type) {
        if ("validateSign".equals(strategy)) {
            Assert.notNull(input, "Parameter \"input\" must not null. ");
            Assert.isInstanceOf(PolyApiReq.class, input
                    , "Parameter \"input\" must is instance of PolyApiReq. ");
            Assert.isSupport((Class<?>) type, Boolean.FALSE, Boolean.class);
            return validateSign((PolyApiReq) input);
        }
        else { return invokeApi(input, strategy, (Class<?>) type); }
    }

}
