package bing.bulb3;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class Main2 extends AppCompatActivity implements View.OnClickListener{
    private int closeBulb = 1;
    private String DEVICE_num;
    private AbsoluteLayout Layout;
    private int cid = 0;
    private String IP = null;
    private Vibrator vibrator;
    final JsonObject colorJson = new JsonObject();
    final JsonObject crypto = new JsonObject();
    public ActionBar bar;
    public SeekBar seekBar;
    public int lux = 50;
    public Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        Log.d( "bing", "onCreate" );
        setContentView( R.layout.activity_main2 );
        findViewById( R.id.layout );
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        Layout = (AbsoluteLayout) findViewById( R.id.layout );
        Button buttonW = (Button) findViewById( R.id.buttonW );
        Button buttonR = (Button) findViewById( R.id.buttonR );
        Button buttonG = (Button) findViewById( R.id.buttonG );
        Button buttonB = (Button) findViewById( R.id.buttonB );
        Button buttonY = (Button) findViewById( R.id.buttonY );
        seekBar = (SeekBar)findViewById( R.id.seekbar );
        buttonW.setOnClickListener(this);
        buttonR.setOnClickListener(this);
        buttonG.setOnClickListener(this);
        buttonB.setOnClickListener(this);
        buttonY.setOnClickListener(this);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable( Color.parseColor("#000000")));
        Intent intent2 = getIntent();
        setTitle( "mode: set color" );
        //getActionBar().setIcon(R.drawable.bulb );
        vibrator = (Vibrator) getSystemService( Service.VIBRATOR_SERVICE );
        DEVICE_num = intent2.getStringExtra( "number" );
        colorJson.addProperty( "cid", cid );
        colorJson.addProperty( "seq", 12345678 );
        crypto.addProperty( "cmd", "pwm" );
    }
    //MENU create
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add( 1, closeBulb, 1, "關閉裝置" );
        return true;
    }
    //MENU act
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == closeBulb) {
            //關燈
            vibrator.vibrate( 500 );
            colorJson.addProperty( "cid", cid );
            crypto.addProperty( "param", "-g0 -l0 -g1 -l0 -g2 -l0 -g3 -l0 -g4 -l0 -s" );
            colorJson.add( "crypto", crypto );
            setDevice( colorJson );
            toast.cancel();
            JsonObject logoutJson = new JsonObject();
            JsonObject logoutJson2 = new JsonObject();
            logoutJson.addProperty( "cid", cid );
            logoutJson.addProperty( "seq", 12345678 );
            logoutJson.add( "crypto", logoutJson2 );
            logoutJson2.addProperty( "cmd", "logout" );
            setDevice( logoutJson );
            finish();
        }
        else onBackPressed();
        return true;
    }
    @Override//BAR,background
    protected void onResume() {
        super.onResume();
        Log.d( "bing", "onResume" );
        Log.d( "bing", String.format( "http://bnw-auth.triclouds-iot.com/iotAPI/getServer/%s", DEVICE_num ) );
        //get device server
        Ion.with( this )
                .load( "http://bnw-auth.triclouds-iot.com/iotAPI/getServer/" + DEVICE_num )
                .asJsonObject()
                .setCallback( new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject IP_result) {
                        if (e == null) {
                            if (IP_result != null) {
                                int stat = IP_result.get( "stat" ).getAsInt();
                                if(stat == 200) {
                                    IP = IP_result.get( "server" ).getAsString();
                                    //get device status
                                    Ion.with( getApplicationContext() )
                                            .load( "http://" + IP + "/iotAPI/getOnline?uid=" + DEVICE_num )
                                            .asJsonObject()
                                            .setCallback( new FutureCallback<JsonObject>() {
                                                @Override
                                                public void onCompleted(Exception e, JsonObject online_result) {
                                                    Log.d( "bing", "http://" + IP + "/iotAPI/getOnline?uid=" + DEVICE_num );
                                                    if (e == null) {
                                                        if (online_result.get( "online" ).getAsInt() == 1) {
                                                            display( "裝置在線\n登入中．．．" );
                                                            //LOGIN
                                                            JsonObject loginJson = new JsonObject();
                                                            loginJson.addProperty( "seq", 12345678 );
                                                            loginJson.addProperty( "cmd", "login" );
                                                            loginJson.addProperty( "uid", DEVICE_num );
                                                            loginJson.addProperty( "username", "admin" );
                                                            loginJson.addProperty( "RN", 888888888 );
                                                            loginJson.addProperty( "tcn1", md5( DEVICE_num + "admin888888888" ) );
                                                            Ion.with( getApplicationContext() )
                                                                    .load( "http://" + IP + "/iotAPI/setDevice/" + DEVICE_num )
                                                                    .setJsonObjectBody( loginJson )
                                                                    .asJsonObject()
                                                                    .setCallback( new FutureCallback<JsonObject>() {
                                                                        @Override
                                                                        public void onCompleted(Exception e, JsonObject LOGIN_result) {
                                                                            if (e == null && LOGIN_result != null) {
                                                                                cid = LOGIN_result.get( "cid" ).getAsInt();
                                                                                //display("cid="+ Integer.toString(cid));
                                                                                s_display( "登入成功！" );
                                                                            } else
                                                                                display( "error" );
                                                                        }
                                                                    } );
                                                        } else display( "裝置不在線" );
                                                    }
                                                }
                                            } );
                                }else {
                                    new AlertDialog.Builder(Main2.this)
                                            .setMessage("裝置不存在")
                                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            })
                                            .show();
                                }
                            } else {
                                e.printStackTrace();
                                display( "ERROR" );
                                finish();
                            }
                        }
                    }
                } );
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lux = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //觸摸BAR
                toast.cancel();
                display("*****改變燈泡顏色後生效*****");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //放開BAR
                toast.cancel();
                s_display("亮度:" + lux + "  / 255 ");
            }
        });
    }
    public static String md5(String string) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance( "MD5" );
            byte[] bytes = md5.digest( string.getBytes() );
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString( b & 0xff );
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    public void display(String string) {
        toast = Toast.makeText( getApplicationContext(), string, Toast.LENGTH_LONG );
        toast.setGravity( Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0 );
        TextView v = (TextView) toast.getView().findViewById( android.R.id.message );
        v.setTextColor( Color.WHITE );
        toast.show();
    }
    public void s_display(String string) {
        toast = Toast.makeText( getApplicationContext(), string, Toast.LENGTH_SHORT );
        toast.setGravity( Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0 );
        TextView v = (TextView) toast.getView().findViewById( android.R.id.message );
        v.setTextColor( Color.WHITE );
        toast.show();
    }
    public void setDevice(JsonObject useJson) {
        display("處理中．．．請勿頻繁操作");
        Ion.with( getApplicationContext() )
                .load( "http://" + IP + "/iotAPI/setDevice/" + DEVICE_num )
                .setJsonObjectBody( useJson )
                .asJsonObject()
                .setCallback( new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null && result != null) {
                            toast.cancel();
                        } else display( "error" );
                    }
                } );
    }
    @Override
    public void onClick(View v) {
        toast.cancel();
        vibrator.vibrate( 100 );
        colorJson.addProperty( "cid", cid );
        switch (v.getId()){
            case R.id.buttonW:
                crypto.addProperty( "param", "-g0 -l" + Integer.toString(lux) + " -g1 -l0 g2 -l0 -g3 -l0 -g4 -l0 -s" );
                break;
            case R.id.buttonY:
                crypto.addProperty( "param", "-g0 -l0 -g1 -l" + Integer.toString(lux) + " g2 -l0 -g3 -l0 -g4 -l0 -s" );
                break;
            case R.id.buttonR:
                crypto.addProperty( "param", "-g0 -l0 -g1 -l0 -g2 -l" + Integer.toString(lux) + " -g3 -l0 -g4 -l0 -s" );
                break;
            case R.id.buttonG:
                crypto.addProperty( "param", "-g0 -l0 -g1 -l0 -g2 -l0 -g3 -l" + Integer.toString(lux) + " -g4 -l0 -s" );
                break;
            case R.id.buttonB:
                crypto.addProperty( "param", "-g0 -l0 -g1 -l0 -g2 -l0 -g3 -l0 -g4 -l" + Integer.toString(lux) + " -s" );
                break;
        }
        colorJson.add( "crypto", crypto );
        setDevice( colorJson );
    }
}




