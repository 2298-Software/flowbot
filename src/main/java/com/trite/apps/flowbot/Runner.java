package com.trite.apps.flowbot;

import com.trite.apps.flowbot.devices.BaseDevice;
import com.trite.apps.flowbot.devices.SmartMeterGateway;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class Runner {
  static Logger log = Logger.getLogger(Runner.class.getName());

  public static void main(String args[]) throws Exception {

    if(args.length!=1){
      log.fatal("Required args: config_file.properties");
      return;
    }

    Properties prop = new Properties();
    String propFileName = args[0];
    InputStream inputStream = new FileInputStream(propFileName);
    prop.load(inputStream);



    File lock_file = new File(prop.getProperty("first_book_lock_file"));
    if(lock_file.exists()){
      log.info("this device has previously booted, no action needed from provisioner.");
    } else {
      log.info("this device has not previously booted");

      BaseDevice device;
      String device_type = prop.getProperty("device_type");
      switch (device_type){
        case "duke-energy-smart-meter-gateway": device = new SmartMeterGateway();
          break;
        default: throw new Exception("Device not supported " + device_type);
      }


      device.setMac("b0:c0:90:be:ac:2d");
      log.info("Device is " + device.toJsonString());

    }
  }
}
