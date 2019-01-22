package com.chulm.websocket.test;

import javax.security.cert.CertificateException;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.UUID;


public class SocketConnectTest {


    private final static String charName = "UTF-8";


    /**
     * @param args
     * @throws IOException
     * @throws CertificateEncodingException
     * @throws CertificateException
     * @throws URISyntaxException
     * @throws InterruptedException
     */

    public static void main(String[] args) throws IOException, CertificateEncodingException, CertificateException, URISyntaxException, InterruptedException {


        /************************************************************************/

        // request

        Socket sock = new Socket();

        sock.connect(new InetSocketAddress("localhost", 8082));


        OutputStream os = sock.getOutputStream();


        os.write(("GET /ws-handler/info?t=" + System.currentTimeMillis() + " HTTP/1.1" + "\n").getBytes(charName));

        os.write(("Connection: KeepAlive\n").getBytes(charName));

        os.write("\n\n".getBytes(charName));

        os.flush();


        String line = "";

        String result = "";


        // response

        BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream(), charName));

        while ((line = br.readLine()) != null)

            result += line + "\n";


        System.out.println("result = " + result);


        byte[] encode = Base64.getEncoder().encode(toByteArray(UUID.randomUUID()));

        String key = new String(encode);


        System.out.println("Request Sec-WebSocket-Key = " + key);


        // disconnect

        os.close();

        br.close();

        sock.close();


        /************************************************************************/

        //handshake request


        Socket sock2 = new Socket();

        sock2.connect(new InetSocketAddress("localhost", 8082));


        os = sock2.getOutputStream();

        ///ws/733/a1f0yayx/websocket

        os.write(("GET /ws-handler/111/" + System.currentTimeMillis()+"/websocket"  + " HTTP/1.1" + "\n").getBytes("UTF-8"));

        os.write(("Connection: Upgrade\n").getBytes("UTF-8"));

        os.write(("Sec-WebSocket-Version: 13\n").getBytes("UTF-8"));

        os.write(("Sec-WebSocket-Key:" + key + "\n").getBytes("UTF-8"));

        os.write(("Upgrade: websocket" + "\n").getBytes("UTF-8"));

        os.write(("\n\n").getBytes("UTF-8"));

        os.flush();

        result = "";


        //handshake response

        BufferedInputStream bis = new BufferedInputStream(sock2.getInputStream());


        while (true) {

            bis.mark(9999999);


            int offset = bis.read();

            byte[] readByte = new byte[offset];


            bis.reset();


            bis.read(readByte);


            String data = new String(readByte, "UTF-8");

            System.out.println("data: " + data);

        }


    }


    private static byte[] toByteArray(UUID uuid) {

        byte[] byteArray = new byte[(Long.SIZE / Byte.SIZE) * 2];

        ByteBuffer buffer = ByteBuffer.wrap(byteArray);

        LongBuffer longBuffer = buffer.asLongBuffer();

        longBuffer.put(new long[]{uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()});

        return byteArray;

    }


}