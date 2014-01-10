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
@Embeddable
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
}
