package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Author: Nikolay A. Viguro
 * E-Mail: nv@ph-systems.ru
 * Date: 10.01.14
 * Time: 14:53
 * License: Apache 2.0
 */

@Entity
public class PortSecurity extends Model {

    /** Port security state*/
    private boolean portSecurityEnabled = false;

    /** Port security policy, e.g. violation restrict | shutdown | protect */
    private String portSecurityPolicy;

    /** Port security maximum MAC addresses. Default value is 1 */
    private int portSecurityMaxMAC = 1;

    /** Port security MAC addresses list */
    @ElementCollection
    @CollectionTable(name="portsecurity_macs", joinColumns=@JoinColumn(name="portsec_id"))
    @Column(name="mac")
    private List<String> portSecurityMACAdresses;
}
