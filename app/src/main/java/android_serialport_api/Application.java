package android_serialport_api;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by Administrator on 2017/5/16.
 */

public class Application {

    private SerialPort reviceSerialPort = null;
    private SerialPort sendSerialPort = null;
    private SerialPort obdSerialPort = null;
    private SerialPort blueSerialPort = null;


    public SerialPort getBlueSerialPort() throws SecurityException, IOException, InvalidParameterException {

        if (blueSerialPort == null) {
            //SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
            String path = "/dev/ttyMT0";//sp.getString("DEVICE", "");
            int baudrate =115200;

            if ( (path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }
            blueSerialPort = new SerialPort(new File(path), baudrate,0);
        }
        return blueSerialPort;
    }

    public SerialPort getRevSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (reviceSerialPort == null) {
			/* Read serial port parameters */
            //SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
            String path = "/dev/ttyMT3";//sp.getString("DEVICE", "");
            int baudrate =115200;

			/* Check parameters */
            if ( (path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            reviceSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return reviceSerialPort;
    }

    public SerialPort getSendSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (sendSerialPort == null) {
			/* Read serial port parameters */
            //SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
            String path = "/dev/ttyMT2";//sp.getString("DEVICE", "");
            int baudrate =38400;//38400;

			/* Check parameters */
            if ( (path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            sendSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return sendSerialPort;
    }

    public SerialPort getOBDSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (obdSerialPort == null) {
			/* Read serial port parameters */
            //SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
            String path = "/dev/ttyMT1";//sp.getString("DEVICE", "");
            int baudrate =38400;

			/* Check parameters */
            if ( (path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            obdSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return obdSerialPort;
    }

    public void closeSerialPort() {
        if (reviceSerialPort != null) {
            reviceSerialPort.close();
            reviceSerialPort = null;
        }

        if (sendSerialPort != null) {
            sendSerialPort.close();
            sendSerialPort = null;
        }

        if (obdSerialPort != null) {
            obdSerialPort.close();
            obdSerialPort = null;
        }

        if (blueSerialPort != null) {
            blueSerialPort.close();
            blueSerialPort = null;
        }
    }


}
