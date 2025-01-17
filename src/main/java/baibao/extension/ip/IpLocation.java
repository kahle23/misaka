/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.ip;

import kunlun.data.geo.Geolocation;
import kunlun.util.StringUtils;

import java.io.Serializable;

import static kunlun.common.constant.Symbols.BLANK_SPACE;
import static kunlun.common.constant.Symbols.EMPTY_STRING;

/**
 * The network physical address object.
 * @see <a href="https://en.wikipedia.org/wiki/IP_address">IP address</a>
 * @author Kahle
 */
public class IpLocation extends Geolocation implements Serializable {
    private String ipAddress;
    private String isp;

    public IpLocation(String ipAddress, String address) {
        setIpAddress(ipAddress);
        setAddress(address);
    }

    public IpLocation(String ipAddress) {

        setIpAddress(ipAddress);
    }

    public IpLocation() {

    }

    public String getIpAddress() {

        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {

        this.ipAddress = ipAddress;
    }

    public String getIsp() {

        return isp;
    }

    public void setIsp(String isp) {

        this.isp = isp;
    }

    @Override
    public String toString() {
        String country = getCountry();
        String region = getRegion();
        String city = getCity();
        String district = getDistrict();
        String street = getStreet();
        String address = getAddress();
        return (StringUtils.isNotBlank(country) ? country + BLANK_SPACE : EMPTY_STRING)
                + (StringUtils.isNotBlank(region) ? region + BLANK_SPACE : EMPTY_STRING)
                + (StringUtils.isNotBlank(city) ? city + BLANK_SPACE : EMPTY_STRING)
                + (StringUtils.isNotBlank(district) ? district + BLANK_SPACE : EMPTY_STRING)
                + (StringUtils.isNotBlank(street) ? street + BLANK_SPACE : EMPTY_STRING)
                + (StringUtils.isNotBlank(address) ? address + BLANK_SPACE : EMPTY_STRING)
                + (StringUtils.isNotBlank(isp) ? isp : EMPTY_STRING);
    }

}
