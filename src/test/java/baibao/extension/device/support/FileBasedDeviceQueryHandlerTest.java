package baibao.extension.device.support;

import artoria.action.ActionUtils;
import artoria.exception.ExceptionUtils;
import baibao.extension.device.Device;
import baibao.extension.device.DeviceQuery;
import com.alibaba.fastjson.JSON;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Boolean.TRUE;

@Ignore
public class FileBasedDeviceQueryHandlerTest {
    private static Logger log = LoggerFactory.getLogger(FileBasedDeviceQueryHandlerTest.class);

    @Test
    public void test1() {
        try {
            new DeviceAutoConfiguration().afterPropertiesSet();

            DeviceQuery deviceQuery = new DeviceQuery("SM901");
            Device device = ActionUtils.execute(deviceQuery, "device-query", Device.class);
            log.info(JSON.toJSONString(device, TRUE));
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
