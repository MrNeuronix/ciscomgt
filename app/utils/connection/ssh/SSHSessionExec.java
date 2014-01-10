package utils.connection.ssh;

/**
 * Author: Nikolay A. Viguro
 * E-Mail: nv@ph-systems.ru
 * Date: 10.01.14
 * Time: 17:02
 * License: Apache 2.0
 */

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.InputStream;
import java.util.Properties;

public class SSHSessionExec
{
    private String host;
    private int port;
    private String login;
    private String pswd;
    private Session session;
    private int timeout = 0;

    public SSHSessionExec(String host, int port, String login, String pswd)
    {
        this.host = host;
        this.port = port;
        this.login = login;
        this.pswd = pswd;
    }

    public void connect()
            throws Exception
    {
        Properties pr = new Properties();
        pr.put("StrictHostKeyChecking", "no");

        this.session = new JSch().getSession(this.login, this.host, this.port);
        this.session.setPassword(this.pswd);
        this.session.setConfig(pr);

        this.session.setTimeout(this.timeout);

        this.session.connect(3000);
    }

    public String command(String command)
            throws Exception
    {

        System.out.println("["+host+"] Sending: "+command);

        StringBuffer result = new StringBuffer();
        ChannelExec channel = null;
        try
        {
            connect();
            channel = (ChannelExec)this.session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            InputStream in = channel.getInputStream();
            channel.connect();

            long start = System.currentTimeMillis();
            byte[] tmp = new byte[1024];

            while ((this.timeout == 0) || (System.currentTimeMillis() - start < this.timeout))
            {
                while(in.available()>0)
                {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    result.append(new String(tmp, 0, i));
                }

                if(channel.isClosed()){
                    break;
                }
                Thread.sleep(100L);
            }
        }
        finally
        {
            if (channel != null) {
                channel.disconnect();
            }
            this.session.disconnect();
        }
        return result.toString();
    }

    public void disconnect()
    {
        this.session.disconnect();
    }

    public int getTimeout()
    {
        return this.timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }
}

