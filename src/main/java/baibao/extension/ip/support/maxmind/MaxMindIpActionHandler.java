/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.ip.support.maxmind;

import baibao.extension.ip.IpQuery;
import baibao.extension.ip.support.ipapi.IpApiIpLocation;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import kunlun.action.AbstractActionHandler;
import kunlun.exception.ExceptionUtils;
import kunlun.util.Assert;
import kunlun.util.ObjectUtils;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.InetAddress;

/**
 * Network physical address provider by MaxMind GeoIP.
 * @see <a href="https://dev.maxmind.com/geoip/">MaxMind GeoIP</a>
 * @author Kahle
 */
public class MaxMindIpActionHandler extends AbstractActionHandler {
    private static final Logger log = LoggerFactory.getLogger(MaxMindIpActionHandler.class);
    private final File providerDbPath;

    public MaxMindIpActionHandler(String providerDbPathStr) {

        this(new File(providerDbPathStr));
    }

    public MaxMindIpActionHandler(File providerDbPath) {
        // http://dev.maxmind.com/geoip/geoip2/geolite2/
        this.providerDbPath = providerDbPath;
        log.info("MaxMind Ip Geolocation Provider database path: {}", providerDbPath);
    }

    @Override
    public Object execute(Object input, String strategy, Type type) {
        try {
            Assert.isSupport((Class<?>) type, Boolean.FALSE, IpApiIpLocation.class);
            IpQuery ipQuery = (IpQuery) input;
            String ipAddress = ipQuery.getIpAddress();
            String language = ipQuery.getLanguage();
//            String dbPath = "e:\\GeoLite2-City.mmdb";
            // 语言：de、pt-BR、fr、en、ru、zh-CN、es、ja
            if (StringUtils.isBlank(language)) { language = "en"; }
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            DatabaseReader dbReader = new DatabaseReader.Builder(providerDbPath).build();
            CityResponse response = dbReader.city(inetAddress);

            Country country = response.getCountry();
            Subdivision subdivision = response.getMostSpecificSubdivision();
            City city = response.getCity();
            Postal postal = response.getPostal();
            Location location = response.getLocation();

            IpApiIpLocation ipGeolocation = new IpApiIpLocation();
            ipGeolocation.setCountry(country.getNames().get(language));
            ipGeolocation.setCountryCode(country.getIsoCode());
            ipGeolocation.setRegion(subdivision.getNames().get(language));
            ipGeolocation.setRegionCode(subdivision.getIsoCode());
            ipGeolocation.setCity(city.getNames().get(language));
//            ipGeolocation.setCityCode();
//            ipGeolocation.setDistrict();
//            ipGeolocation.setDistrictCode();
//            ipGeolocation.setStreet();
//            ipGeolocation.setStreetCode();
            ipGeolocation.setLatitude(BigDecimal.valueOf(location.getLatitude()));
            ipGeolocation.setLongitude(BigDecimal.valueOf(location.getLongitude()));
//            ipGeolocation.setElevation();
//            ipGeolocation.set;
            return ObjectUtils.cast(ipGeolocation);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
