package jobs;

import models.Port;
import models.Switch;
import models.VLAN;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.connection.ssh.SSHSessionExec;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Nikolay A. Viguro
 * E-Mail: nv@ph-systems.ru
 * Date: 13.01.14
 * Time: 14:27
 * License: Apache 2.0
 */

public class SendCommands extends Job {

    private String commands;
    private String mgmtIP;

    public SendCommands(String mgmtIP, String commands)
    {
        this.commands = commands;
        this.mgmtIP = mgmtIP;
    }

    public void doJob() throws Exception {

            SSHSessionExec session = new SSHSessionExec(
                    mgmtIP,
                    22,
                    Play.configuration.getProperty("ciscomgt.username"),
                    Play.configuration.getProperty("ciscomgt.password")
            );
            session.setTimeout( 5000 );
            session.connect();

            session.command("conf t");

            // send commands
            for(String command : session.command(commands).split("\n"))
            {
                session.command(command);
            }

            session.disconnect();
    }
}
