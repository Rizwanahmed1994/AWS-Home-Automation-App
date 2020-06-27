package com.example.smartandsafeautomatedhome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.UUID;

import static com.example.smartandsafeautomatedhome.MainActivity.LOG_TAG;

public class SchedulingActivity extends AppCompatActivity {



    TextView tvStatus1;

    AWSIotClient mIotAndroidClient;
    AWSIotMqttManager mqttManager;
    String clientId;
    String keystorePath;
    String keystoreName;
    String keystorePassword;

    KeyStore clientKeyStore = null;
    String certificateId;

    EditText hrsLight1,minsLight1,yearLight1,monthLight1,dayLight1,hrsTV1,minsTV1,yearTV1,monthTV1,dayTV1,hrsAC1,minsAC1,yearAC1,monthAC1,dayAC1;
    String hrs,mins,year,month,day;

    EditText hrsLight1off,minsLight1off,yearLight1off,monthLight1off,dayLight1off,hrsTV1off,minsTV1off,yearTV1off,monthTV1off,dayTV1off,hrsAC1off,minsAC1off,yearAC1off,monthAC1off,dayAC1off;



    MainActivity cls =new MainActivity();

    ToggleButton ConnectButton;

    Button btnLight1On,btnLight1Off,btnAC1On,btnAC1Off,btnTV1On,btnTV1Off;



