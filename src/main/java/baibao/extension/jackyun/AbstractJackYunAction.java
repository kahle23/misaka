/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.jackyun;

import kunlun.action.support.AbstractAutoAction;
import kunlun.data.Dict;
import kunlun.data.json.JsonUtils;
import kunlun.exception.ExceptionUtils;
import kunlun.net.http.HttpMethod;
import kunlun.net.http.HttpResponse;
import kunlun.net.http.HttpUtils;
import kunlun.net.http.support.SimpleRequest;
import kunlun.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Map;
import java.util.SortedMap;

import static kunlun.common.constant.Charsets.UTF_8;

/**
 * 抽象的吉客云相关处理器.
 * @author Kahle
 */
public abstract class AbstractJackYunAction extends AbstractAutoAction {
    private static final Logger log = LoggerFactory.getLogger(AbstractJackYunOpenApiAction.class);

    public AbstractJackYunAction(String actionName) {

        super(actionName);
    }

    protected abstract Object getInvokeConfig(Object input, String operation, Class<?> clazz);

    protected String sign(Object data) {
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] textBytes = String.valueOf(data).getBytes(UTF_8);
            byte[] md5Bytes = md5.digest(textBytes);
            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = (md5Byte) & 0xff;
                if (val < 16) { hexValue.append("0"); }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    protected String createSign(String appSecret, SortedMap<String, String> sortedMap) {
        // 构建待签名的字符串
        StringBuilder signStrBuilder = new StringBuilder(appSecret);
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            signStrBuilder.append(entry.getKey()).append(entry.getValue());
        }
        signStrBuilder.append(appSecret);
        // 生成签名
        return sign(signStrBuilder.toString().toLowerCase());
    }

    protected String http(String info, HttpMethod method, String url, Map<?, ?> headers, Object data) {
        Assert.isInstanceOf(Map.class, data, "Parameter \"data\" must is instance of map. ");
        SimpleRequest request = SimpleRequest.of(method, url);
        request.addParameters((Map<?, ?>) data);
        HttpResponse response = HttpUtils.execute(request);
        return response.getBodyAsString();
    }

    protected Object invokeApi(Object input, String method, Class<?> clazz) {

        throw new UnsupportedOperationException();
    }

    protected Object convertInput(Object input, String operation, Class<?> clazz) {
        if (input instanceof String) {
            return String.valueOf(input);
        }
        else {
            return JsonUtils.toJsonString(input);
        }
    }

    protected Object convertOutput(Object input, String operation, Class<?> clazz, String result) {
        if (result == null) { return null; }
        if (CharSequence.class.isAssignableFrom(clazz)) {
            return result;
        }
        else if (Dict.class.isAssignableFrom(clazz)) {
            return JsonUtils.parseObject(result, Dict.class);
        }
        else if (Map.class.isAssignableFrom(clazz)) {
            return JsonUtils.parseObject(result, Map.class);
        }
        else {
            return JsonUtils.parseObject(result, clazz);
        }
    }

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {

        return invokeApi(input, strategy, null); // todo actionUtils
    }

}
