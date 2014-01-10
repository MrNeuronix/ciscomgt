package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Author: Nikolay A. Viguro
 * E-Mail: nv@ph-systems.ru
 * Date: 10.01.14
 * Time: 14:53
 * License: Apache 2.0
 */

@Entity
public class VLAN extends Model {

    /** VLAN id, e.g. 5 */
    private int vlanID;

    /** VLAN description */
    private String description;

    /** VLAN status, e.g. active */
    private String state;

}
