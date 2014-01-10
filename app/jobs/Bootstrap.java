package jobs;

/**
 * Author: Nikolay A. Viguro
 * E-Mail: nv@ph-systems.ru
 * Date: 10.01.14
 * Time: 16:25
 * License: Apache 2.0
 */

import models.Port;
import models.PortSecurity;
import models.Switch;
import models.VLAN;
import play.Play;
import play.jobs.*;
import utils.connection.ssh.SSHSessionExec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() throws Exception {

        List<Switch> switches = Switch.findAll();

        for(Switch sw : switches)
        {
            List<Port> ports = new ArrayList<>();

            SSHSessionExec session = new SSHSessionExec(
                    sw.getManagementIP(),
                    22,
                    Play.configuration.getProperty("ciscomgt.username"),
                    Play.configuration.getProperty("ciscomgt.password")
            );
            session.setTimeout( 5000 );
            session.connect();

            String runConfig = session.command("sh run");

            // parse VLANs
            for(String portstr : session.command("sh vlan brief").split("\n"))
            {
                Pattern regex = Pattern.compile("(.*?)\\s+(.*?)\\s+(active|act/unsup)\\s+(.*)");
                Matcher regexMatcher = regex.matcher(portstr);
                if (regexMatcher.find()) {
                    if(regexMatcher.group(1).isEmpty()) continue;
                    if(regexMatcher.group(1).equals("VLAN")) continue;

                    VLAN vlan = new VLAN();

                    vlan.setVlanID(Integer.valueOf(regexMatcher.group(1)));
                    vlan.setDescription(regexMatcher.group(2));
                    vlan.setState(regexMatcher.group(3));

                    vlan.save();
                }
            }

            // parse interfaces
            for(String portstr : session.command("sh int status").split("\n"))
            {
                Pattern regex = Pattern.compile("(.*?)\\s+(.*?)\\s+(connected|disabled|notconnect|error-disabled)\\s+(.*?)\\s+(.*?)\\s+(.*?)\\s+(.*)");
                Matcher regexMatcher = regex.matcher(portstr);
                if (regexMatcher.find()) {
                    if(regexMatcher.group(1).isEmpty()) continue;
                    if(regexMatcher.group(1).equals("Port")) continue;

                    Port port = new Port();

                    if(regexMatcher.group(4).equals("trunk")) port.setTrunk(true);

                    // expand interface name to full
                    if(regexMatcher.group(1).contains("Fa")) port.setPortID(regexMatcher.group(1).replaceAll("Fa", "FastEthernet"));
                    if(regexMatcher.group(1).contains("Gi")) port.setPortID(regexMatcher.group(1).replaceAll("Gi", "GigabitEthernet"));
                    if(regexMatcher.group(1).contains("Se")) port.setPortID(regexMatcher.group(1).replaceAll("Se", "Serial"));

                    port.setDescription(regexMatcher.group(2));
                    port.setState(regexMatcher.group(3));

                    if(regexMatcher.group(4).equals("trunk"))
                    {
                        port.setTrunk(true);
                    }
                    else
                    {
                        port.setVlan(VLAN.load(Integer.valueOf(regexMatcher.group(4))));
                        port.setTrunk(false);
                    }

                    port.setDuplex(regexMatcher.group(5));
                    port.setSpeed(regexMatcher.group(6));
                    port.setType(regexMatcher.group(7));

                    // Check port security settings
                    Pattern p = Pattern.compile("interface "+port.getPortID()+"[ \t]*\r?\n(.*?)!", Pattern.DOTALL);
                    Matcher m = p.matcher(runConfig);

                    while (m.find())
                    {
                        PortSecurity portSecurity = new PortSecurity();

                        String portconfig = m.group(1);

                        // Port security policy
                        Pattern psecurity = Pattern.compile("switchport port-security[ \t]*\r?\n", Pattern.DOTALL);
                        Matcher msecurity = psecurity.matcher(portconfig);
                        while (msecurity.find()) { portSecurity.setPortSecurityEnabled(true); }

                        // Port security policy
                        Pattern pviolation = Pattern.compile("switchport port-security violation (.*?)[ \t]*\r?\n", Pattern.DOTALL);
                        Matcher mviolation = pviolation.matcher(portconfig);
                        while (mviolation.find()) { portSecurity.setPortSecurityPolicy(mviolation.group(1)); }

                        // Port security maximum
                        Pattern psecmax = Pattern.compile("switchport port-security maximum (.*?)[ \t]*\r?\n", Pattern.DOTALL);
                        Matcher msecmax = psecmax.matcher(portconfig);
                        while (msecmax.find()) { portSecurity.setPortSecurityMaxMAC(Integer.valueOf(msecmax.group(1))); }

                        // Port security macs
                        List<String> macs = new ArrayList<String>();
                        Pattern pmac = Pattern.compile("switchport port-security mac-address (.*?)[ \t]*\r?\n", Pattern.DOTALL);
                        Matcher mmac = pmac.matcher(portconfig);
                        while (mmac.find())
                        {
                                macs.add(mmac.group(1));
                        }
                        portSecurity.setPortSecurityMACAdresses(macs);

                        //save into db and add portsecurity to port
                        portSecurity.save();
                        port.setPortSecurity(portSecurity);

                        // Finally, save into db and adding port to array
                        port.save();
                        ports.add(port);
                }
            }
        }

            // save into database
            sw.setPorts(ports);
            sw.save();
        }
    }



}