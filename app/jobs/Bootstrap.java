package jobs;

/**
 * Author: Nikolay A. Viguro
 * E-Mail: nv@ph-systems.ru
 * Date: 10.01.14
 * Time: 16:25
 * License: Apache 2.0
 */

import models.Switch;
import play.Play;
import play.jobs.*;
import utils.connection.ssh.SSHSessionExec;

import java.util.List;

@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() throws Exception {

        List<Switch> switches = Switch.findAll();

        for(Switch sw : switches)
        {
            SSHSessionExec session = new SSHSessionExec(
                    sw.getManagementIP(),
                    22,
                    Play.configuration.getProperty("ciscomgt.username"),
                    Play.configuration.getProperty("ciscomgt.password")
            );
            session.setTimeout( 5000 );
            session.connect();

            System.out.println("APP: "+session.command("sh run int fa0/1"));
        }
    }

}