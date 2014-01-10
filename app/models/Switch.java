package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Author: Nikolay A. Viguro
 * E-Mail: nv@ph-systems.ru
 * Date: 10.01.14
 * Time: 14:52
 * License: Apache 2.0
 */

@Entity
public class Switch extends Model {

    /** Management IP */
    public String managementIP;

    /** Hostname description, e.g. lan-bc-sw2 */
    public String hostname;

    /** Switch state, e.g. active */
    public String status;

    /** Swith model, e.g. C2960-48TS-L */
    public String model;

    /** IOS version */
    public String osVersion;

    /** Switch ports */
    @OneToMany
    public List<Port> ports;

    public String getManagementIP() {
        return managementIP;
    }

    public void setManagementIP(String managementIP) {
        this.managementIP = managementIP;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }

    /** This function save changes on switch */
    public void saveChanges()
    {

    }
}
