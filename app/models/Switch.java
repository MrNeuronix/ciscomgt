package models;

import jobs.SendCommands;
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

    /** This function collect changes for switch and
     * run SendCommands job */
     public void saveChanges()
    {
        // This string contains command, what will be sended to switch
        String toSwitchCommands = "";

         // load saved switch
        Switch savedSW = Switch.findById(id);

        // get ports and compare
        for(Port port : getPorts())
        {
            // get same port, already saved in database
            Port savedPort = Port.findById(port.getId());

            // store temp commands for this port
            String tempCommands = "interface " + port.getPortID() + "\n";

            // if port description changed
            if(!port.getDescription().equals(savedPort.getDescription()))
            {
                tempCommands += "description " + port.getDescription() + "\n";
            }

            // if duplex changed
            if(!port.getDuplex().equals(savedPort.getDuplex()))
            {
                tempCommands += "duplex " + port.getDuplex() + "\n";
            }

            // if speed changed
            if(!port.getSpeed().equals(savedPort.getSpeed()))
            {
                tempCommands += "speed " + port.getSpeed() + "\n";
            }

            // if vlan changed on access port
            if(!(port.getVlan().getVlanID() == savedPort.getVlan().getVlanID()) && !port.isTrunk())
            {
                tempCommands += "switchport mode access\n";
                tempCommands += "switchport access vlan " + port.getSpeed() + "\n";
            }

            // if port set to be trunk
            if(port.isTrunk())
            {
                tempCommands += "switchport mode trunk\n";
            }

            // if port set to shutdown
            if(!port.getState().equals(savedPort.getState()) && port.getState().equals("disabled"))
            {
                tempCommands += "shutdown\n";
            }

            // if port set to enabled (this is not real state, just command to software)
            if(!port.getState().equals(savedPort.getState()) && port.getState().equals("enabled"))
            {
                tempCommands += "no shutdown\n";
            }

            // Port security settings
            // if port security changed to disable
            if(port.getPortSecurity().isPortSecurityEnabled() != savedPort.getPortSecurity().isPortSecurityEnabled()
                    && port.getPortSecurity().isPortSecurityEnabled())
            {
                tempCommands += "no switchport port-security\n";
            }

            // if port security changed to enable
            if(port.getPortSecurity().isPortSecurityEnabled() != savedPort.getPortSecurity().isPortSecurityEnabled()
                    && !port.getPortSecurity().isPortSecurityEnabled())
            {
                tempCommands += "switchport port-security\n";
            }

            // if policy changed
            if(port.getPortSecurity().getPortSecurityPolicy().equals(savedPort.getPortSecurity().getPortSecurityPolicy()))
            {
                tempCommands += "switchport port-security policy " + port.getPortSecurity().getPortSecurityPolicy() + "\n";
            }

            // if mac-addressed changed
            List<String> macs = port.getPortSecurity().getPortSecurityMACAdresses();
            List<String> savedMacs = savedPort.getPortSecurity().getPortSecurityMACAdresses();

            // macs changed
            if(!(macs.size() != savedMacs.size() || macs.containsAll(savedMacs)))
            {
                // temporary disables portsecurity
                tempCommands += "no switchport port-security\n";

                // set new max macs
                tempCommands += "switchport port-security maximum " + macs.size() + "\n";

                // delete old macs
                for (String savedMac : savedMacs)
                {
                    tempCommands += "no switchport port-security mac-address " + savedMac + "\n";
                }

                // add new macs
                for (String mac : macs)
                {
                    tempCommands += "switchport port-security mac-address " + mac + "\n";
                }

                // turn on port-security
                tempCommands += "switchport port-security\n";
            }

            // save port to database
            port.save();
        }

        // send commands to switch async
        new SendCommands(this.getManagementIP(), toSwitchCommands).run();

        // save switch state
        this.save();
    }
}
