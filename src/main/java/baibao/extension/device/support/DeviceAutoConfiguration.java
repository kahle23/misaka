/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.device.support;

import kunlun.action.ActionUtils;
import kunlun.crypto.EncryptUtils;
import kunlun.file.Csv;
import kunlun.io.util.IOUtils;
import kunlun.util.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

import static kunlun.common.constant.Charsets.STR_UTF_8;

@Deprecated
@Configuration
@ConditionalOnProperty(name = "baibao.device.enabled", havingValue = "true")
public class DeviceAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DeviceAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        Class<?> callingClass = DeviceAutoConfiguration.class;
        String resourceName = "device_info.data";
        InputStream inputStream =
                ClassLoaderUtils.getResourceAsStream(resourceName, callingClass);
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        byte[] decrypt = EncryptUtils.decrypt(byteArray);
        Csv csv = new Csv();
        csv.setCharset(STR_UTF_8);
        csv.readFromByteArray(decrypt);
        ActionUtils.registerAction("device-query", new FileBasedDeviceActionHandler(csv));
    }

    @Override
    public void destroy() throws Exception {
    }

}
