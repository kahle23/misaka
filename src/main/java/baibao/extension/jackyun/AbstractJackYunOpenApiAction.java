/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.jackyun;

import kunlun.net.http.HttpMethod;
import kunlun.time.DateUtils;
import kunlun.util.Assert;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 吉客云开放平台相关处理器.
 * @see <a href="https://open.jackyun.com/developer/document.html?alias=outsystem_openplat">吉客云开放平台 - 外部系统调用开放平台</>
 * @see <a href="https://open.jackyun.com/developer/apidocinfo.html">吉客云开放平台 - API接口</>
 * @author Kahle
 */
public abstract class AbstractJackYunOpenApiAction extends AbstractJackYunAction {
    private static final Logger log = LoggerFactory.getLogger(AbstractJackYunOpenApiAction.class);

    public AbstractJackYunOpenApiAction(String actionName) {

        super(actionName);
    }

    protected void handleConfig(JackYunConfig config) {
        Assert.notNull(config, "Parameter \"config\" must not null. ");
        Assert.notBlank(config.getAppSecret(), "Parameter \"config.appSecret\" must not blank. ");
        Assert.notBlank(config.getAddress(), "Parameter \"config.address\" must not blank. ");
        Assert.notBlank(config.getAppKey(), "Parameter \"config.appKey\" must not blank. ");
        if (StringUtils.isBlank(config.getContentType())) {
            config.setContentType("json");
        }
        if (StringUtils.isBlank(config.getVersion())) {
            config.setVersion("v1.0");
        }
    }

    @Override
    protected abstract JackYunConfig getInvokeConfig(Object input, String operation, Class<?> clazz);

    @Override
    protected Object invokeApi(Object input, String method, Class<?> clazz) {
        // 参数校验
        Assert.notBlank(method, "Parameter \"method\" must not blank. ");
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        Assert.notNull(clazz, "Parameter \"clazz\" must not null. ");
        // 获取要调用的吉客云开放平台的配置信息
        JackYunConfig config = getInvokeConfig(input, method, clazz);
        // 校验配置，并给部分配置设置默认值
        handleConfig(config);
        // 从入参中转换出 业务数据（返回的 bizData 大概率都是 String）
        Object bizData = convertInput(input, method, clazz);
        // 构造请求参数(name1=value1&name2=value2格式)
        SortedMap<String, String> sortedMap = new TreeMap<String, String>();
        sortedMap.put("method", method);
        sortedMap.put("appkey", config.getAppKey());
        sortedMap.put("version", config.getVersion());
        sortedMap.put("contenttype", config.getContentType());
        sortedMap.put("timestamp", DateUtils.format("yyyy-MM-dd HH:mm:ss"));
        sortedMap.put("bizcontent", String.valueOf(bizData));
        // 生成签名
        String sign = createSign(config.getAppSecret(), sortedMap);
        sortedMap.put("sign", sign);
        log.info("[JackYun]调用吉客云开放平台接口“{}”时生成的签名为“{}”。", method, sign);
        // 进行 http 调用
        String result = http(method, HttpMethod.POST, config.getAddress(), null, sortedMap);
        // 转换成输出结果
        return convertOutput(input, method, clazz, result);
    }

}
