package baibao.io.oss.support.aliyun;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("spring.extension.storage.aliyun-oss")
public class AliYunOssProperties {
    private Boolean enabled;
    /**
     * AK in the access key.
     */
    private String accessKeyId;
    /**
     * SK in the access key.
     */
    private String secretAccessKey;
    /**
     * OBS endpoint.
     */
    private String endpoint;
    /**
     * The default bucket.
     */
    private String defaultBucket;
    /**
     * 对象 URL 前缀配置（key 为桶名称，value 为 url 前缀）。
     */
    private Map<String, String> objectUrlPrefixes;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDefaultBucket() {
        return defaultBucket;
    }

    public void setDefaultBucket(String defaultBucket) {
        this.defaultBucket = defaultBucket;
    }

    public Map<String, String> getObjectUrlPrefixes() {
        return objectUrlPrefixes;
    }

    public void setObjectUrlPrefixes(Map<String, String> objectUrlPrefixes) {
        this.objectUrlPrefixes = objectUrlPrefixes;
    }
}
