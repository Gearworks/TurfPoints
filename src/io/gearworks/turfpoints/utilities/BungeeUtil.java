package io.gearworks.turfpoints.utilities;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeeUtil {

    public static void sendMessageToServers (String message){
        ByteArrayOutputStream b = new ByteArrayOutputStream ();
        DataOutputStream out = new DataOutputStream (b);

        try{
            out.writeUTF ("Forward");
            out.writeUTF ("ALL");
            out.writeUTF ("MyChannel");

            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);

            msgout.writeUTF (message);
            msgout.writeShort (123);

            out.writeShort (msgbytes.toByteArray ().length);
            out.write (msgbytes.toByteArray ());
        }catch (IOException e){

        }
    }
}
