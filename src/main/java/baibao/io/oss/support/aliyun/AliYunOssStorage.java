package baibao.io.oss.support.aliyun;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import kunlun.exception.ExceptionUtils;
import kunlun.io.oss.OssBase;
import kunlun.io.oss.OssInfo;
import kunlun.io.oss.OssObject;
import kunlun.io.oss.support.AbstractOssStorage;
import kunlun.io.oss.support.OssObjectImpl;
import kunlun.util.Assert;
import kunlun.util.CloseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AliYunOssStorage extends AbstractOssStorage {
    private static final Logger log = LoggerFactory.getLogger(AliYunOssStorage.class);
    private final OSS ossClient;

    public AliYunOssStorage(OSS ossClient, Map<String, String> objectUrlPrefixes,
                            String defaultBucket) {
        super(objectUrlPrefixes, defaultBucket);
        Assert.notNull(ossClient, "Parameter \"ossClient\" must not null. ");
        this.ossClient = ossClient;
        initializeHeaders();
    }

    public AliYunOssStorage(OSS ossClient, Map<String, String> objectUrlPrefixes) {

        this(ossClient, objectUrlPrefixes, null);
    }

    private static final List<String> OSS_HEADER_LIST = new ArrayList<String>();

    private void initializeHeaders() {
        try {
            Field[] fields = OSSHeaders.class.getFields();
            for (Field field : fields) {
                Object obj = field.get(null);
                String value = String.valueOf(obj);
                value = value.trim().toLowerCase();
                OSS_HEADER_LIST.add(value);
            }
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    private ObjectMetadata convert(Map<String, Object> metadata) {
        if (metadata == null) { return null; }
        ObjectMetadata result = new ObjectMetadata();
        if (MapUtil.isEmpty(metadata)) { return result; }
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (StrUtil.isBlank(key)) { continue; }
            if (value == null) { continue; }
            String tmpKey = key.trim().toLowerCase();
            if (OSS_HEADER_LIST.contains(tmpKey)) {
                result.setHeader(key, value);
            }
            else {
                result.addUserMetadata(key, String.valueOf(value));
            }
        }
        return result;
    }

    @Override
    public OSS getNative() {

        return ossClient;
    }

    @Override
    public boolean exist(Object key) {

        return super.exist(key);
    }

    @Override
    public OssObject get(Object key) {
        OssBase ossBase = getOssBase(key);
        String bucketName = ossBase.getBucketName();
        String objectKey = ossBase.getObjectKey();
        OSSObject ossObject = ossClient.getObject(bucketName, objectKey);
        if (ossObject == null) { return null; }
        OssObjectImpl result = new OssObjectImpl();
        result.setBucketName(ossObject.getBucketName());
        result.setObjectKey(ossObject.getKey());
        result.setMetadata(ossObject.getObjectMetadata().getRawMetadata());
        result.setObjectContent(ossObject.getObjectContent());
        return result;
    }

    @Override
    public OssInfo put(Object data) {
        OssObject ossObject = convertToOssObject(data);
        InputStream inputStream = null;
        try {
            String bucketName = ossObject.getBucketName();
            String objectKey = ossObject.getObjectKey();
            inputStream = ossObject.getObjectContent();
            Object metadata = ossObject.getMetadata();
            PutObjectResult putObjectResult = ossClient
                    .putObject(bucketName, objectKey, inputStream, convert((Map<String, Object>) metadata));
            return buildOssInfo(bucketName, objectKey, null, putObjectResult);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
        finally {
            CloseUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public Object delete(Object key) {
        OssBase ossBase = getOssBase(key);
        String bucketName = ossBase.getBucketName();
        String objectKey = ossBase.getObjectKey();
        ossClient.deleteObject(bucketName, objectKey);
        return null;
    }

    @Override
    public Object list(Object conditions) {
        // todo ossClient listObjects
        return super.list(conditions);
    }

}
