package com.bitallowance;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CreateReserve extends AsyncTask<String, Integer, Void> {

    // the socket is the connection to the server
    private Socket _socket;
    // the reader and writer are both connected to the socket and used to read from/write to the
    // server
    //private DataOutputStream _out;
    //private DataInputStream _in;
    private PrivateKey _priv;
    private PublicKey _pub;
    // the port is the door use to connect to the sever
    private static final int SERVER_PORT = 3490;
    // the address of the server
    private static final String SERVER_IP = "107.174.13.151";

    private void generateKeyPair() {
        // generate the public and private key pair
        try {
            // prepare the key generator
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");

            // generate some secure randomness
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            keyGen.initialize(2048, random);

            // generate the key pair
            KeyPair pair = keyGen.generateKeyPair();
            _priv = pair.getPrivate();
            _pub = pair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... strings) {
        generateKeyPair();

        String username = strings[0];
        String displayName = strings[1];
        String email = strings[2];
        String password = strings[3]; // DO NOT SEND TO SERVER DO NOT WRITE TO DISK

        // The server connection section
        try {
            // the address has to be in the correct format for the socket to use it
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            SocketAddress sockaddr = new InetSocketAddress(serverAddr, SERVER_PORT);
            _socket = new Socket();

            // if there's a problem with the server this makes sure the thread doesn't hang
            int timeout = 2000;
            // connects the socket to the remote server
            _socket.connect(sockaddr, timeout);
            // instantiates the reader/writer
            PrintWriter _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream())), true);
            InputStreamReader _in = new InputStreamReader(_socket.getInputStream());

            // write to the buffered writer, then sends the packet with flush
            // we're telling the server we want to 'c'reate an account
            _out.print('c');
            _out.flush();

            // read the public key into a byte array
            Log.d("Create Reserve", "accepting new");
            ByteArrayOutputStream serverKeyByteStream = new ByteArrayOutputStream();
            char[] serverKeyBytes = new char[426];
            int read = _in.read(serverKeyBytes, 0, 426);
            String serverPEMKey = new String(serverKeyBytes);
            Log.d("Read", Integer.toString(read));
            Log.d("Create Reserve", serverPEMKey);

            // encode the data into a usable format
            //X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(serverPEMKey, Base64.DEFAULT));
            // rev up the RSA algorithm for generating the public key
            //KeyFactory fact = KeyFactory.getInstance("RSA");
            // generate the public key from the encoded data
            //RSAPublicKey serverPublic = (RSAPublicKey) fact.generatePublic(spec);
            //Log.d("Create Reserve", serverPublic.toString());

            // rev up the cipher
            //Cipher encryptCipher = Cipher.getInstance("RSA");
            //encryptCipher.init(Cipher.ENCRYPT_MODE, serverPublic);

            // convert to bytes, encrypt, and send the packets all in one fell swoop
            //_out.write(encryptCipher.doFinal(_pub.getEncoded()));
            //_out.write(encryptCipher.doFinal(userame.getBytes()));
            //_out.write(encryptCipher.doFinal(displayName.getBytes()));
            //_out.write(encryptCipher.doFinal(email.getBytes()));
            String nul = "\0";
            _out.println(_pub.toString());
            Log.d("Create Reserve", _pub.toString());
            _out.println(username);
            Log.d("Create Reserve", username);
            _out.println(displayName);
            Log.d("Create Reserve", displayName);
            _out.println(email);
            Log.d("Create Reserve", email);
            _out.flush();
            Log.d("Create Reserve", "Flushed");

            // rev up a different cipher
            //Cipher decryptCipher = Cipher.getInstance("RSA");
            //decryptCipher.init(Cipher.DECRYPT_MODE, serverPublic);

            char[] idBytes = new char[100];
            Log.d("Create Reserve", "receiving id");
            _in.read(idBytes, 0, 100);
            String id = new String(idBytes);
            Log.d("Create Reserve", id);
            Log.d("Create Reserve", String.valueOf(id.length()));

            Log.d("Create Reserve", "start closing things");
            _out.close();
            _in.close();
            _socket.close();
            Log.d("Create Reserve", "closed everything");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }/* catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... data) {

    }

    @Override
    protected void onPostExecute(Void result) {

    }
}
