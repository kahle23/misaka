/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.ip.support.ipapi;

import baibao.extension.ip.IpLocation;

import java.io.Serializable;

/**
 * Network physical address object.
 * @see <a href="https://en.wikipedia.org/wiki/IP_address">IP address</a>
 * @author Kahle
 */
public class IpApiIpLocation extends IpLocation implements Serializable {
    private String org;
    private String timezone;
    private String zip;
    /**
     * Autonomous system number.
     */
    private String as;

    public IpApiIpLocation(String ipAddress, String address) {
        setIpAddress(ipAddress);
        setAddress(address);
    }

    public IpApiIpLocation(String ipAddress) {

        setIpAddress(ipAddress);
    }

    public IpApiIpLocation() {

    }

    public String getOrg() {

        return org;
    }

    public void setOrg(String org) {

        this.org = org;
    }

    public String getTimezone() {

        return timezone;
    }

    public void setTimezone(String timezone) {

        this.timezone = timezone;
    }

    public String getZip() {

        return zip;
    }

    public void setZip(String zip) {

        this.zip = zip;
    }

    public String getAs() {

        return as;
    }

    public void setAs(String as) {

        this.as = as;
    }

}
