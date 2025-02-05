/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.ip.support.ipapi;

import baibao.extension.ip.AbstractIpLocationAction;
import baibao.extension.ip.IpLocation;
import baibao.extension.ip.IpQuery;
import kunlun.data.Dict;
import kunlun.data.json.JsonUtils;
import kunlun.net.http.HttpMethod;
import kunlun.net.http.HttpUtils;
import kunlun.net.http.support.SimpleRequest;
import kunlun.util.MapUtils;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static kunlun.convert.ConversionUtils.convert;

/**
 * Network physical address provider by website(http://ip-api.com).
 * @see <a href="http://ip-api.com/">IP Geolocation API</a>
 * @author Kahle
 */
public class IpApiIpLocationAction extends AbstractIpLocationAction {
    private static final Logger log = LoggerFactory.getLogger(IpApiIpLocationAction.class);

    @Override
    protected IpLocation doQuery(IpQuery ipQuery) {
        String ipAddress = ipQuery.getIpAddress(), language = ipQuery.getLanguage();
        if (StringUtils.isBlank(language)) { language = "zh-CN"; }
        // Invoke the API interface.
        String jsonString = HttpUtils.execute(SimpleRequest.of(HttpMethod.GET
                , "http://ip-api.com/json/" + ipAddress + "?lang=" + language)).getBodyAsString();
        if (StringUtils.isBlank(jsonString)) { return null; }
        Dict dict = JsonUtils.parseObject(jsonString, Dict.class);
        if (MapUtils.isEmpty(dict)) { return null; }
        // Construct the result object.
        IpApiIpLocation ipApiIpLocation = new IpApiIpLocation();
        ipApiIpLocation.setIpAddress(ipAddress);
        ipApiIpLocation.setCountry(dict.getString("country"));
        ipApiIpLocation.setCountryCode(dict.getString("countryCode"));
        ipApiIpLocation.setRegion(dict.getString("regionName"));
        ipApiIpLocation.setRegionCode(dict.getString("region"));
        ipApiIpLocation.setCity(dict.getString("city"));
        ipApiIpLocation.setCityCode(null);
        ipApiIpLocation.setIsp(dict.getString("isp"));
        ipApiIpLocation.setOrg(dict.getString("org"));
        ipApiIpLocation.setTimezone(dict.getString("timezone"));
        ipApiIpLocation.setZip(dict.getString("zip"));
        ipApiIpLocation.setAs(dict.getString("as"));
        Object lat = dict.get("lat");
        Object lon = dict.get("lon");
        // Parse latitude and longitude.
        try {
            ipApiIpLocation.setLongitude(lon != null ? convert(lon, BigDecimal.class) : null);
            ipApiIpLocation.setLatitude(lat != null ? convert(lat, BigDecimal.class) : null);
        } catch (Exception e) {
            log.info("Parse latitude and longitude to double error", e);
        }
        return ipApiIpLocation;
    }

}