    public void connectClick(final View view) {
        Log.d(LOG_TAG, "clientId = " + clientId);

        try {
            mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvStatus1.setText(status.toString());
                            if (throwable != null) {
                                Log.e(LOG_TAG, "Connection error.", throwable);
                            }
                        }
                    });
                }
            });
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Connection error.", e);
            tvStatus1.setText("Error! " + e.getMessage());
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduling_activity);

        tvStatus1 = findViewById(R.id.tvStatus3);


        // MQTT client IDs are required to be unique per AWS IoT account.
        // This UUID is "practically unique" but does not _guarantee_
        // uniqueness.
        clientId = UUID.randomUUID().toString();

        ConnectButton=findViewById(R.id.btnConnectS);

        hrsLight1=findViewById(R.id.editTextTimeHrsLightR1on);
        minsLight1=findViewById(R.id.editTextTimeMinLightR1on);
        yearLight1=findViewById(R.id.editTextDateyearLightR1on);
        monthLight1=findViewById(R.id.editTextDateMonthLightR1on);
        dayLight1=findViewById(R.id.editTextDateDayLightR1on);

        hrsLight1off=findViewById(R.id.editTextTimeHrsLightR1off);
        minsLight1off=findViewById(R.id.editTextTimeMinLightR1off);
        yearLight1off=findViewById(R.id.editTextDateyearLightR1off);
        monthLight1off=findViewById(R.id.editTextDateMonthLightR1off);
        dayLight1off=findViewById(R.id.editTextDateDayLightR1off);

        hrsTV1=findViewById(R.id.editTextTimeHrsTVR1on);
        minsTV1=findViewById(R.id.editTextTimeMinTVR1on);
        yearTV1=findViewById(R.id.editTextDateyearTVR1on);
        monthTV1=findViewById(R.id.editTextDateMonthTVR1on);
        dayTV1=findViewById(R.id.editTextDateDayTVR1on);

        hrsTV1off=findViewById(R.id.editTextTimeHrsTVR1off);
        minsTV1off=findViewById(R.id.editTextTimeMinTVR1off);
        yearTV1off=findViewById(R.id.editTextDateyearTVR1off);
        monthTV1off=findViewById(R.id.editTextDateMonthTVR1off);
        dayTV1off=findViewById(R.id.editTextDateDayTVR1off);

        hrsAC1off=findViewById(R.id.editTextTimeHrsACR1off);
        minsAC1off=findViewById(R.id.editTextTimeMinACR1off);
        yearAC1off=findViewById(R.id.editTextDateyearACR1off);
        monthAC1off=findViewById(R.id.editTextDateMonthACR1off);
        dayAC1off=findViewById(R.id.editTextDateDayACR1off);

        hrsAC1=findViewById(R.id.editTextTimeHrsACR1on);
        minsAC1=findViewById(R.id.editTextTimeMinACR1on);
        yearAC1=findViewById(R.id.editTextDateyearACR1on);
        monthAC1=findViewById(R.id.editTextDateMonthACR1on);
        dayAC1=findViewById(R.id.editTextDateDayACR1on);


        btnLight1On=findViewById(R.id.btnLight1Sh);
        btnLight1Off=findViewById(R.id.btnLight1Shoff);
        btnAC1On=findViewById(R.id.btnAC1Sh);
        btnAC1Off=findViewById(R.id.btnAC1Shoff);
        btnTV1On=findViewById(R.id.btnTV1Sh);
        btnTV1Off=findViewById(R.id.btnTV1Shoff);



        // Initialize the AWS Cognito credentials provider
        AWSMobileClient.getInstance().initialize(this, new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                initIoTClient();
            }

            @Override
            public void onError(Exception e) {
                Log.e(LOG_TAG, "onError: ", e);
            }
        });

        btnLight1On.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               hrs=hrsLight1.getText().toString();
               mins=minsLight1.getText().toString();
               year=yearLight1.getText().toString();
               month=monthLight1.getText().toString();
               day=dayLight1.getText().toString();

               if(DateTimeValidation(hrs,mins,year,month,day))
               {
                   final String combine=  year+"-"+month+"-"+day+" "+hrs+":"+mins+"light1";
                   Toast.makeText(SchedulingActivity.this, "Light 1 of Room1 is Scheduled ON Sucessfully", Toast.LENGTH_LONG).show();

                   final String topic = "$aws/things/Lamp/shadow/update";
                   final String msg = "{\n" +
                           "    \"state\" : {\n" +
                           "        \"desired\" : {\n" +
                           "\t    \"thing\": \""+combine+"\"\n" +
                           "         }\n" +
                           "     }\n" +
                           "}";

                   try {
                       mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
                   } catch (Exception e) {
                       Log.e(LOG_TAG, "Publish error.", e);
                   }
               }





            }
        });
        btnAC1On.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hrs=hrsAC1.getText().toString();
                mins=minsAC1.getText().toString();
                year=yearAC1.getText().toString();
                month=monthAC1.getText().toString();
                day=dayAC1.getText().toString();

                if(DateTimeValidation(hrs,mins,year,month,day))
                {
                    final String combine=  year+"-"+month+"-"+day+" "+hrs+":"+mins+"ac1";
                    Toast.makeText(SchedulingActivity.this, "AC of Room1 is Scheduled ON Sucessfully", Toast.LENGTH_LONG).show();

                    final String topic = "$aws/things/Lamp/shadow/update";
                    final String msg = "{\n" +
                            "    \"state\" : {\n" +
                            "        \"desired\" : {\n" +
                            "\t    \"thing\": \""+combine+"\"\n" +
                            "         }\n" +
                            "     }\n" +
                            "}";

                    try {
                        mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Publish error.", e);
                    }
                }





            }
        });
        btnTV1On.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hrs=hrsTV1.getText().toString();
                mins=minsTV1.getText().toString();
                year=yearTV1.getText().toString();
                month=monthTV1.getText().toString();
                day=dayTV1.getText().toString();



                if(DateTimeValidation(hrs,mins,year,month,day))
                {
                    final String combine=  year+"-"+month+"-"+day+" "+hrs+":"+mins+"tv1";
                    Toast.makeText(SchedulingActivity.this, "TV of Room1 is Scheduled ON Sucessfully", Toast.LENGTH_LONG).show();

                    final String topic = "$aws/things/Lamp/shadow/update";
                    final String msg = "{\n" +
                            "    \"state\" : {\n" +
                            "        \"desired\" : {\n" +
                            "\t    \"thing\": \""+combine+"\"\n" +
                            "         }\n" +
                            "     }\n" +
                            "}";

                    try {
                        mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Publish error.", e);
                    }
                }





            }
        });
        btnLight1Off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hrs=hrsLight1off.getText().toString();
                mins=minsLight1off.getText().toString();
                year=yearLight1off.getText().toString();
                month=monthLight1off.getText().toString();
                day=dayLight1off.getText().toString();

                if(DateTimeValidation(hrs,mins,year,month,day))
                {
                    final String combine=  year+"-"+month+"-"+day+" "+hrs+":"+mins+"light0";
                    Toast.makeText(SchedulingActivity.this, "Light 1 of Room1 is OFF Scheduled Sucessfully", Toast.LENGTH_LONG).show();

                    final String topic = "$aws/things/Lamp/shadow/update";
                    final String msg = "{\n" +
                            "    \"state\" : {\n" +
                            "        \"desired\" : {\n" +
                            "\t    \"thing\": \""+combine+"\"\n" +
                            "         }\n" +
                            "     }\n" +
                            "}";

                    try {
                        mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Publish error.", e);
                    }
                }





            }
        });
        btnAC1Off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hrs=hrsAC1off.getText().toString();
                mins=minsAC1off.getText().toString();
                year=yearAC1off.getText().toString();
                month=monthAC1off.getText().toString();
                day=dayAC1off.getText().toString();

                if(DateTimeValidation(hrs,mins,year,month,day))
                {
                    final String combine=  year+"-"+month+"-"+day+" "+hrs+":"+mins+"ac0";
                    Toast.makeText(SchedulingActivity.this, "AC of Room1 is Scheduled OFF Sucessfully", Toast.LENGTH_LONG).show();

                    final String topic = "$aws/things/Lamp/shadow/update";
                    final String msg = "{\n" +
                            "    \"state\" : {\n" +
                            "        \"desired\" : {\n" +
                            "\t    \"thing\": \""+combine+"\"\n" +
                            "         }\n" +
                            "     }\n" +
                            "}";

                    try {
                        mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Publish error.", e);
                    }
                }



            }
        });
        btnTV1Off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hrs=hrsTV1off.getText().toString();
                mins=minsTV1off.getText().toString();
                year=yearTV1off.getText().toString();
                month=monthTV1off.getText().toString();
                day=dayTV1off.getText().toString();



                if(DateTimeValidation(hrs,mins,year,month,day))
                {
                    final String combine=  year+"-"+month+"-"+day+" "+hrs+":"+mins+"tv0";
                    Toast.makeText(SchedulingActivity.this, "TV of Room1 is Scheduled OFF Sucessfully", Toast.LENGTH_LONG).show();

                    final String topic = "$aws/things/Lamp/shadow/update";
                    final String msg = "{\n" +
                            "    \"state\" : {\n" +
                            "        \"desired\" : {\n" +
                            "\t    \"thing\": \""+combine+"\"\n" +
                            "         }\n" +
                            "     }\n" +
                            "}";

                    try {
                        mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Publish error.", e);
                    }
                }





            }
        });







    }

    public boolean DateTimeValidation(String hrs,String mins,String year,String month,String day)
    {

        if (hrs.matches("\\d{2}")&&mins.matches("\\d{2}")&&year.matches("\\d{4}")&&month.matches("\\d{2}")&&day.matches("\\d{2}")){

            int h= Integer.parseInt(hrs);
            int m= Integer.parseInt(mins);
            int y= Integer.parseInt(year);
            int mo= Integer.parseInt(month);
            int d= Integer.parseInt(day);
            if(h>-1&&h<24)
            {
                if(m>-1&&m<60)
                {
                    if(y>2019)
                    {
                        if(mo>0&&mo<13)
                        {
                            if(d>0&&d<32)
                            {
                                return true;

                            }
                            else
                            {
                                Toast.makeText(SchedulingActivity.this, "Error: Enter Day should be in range 01-31", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(SchedulingActivity.this, "Error: Enter Month should be in range 01-12", Toast.LENGTH_LONG).show();

                        }
                    }
                    else
                    {
                        Toast.makeText(SchedulingActivity.this, "Error: Enter Year should be Greater than 2019", Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    Toast.makeText(SchedulingActivity.this, "Error: Enter Minutes should be in range 00-59", Toast.LENGTH_LONG).show();

                }


            }
            else
            {
                Toast.makeText(SchedulingActivity.this, "Error: Enter Hours should be in range 00-23", Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            Toast.makeText(SchedulingActivity.this, "Enter the Exact size in all Text Fields as in Hints", Toast.LENGTH_LONG).show();
        }
        return false;
    }



    void initIoTClient() {
        Region region = Region.getRegion(cls.MY_REGION);

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, cls.CUSTOMER_SPECIFIC_ENDPOINT);

        // Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
        // MQTT pings every 10 seconds.
        mqttManager.setKeepAlive(10);

        // Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
        // AWS IoT will publish this message to alert other clients.
        AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("my/lwt/topic",
                "Android client lost connection", AWSIotMqttQos.QOS0);
        mqttManager.setMqttLastWillAndTestament(lwt);

        // IoT Client (for creation of certificate if needed)
        mIotAndroidClient = new AWSIotClient(AWSMobileClient.getInstance());
        mIotAndroidClient.setRegion(region);

        keystorePath = getFilesDir().getPath();
        keystoreName = cls.KEYSTORE_NAME;
        keystorePassword = cls.KEYSTORE_PASSWORD;
        certificateId = cls.CERTIFICATE_ID;

        // To load cert/key from keystore on filesystem
        try {
            if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
                        keystoreName, keystorePassword)) {
                    Log.i(LOG_TAG, "Certificate " + certificateId
                            + " found in keystore - using for MQTT.");
                    // load keystore from file into memory to pass on connection
                    clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                            keystorePath, keystoreName, keystorePassword);
                    /* initIoTClient is invoked from the callback passed during AWSMobileClient initialization.
                    The callback is executed on a background thread so UI update must be moved to run on UI Thread. */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //btnConnect.setEnabled(true);
                        }
                    });
                } else {
                    Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
                }
            } else {
                Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
        }

        if (clientKeyStore == null) {
            Log.i(LOG_TAG, "Cert/key was not found in keystore - creating new key and certificate.");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Create a new private key and certificate. This call
                        // creates both on the server and returns them to the
                        // device.
                        CreateKeysAndCertificateRequest createKeysAndCertificateRequest =
                                new CreateKeysAndCertificateRequest();
                        createKeysAndCertificateRequest.setSetAsActive(true);
                        final CreateKeysAndCertificateResult createKeysAndCertificateResult;
                        createKeysAndCertificateResult =
                                mIotAndroidClient.createKeysAndCertificate(createKeysAndCertificateRequest);
                        Log.i(LOG_TAG,
                                "Cert ID: " +
                                        createKeysAndCertificateResult.getCertificateId() +
                                        " created.");

                        // store in keystore for use in MQTT client
                        // saved as alias "default" so a new certificate isn't
                        // generated each run of this application
                        AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
                                createKeysAndCertificateResult.getCertificatePem(),
                                createKeysAndCertificateResult.getKeyPair().getPrivateKey(),
                                keystorePath, keystoreName, keystorePassword);

                        // load keystore from file into memory to pass on
                        // connection
                        clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                                keystorePath, keystoreName, keystorePassword);

                        // Attach a policy to the newly created certificate.
                        // This flow assumes the policy was already created in
                        // AWS IoT and we are now just attaching it to the
                        // certificate.
                        AttachPrincipalPolicyRequest policyAttachRequest =
                                new AttachPrincipalPolicyRequest();
                        policyAttachRequest.setPolicyName(cls.AWS_IOT_POLICY_NAME);
                        policyAttachRequest.setPrincipal(createKeysAndCertificateResult
                                .getCertificateArn());
                        mIotAndroidClient.attachPrincipalPolicy(policyAttachRequest);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //btnConnect.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(LOG_TAG,
                                "Exception occurred when generating new private key and certificate.",
                                e);
                    }
                }
            }).start();
        }
    }





}

