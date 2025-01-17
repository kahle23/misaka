/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.device.support;

import baibao.extension.device.Device;
import baibao.extension.device.DeviceQuery;
import kunlun.action.AbstractActionHandler;
import kunlun.data.bean.BeanUtils;
import kunlun.file.Csv;
import kunlun.util.Assert;
import kunlun.util.ObjectUtils;
import kunlun.util.RecombineUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Deprecated
public class FileBasedDeviceActionHandler extends AbstractActionHandler {
    private final Map<String, Device> deviceMap;

    public FileBasedDeviceActionHandler(Csv csv) {
        List<Device> deviceList = BeanUtils.mapToBeanInList(csv.toMapList(), Device.class);
        Map<String, Device> modelMap = RecombineUtils.listToMapBean(deviceList, "model");
        deviceMap = Collections.unmodifiableMap(modelMap);
    }

    @Override
    public Object execute(Object input, String strategy, Type type) {
        Assert.isSupport((Class<?>) type, Boolean.FALSE, Device.class);
        DeviceQuery deviceQuery = (DeviceQuery) input;
        String model = deviceQuery.getModel();
        Assert.notBlank(model, "Parameter \"model\" must not blank. ");
        return ObjectUtils.cast(deviceMap.get(model));
    }

}
