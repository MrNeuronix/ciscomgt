package controllers;

/**
 * Author: Nikolay A. Viguro
 * E-Mail: nv@ph-systems.ru
 * Date: 10.01.14
 * Time: 15:50
 * License: Apache 2.0
 */

import play.*;
import play.mvc.*;
import models.*;

import java.util.List;

@With(Secure.class)
public class Switches extends Controller {

    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }

    public static void index(long switch_id) {
        Switch sw = Switch.findById(switch_id);
        render(sw);
    }

}