/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.io.oss.support.obs;

import com.obs.services.ObsClient;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;
import kunlun.data.bean.BeanUtils;
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
import java.util.Map;

/**
 * https://support.huaweicloud.com/productdesc-obs/obs_03_0370.html
 */
public class ObsStorage extends AbstractOssStorage {
    private static final Logger log = LoggerFactory.getLogger(ObsStorage.class);
    private final ObsClient obsClient;

    public ObsStorage(ObsClient obsClient, Map<String, String> objectUrlPrefixes,
                      String defaultBucket) {
        super(objectUrlPrefixes, defaultBucket);
        Assert.notNull(obsClient, "Parameter \"obsClient\" must not null. ");
        this.obsClient = obsClient;
    }

    public ObsStorage(ObsClient obsClient, Map<String, String> objectUrlPrefixes) {

        this(obsClient, objectUrlPrefixes, null);
    }

    @Override
    public ObsClient getNative() {

        return obsClient;
    }

    @Override
    public boolean exist(Object key) {
        OssBase ossBase = getOssBase(key);
        String bucketName = ossBase.getBucketName();
        String objectKey = ossBase.getObjectKey();
        return obsClient.doesObjectExist(bucketName, objectKey);
    }

    @Override
    public OssObject get(Object key) {
        OssBase ossBase = getOssBase(key);
        String bucketName = ossBase.getBucketName();
        String objectKey = ossBase.getObjectKey();
        ObsObject obsObject = obsClient.getObject(bucketName, objectKey);
        if (obsObject == null) { return null; }
        return BeanUtils.beanToBean(obsObject, OssObjectImpl.class);
    }

    @Override
    public OssInfo put(Object data) {
        OssObject ossObject = convertToOssObject(data);
        InputStream inputStream = null;
        try {
            String bucketName = ossObject.getBucketName();
            String objectKey = ossObject.getObjectKey();
            inputStream = ossObject.getObjectContent();
            PutObjectResult putObjectResult = obsClient.putObject(bucketName, objectKey, inputStream);
            return buildOssInfo(putObjectResult.getBucketName(), putObjectResult.getObjectKey(),
                    putObjectResult.getObjectUrl(), putObjectResult);
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
        return obsClient.deleteObject(bucketName, objectKey);
    }

    @Override
    public Object list(Object conditions) {
        // todo obsClient listObjects
        // pattern
        String keyString = (String) conditions;
        ObjectListing objectListing = obsClient.listObjects(keyString);
        return objectListing.getObjects();
    }

}
