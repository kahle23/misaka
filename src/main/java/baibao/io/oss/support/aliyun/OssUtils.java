package baibao.io.oss.support.aliyun;

import com.aliyun.oss.OSS;
import kunlun.io.oss.OssInfo;
import kunlun.io.oss.OssObject;
import kunlun.io.oss.OssStorage;
import kunlun.io.storage.StorageUtils;
import kunlun.util.ObjectUtils;

public class OssUtils {
    public static final String STORAGE_NAME = "aliyun-oss";

    public static OSS getNative() {

        return (OSS) ((OssStorage) StorageUtils.getStorage(STORAGE_NAME)).getNative();
    }

    public static boolean exist(Object key) {

        return StorageUtils.exist(STORAGE_NAME, key);
    }

    public static OssObject get(Object key) {

        return ObjectUtils.cast(StorageUtils.get(STORAGE_NAME, key));
    }

    public static OssInfo put(Object data) {

        return ObjectUtils.cast(StorageUtils.put(STORAGE_NAME, data));
    }

    public static OssInfo put(Object key, Object value) {

        return ObjectUtils.cast(StorageUtils.put(STORAGE_NAME, key, value));
    }

    public static Object delete(Object key) {

        return StorageUtils.delete(STORAGE_NAME, key);
    }

}
