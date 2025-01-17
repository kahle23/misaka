/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.ip;

import java.io.Serializable;

public class IpQuery implements Serializable {
    private String ipAddress;
    private String language;

    public IpQuery(String ipAddress, String language) {
        this.ipAddress = ipAddress;
        this.language = language;
    }

    public IpQuery(String ipAddress) {

        this.ipAddress = ipAddress;
    }

    public IpQuery() {

    }

    public String getIpAddress() {

        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {

        this.ipAddress = ipAddress;
    }

    public String getLanguage() {

        return language;
    }

    public void setLanguage(String language) {

        this.language = language;
    }

}
