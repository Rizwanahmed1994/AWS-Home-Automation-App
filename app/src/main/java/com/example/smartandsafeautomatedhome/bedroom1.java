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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
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

public class bedroom1  extends AppCompatActivity {



    TextView tvStatus1;

    Button btnFanon;
    Button btnFanoff;

    ToggleButton StatusLight1R1;
    ToggleButton StatusLampR1;
    ToggleButton StatusACR1;
    ToggleButton StatusTVR1;

    Button  StatusfanoffR1;
    Button StatusfanonR1;

    MainActivity cls =new MainActivity();

    AWSIotClient mIotAndroidClient;
    AWSIotMqttManager mqttManager;
    String clientId;
    String keystorePath;
    String keystoreName;
    String keystorePassword;

    KeyStore clientKeyStore = null;
    String certificateId;

    ImageView imLight;
    ImageView imLamp;
    ImageView imAC;
    ImageView imTV;
    ImageView imFan;

    ToggleButton ConnectButton;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String LightR1 = "LR1";
    public static final String Light2R1 = "L2R1";
    public static final String TVR1 = "TR1";
    public static final String ACR1 = "AR1";
    public static final String FanR1 = "FR1";

    SharedPreferences sharedpreferences;


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


    public void onToggleClickedLight(View view) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (((ToggleButton) view).isChecked()) {
            // handle toggle on
            final String topic = "$aws/things/Device1/shadow/update";
            final String msg = "{\n" +
                    "    \"state\" : {\n" +
                    "        \"desired\" : {\n" +
                    "\t    \"thing\": \"light1\"\n" +
                    "         }\n" +
                    "     }\n" +
                    "}";

            try {
                mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Publish error.", e);
            }
            editor.putString(LightR1, "1");
            editor.commit();


            imLight.setImageResource(R.drawable.light_off);
        } else {
            // handle toggle off
            final String topic = "$aws/things/Device1/shadow/update";
            final String msg = "{\n" +
                    "    \"state\" : {\n" +
                    "        \"desired\" : {\n" +
                    "\t    \"thing\": \"light0\"\n" +
                    "         }\n" +
                    "     }\n" +
                    "}";

            try {
                mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Publish error.", e);
            }
            imLight.setImageResource(R.drawable.light_on);

            editor.putString(LightR1, "0");
            editor.commit();
        }
    }

    public void onToggleClickedAC(View view) {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (((ToggleButton) view).isChecked()) {
            // handle toggle on
            final String topic = "$aws/things/Device1/shadow/update";
            final String msg = "{\n" +
                    "    \"state\" : {\n" +
                    "        \"desired\" : {\n" +
                    "\t    \"thing\": \"ac1\"\n" +
                    "         }\n" +
                    "     }\n" +
                    "}";

            try {
                mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Publish error.", e);
            }
            editor.putString(ACR1, "1");
            editor.commit();

            imAC.setImageResource(R.drawable.acoff);
        } else {
            // handle toggle off
            final String topic = "$aws/things/Device1/shadow/update";
            final String msg = "{\n" +
                    "    \"state\" : {\n" +
                    "        \"desired\" : {\n" +
                    "\t    \"thing\": \"ac0\"\n" +
                    "         }\n" +
                    "     }\n" +
                    "}";

            try {
                mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Publish error.", e);
            }
            editor.putString(ACR1, "0");
            editor.commit();
            imAC.setImageResource(R.drawable.acon);
        }
    }

    /*public void onToggleClickedLamp(View view) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (((ToggleButton) view).isChecked()) {
            // handle toggle on
            final String topic = "$aws/things/Lamp/shadow/update";
            final String msg = "{\n" +
                    "    \"state\" : {\n" +
                    "        \"desired\" : {\n" +
                    "\t    \"lamp\": \"true\"\n" +
                    "         }\n" +
                    "     }\n" +
                    "}";

            try {
                mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Publish error.", e);
            }
            editor.putString(Light2R1, "1");
            editor.commit();

            imLamp.setImageResource(R.drawable.light_off);
        } else {
            // handle toggle off
            final String topic = "$aws/things/Lamp/shadow/update";
            final String msg = "{\n" +
                    "    \"state\" : {\n" +
                    "        \"desired\" : {\n" +
                    "\t    \"lamp\": \"false\"\n" +
                    "         }\n" +
                    "     }\n" +
                    "}";

            try {
                mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Publish error.", e);
            }
            editor.putString(Light2R1, "0");
            editor.commit();
            imLamp.setImageResource(R.drawable.light_on);
        }
    }*/

    public void onToggleClickedTV(View view) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (((ToggleButton) view).isChecked()) {
            // handle toggle on
            final String topic = "$aws/things/Device1/shadow/update";
            final String msg = "{\n" +
                    "    \"state\" : {\n" +
                    "        \"desired\" : {\n" +
                    "\t    \"thing\": \"tv1\"\n" +
                    "         }\n" +
                    "     }\n" +
                    "}";

            try {
                mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Publish error.", e);
            }
            editor.putString(TVR1, "1");
            editor.commit();

            imTV.setImageResource(R.drawable.tvoff);
        } else {
            // handle toggle off
            final String topic = "$aws/things/Device1/shadow/update";
            final String msg = "{\n" +
                    "    \"state\" : {\n" +
                    "        \"desired\" : {\n" +
                    "\t    \"thing\": \"tv0\"\n" +
                    "         }\n" +
                    "     }\n" +
                    "}";

            try {
                mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Publish error.", e);
            }
            editor.putString(TVR1, "0");
            editor.commit();
            imTV.setImageResource(R.drawable.tvon);
        }
    }

    public void onToggleClickedFanOn(View view) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        final String topic = "$aws/things/Fan/shadow/update";
        final String msg = "{\n" +
                "    \"state\" : {\n" +
                "        \"desired\" : {\n" +
                "\t    \"fan\": \"1\"\n" +
                "         }\n" +
                "     }\n" +
                "}";

        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
        editor.putString(FanR1, "1");
        editor.commit();
        imFan.setImageResource(R.drawable.fanon);
    }
    public void onToggleClickedFanOff(View view) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        final String topic = "$aws/things/Fan/shadow/update";
        final String msg = "{\n" +
                "    \"state\" : {\n" +
                "        \"desired\" : {\n" +
                "\t    \"fan\": \"0\"\n" +
                "         }\n" +
                "     }\n" +
                "}";

        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
        editor.putString(FanR1, "0");
        editor.commit();
        imFan.setImageResource(R.drawable.fan2);
    }




    public void publishClickOff(final View view,final String topic) {
        final String msg = "{\n" +
                "    \"state\" : {\n" +
                "        \"desired\" : {\n" +
                "\t    \"isLEDOn\": \"false\"\n" +
                "         }\n" +
                "     }\n" +
                "}";

        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }



    public void publishClick(final View view,final String topic) {

        final String msg = "{\n" +
                "    \"state\" : {\n" +
                "        \"desired\" : {\n" +
                "\t    \"isLEDOn\": \"true\"\n" +
                "         }\n" +
                "     }\n" +
                "}";

        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }


    public void disconnectClick(final View view) {
        try {
            mqttManager.disconnect();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Disconnect error.", e);
        }
    }

    public void statusrefresh()
    {
        SharedPreferences pref = getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        String stateLightR1 = pref.getString(LightR1,"0");
        String stateLampR1 = pref.getString(Light2R1,"0");
        String stateTVR1 = pref.getString(TVR1,"0");
        String stateACR1 = pref.getString(ACR1,"0");
        String stateFanR1 = pref.getString(FanR1,"0");
        StatusLight1R1= findViewById(R.id.btnLight_r1);
        StatusLampR1= findViewById(R.id.btnLampR1);
        StatusACR1= findViewById(R.id.btnACR1);
        StatusTVR1= findViewById(R.id.btnTVR1);


       if(stateLightR1.equals("1")){
           StatusLight1R1.setChecked(true);
           imLight.setImageResource(R.drawable.light_off);
       }
       else
       {
           StatusLight1R1.setChecked(false);
           imLight.setImageResource(R.drawable.light_on);
       }

        if(stateLampR1.equals("1")){
            StatusLampR1.setChecked(true);
            imLamp.setImageResource(R.drawable.light_off);
        }
        else
        {
            StatusLampR1.setChecked(false);
            imLamp.setImageResource(R.drawable.light_on);
        }

        if(stateTVR1.equals("1")){
            StatusTVR1.setChecked(true);
            imTV.setImageResource(R.drawable.tvon);
        }
        else
        {
            StatusTVR1.setChecked(false);
            imTV.setImageResource(R.drawable.tvoff);
        }

        if(stateACR1.equals("1")){
            StatusACR1.setChecked(true);
            imAC.setImageResource(R.drawable.acoff);
        }
        else
        {
            StatusACR1.setChecked(false);
            imAC.setImageResource(R.drawable.acon);
        }

        if(stateFanR1.equals("1")){
            btnFanon.setEnabled(false);
            btnFanoff.setEnabled(true);
            imFan.setImageResource(R.drawable.fanon);
        }
        else
        {
            btnFanoff.setEnabled(false);
            btnFanon.setEnabled(true);
            imFan.setImageResource(R.drawable.fan2);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bedroom1_activity);

        tvStatus1 = findViewById(R.id.tvStatus1);


        // MQTT client IDs are required to be unique per AWS IoT account.
        // This UUID is "practically unique" but does not _guarantee_
        // uniqueness.
        clientId = UUID.randomUUID().toString();

        btnFanon=findViewById(R.id.btnfanonR1);

        btnFanoff=findViewById(R.id.btnFanoffR1);

        imLight=(ImageView) findViewById(R.id.imageViewLight);
        imLamp=(ImageView) findViewById(R.id.imageViewLamp);
        imAC=(ImageView) findViewById(R.id.imageViewAC);
        imTV=(ImageView) findViewById(R.id.imageViewTV);
        imFan=(ImageView) findViewById(R.id.imageViewFan);
        ConnectButton=findViewById(R.id.btnConnectR1);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


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

        btnFanoff.setEnabled(false);

        btnFanoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                onToggleClickedFanOff(view);
                btnFanoff.setEnabled(false);
                btnFanon.setEnabled(true);
            }
        });

        btnFanon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                onToggleClickedFanOn(view);
                btnFanon.setEnabled(false);
                btnFanoff.setEnabled(true);
            }
        });

        SeekBar seekBar = findViewById(R.id.sbProgress);
        final TextView textView=findViewById(R.id.number_value);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress==0){
                    textView.setText("SLOW");
                    final String topic = "$aws/things/Fan/shadow/update";
                    final String msg = "{\n" +
                            "    \"state\" : {\n" +
                            "        \"desired\" : {\n" +
                            "\t    \"fan\": \"1\"\n" +
                            "         }\n" +
                            "     }\n" +
                            "}";

                    try {
                        mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Publish error.", e);
                    }

                }else if(progress==1){
                    textView.setText("NORMAL");
                    final String topic = "$aws/things/Fan/shadow/update";
                    final String msg = "{\n" +
                            "    \"state\" : {\n" +
                            "        \"desired\" : {\n" +
                            "\t    \"fan\": \"2\"\n" +
                            "         }\n" +
                            "     }\n" +
                            "}";

                    try {
                        mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Publish error.", e);
                    }
                } else if(progress==2){
                    textView.setText("Moderate");
                    final String topic = "$aws/things/Fan/shadow/update";
                    final String msg = "{\n" +
                            "    \"state\" : {\n" +
                            "        \"desired\" : {\n" +
                            "\t    \"fan\": \"3\"\n" +
                            "         }\n" +
                            "     }\n" +
                            "}";

                    try {
                        mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Publish error.", e);
                    }
                } else if(progress==3){
                    textView.setText("FAST");
                    final String topic = "$aws/things/Fan/shadow/update";
                    final String msg = "{\n" +
                            "    \"state\" : {\n" +
                            "        \"desired\" : {\n" +
                            "\t    \"fan\": \"4\"\n" +
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

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        statusrefresh();
        // put your code here...

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


