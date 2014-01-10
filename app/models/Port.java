package models;

import org.hibernate.annotations.ManyToAny;
import play.db.jpa.Model;

import javax.persistence.*;

/**
 * Author: Nikolay A. Viguro
 * E-Mail: nv@ph-systems.ru
 * Date: 10.01.14
 * Time: 14:53
 * License: Apache 2.0
 */

@Entity
public class Port extends Model {

    /** Switch port id, e.g. Gi0/1 */
    public String portID;

    /** Switch port description */
    public String description;

    /** Switch port type, e.g. 10/100BaseTX */
    public String type;

    /** Switch port VLAN */
    @ManyToOne
    public VLAN vlan;

    /** Is switch port int trunk mode? */
    public boolean isTrunk = false;

    /** Switch port speed */
    public String speed;

    /** Switch port duplex mode */
    public String duplex;

    /** Port state */
    public String state;

    /** Port security*/
    @OneToOne
    public PortSecurity portSecurity;

    public String getPortID() {
        return portID;
    }

    public void setPortID(String portID) {
        this.portID = portID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public VLAN getVlan() {
        return vlan;
    }

    public void setVlan(VLAN vlan) {
        this.vlan = vlan;
    }

    public boolean isTrunk() {
        return isTrunk;
    }

    public void setTrunk(boolean isTrunk) {
        this.isTrunk = isTrunk;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDuplex() {
        return duplex;
    }

    public void setDuplex(String duplex) {
        this.duplex = duplex;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public PortSecurity getPortSecurity() {
        return portSecurity;
    }

    public void setPortSecurity(PortSecurity portSecurity) {
        this.portSecurity = portSecurity;
    }
}
