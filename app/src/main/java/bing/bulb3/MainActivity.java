package bing.bulb3;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
public class MainActivity extends AppCompatActivity  {
    private View view;
    private AlertDialog pop;
    private AlertDialog.Builder builder;
    private int new_device = 1;
    private TextView tv_name;
    private TextView tv_num;
    private Context mContext;
    private List<Device> mData = null;
    private DeviceAdapter mAdapter = null;
    private ListView list_device;
    private TextView empty;
    private Toast toast;
    public static final String FILE_NAME = "my_data";
    int data_len = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        getSupportActionBar().setIcon(R.drawable.bulb );
        setTitle("");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable( Color.parseColor("#000000")));
        //menu  menuAlert
        builder = new AlertDialog.Builder( this );
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        view = inflater.inflate( R.layout.new_pop, null, false );
        builder.setView( view );
        pop = builder.create();
        mData = new LinkedList<>();
       //list_view
        mContext = MainActivity.this;
        mAdapter = new DeviceAdapter( (LinkedList<Device>) mData, mContext );
        list_device = (ListView) findViewById( R.id.list_device );
        empty = (TextView) findViewById(R.id.empty);
        list_device.setAdapter( mAdapter );
        list_device.setEmptyView(empty);
        read();
        //menu
        list_device.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, "刪除裝置");
            }
        });
        //長按鍵
        list_device.setOnItemLongClickListener( new OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long id) {
                //刪除提示
                Log.d( "bing","delConfirmPOP" );
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("確認刪除?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //刪除
                                Log.d( "bing","delConfirm" );
                                mData.remove( position );
                                mAdapter.notifyDataSetChanged();
                                delete(String.valueOf(position));
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //取消
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });
        //短按鍵
        list_device.setOnItemClickListener( new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, final int position, long id) {
                //裝置選定
                Intent intent = new Intent(  );
                intent.putExtra("number",/*DEVICE_num*/((TextView)view.findViewById( R.id.d_num )).getText().toString().trim());
                intent.setClass(MainActivity.this,Main2.class);
                startActivity( intent );
            }
        });
        //menuAlert
        pop.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                tv_name = (TextView)((AlertDialog) dialog).findViewById( R.id.d_name );
                tv_name.setText("");
                tv_num = (TextView)((AlertDialog) dialog).findViewById( R.id.d_num);
                tv_num.setText("");
                Button N = (Button) ((AlertDialog) dialog).findViewById( R.id.N );
                Button Y = (Button) ((AlertDialog) dialog).findViewById( R.id.Y );
                N.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                    }
                } );
                Y.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //確認  action
                        String str_name = tv_name.getText().toString().trim();
                        String str_num = tv_num.getText().toString().trim();
                        if(!Objects.equals( str_num, "" )){
                            if(Objects.equals( str_name, "" )) str_name = str_num;

                            mData.add(new Device(str_name, str_num));
                            mAdapter.notifyDataSetChanged();
                            String temp =  str_name + ":" + str_num;
                            save(String.valueOf(data_len),temp);
                            pop.dismiss();
                        }
                        else display("請輸入裝置ID");
                    }
                } );
            }
        } );
    }
    //LIST內容
    public class Device {
        private String str_name, str_num;
        public Device(String str_name, String str_num) {
            this.str_name = str_name;
            this.str_num = str_num;
        }
        public String getname() {
            return str_name;
        }
        public String getnum() {
            return str_num;
        }
        public void setname(String str_name) {
            this.str_name = str_name;
        }
        public void setnum(String str_num) {
            this.str_num = str_num;
        }

    }
    //MENU create
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add( 1, new_device, 1, "新增裝置" );
        return true;
    }
    //MENU act
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == new_device) pop.show();
        return true;
    }



    //保存
    void save(String index,String content) {
        SharedPreferences sp = getSharedPreferences( FILE_NAME, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit();
        editor.putString( index, content );
        editor.commit();
        data_len++;
    }
    //刪除
    void delete(String index) {
        SharedPreferences sp = getSharedPreferences( FILE_NAME, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(index);
        //整理數據
        int INT_index = Integer.valueOf(index);
        while(sp.getString(String.valueOf(INT_index+1),null)!=null){
            editor.putString(String.valueOf(INT_index),sp.getString(String.valueOf(INT_index+1),null));
            INT_index++;
        }
        editor.remove(String.valueOf(data_len-1));
        data_len--;
        editor.commit();
    }
    //讀取
    void read() {
        SharedPreferences sp = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE );
        while(sp.getString(String.valueOf(data_len),null)!=null){
            String value_of_data = sp.getString(String.valueOf(data_len),null);
            Log.d( "bing","get"+ value_of_data);
            String[] data_temp = value_of_data.split( ":" );
            mData.add(new Device(data_temp[0], data_temp[1]));
            mAdapter.notifyDataSetChanged();
            data_len++;
        }
    }
    boolean doubleBackToExitPressedOnce = false;
    //雙擊返回
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    public void display(String string) {
        toast = Toast.makeText( getApplicationContext(), string, Toast.LENGTH_SHORT );
        toast.setGravity( Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0 );
        TextView v = (TextView) toast.getView().findViewById( android.R.id.message );
        v.setTextColor( Color.WHITE );
        toast.show();
    }
}

