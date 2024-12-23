package baibao.io.oss.support.aliyun;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import kunlun.io.storage.StorageUtils;
import kunlun.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@Configuration
@ConditionalOnClass(OSS.class)
@ConditionalOnProperty(name = "spring.extension.storage.aliyun-oss.enabled", havingValue = "true")
@EnableConfigurationProperties({AliYunOssProperties.class})
public class AliYunOssAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(AliYunOssAutoConfiguration.class);
    private final AliYunOssProperties ossProperties;

    @Autowired
    public AliYunOssAutoConfiguration(AliYunOssProperties ossProperties) {
        Assert.notNull(ossProperties, "Parameter \"ossProperties\" must not null. ");
        Assert.notBlank(ossProperties.getAccessKeyId(), "Parameter \"accessKeyId\" must not blank. ");
        Assert.notBlank(ossProperties.getSecretAccessKey(), "Parameter \"secretAccessKey\" must not blank. ");
        Assert.notBlank(ossProperties.getEndpoint(), "Parameter \"endpoint\" must not blank. ");
        this.ossProperties = ossProperties;
    }

    @Bean
    public OSS ossClient() {
        Map<String, String> urlPrefixes = ossProperties.getObjectUrlPrefixes();
        if (urlPrefixes == null) { urlPrefixes = Collections.emptyMap(); }
        String secretAccessKey = ossProperties.getSecretAccessKey();
        String defaultBucket = ossProperties.getDefaultBucket();
        String accessKeyId = ossProperties.getAccessKeyId();
        String endpoint = ossProperties.getEndpoint();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, secretAccessKey);
        AliYunOssStorage storage = new AliYunOssStorage(ossClient, urlPrefixes, defaultBucket);
        StorageUtils.register(OssUtils.STORAGE_NAME, storage);
        return ossClient;
    }

}
