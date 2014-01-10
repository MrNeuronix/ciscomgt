package jobs;

/**
 * Author: Nikolay A. Viguro
 * E-Mail: nv@ph-systems.ru
 * Date: 10.01.14
 * Time: 16:25
 * License: Apache 2.0
 */

import play.jobs.*;
import utils.connection.ssh.SSHSessionExec;

@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() throws Exception {
        System.out.println("App in boot");

            SSHSessionExec session = new SSHSessionExec("192.168.1.42", 22, "test", "test" );
            session.setTimeout( 5000 );
            session.connect();

        System.out.println("CFG: "+session.command( "show run" ));
    }

}