package bing.bulb3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.LinkedList;
import static bing.bulb3.R.id.d_name;
//自定義ADAPTER
public class DeviceAdapter extends BaseAdapter {
    private LinkedList<MainActivity.Device> mData;
    private Context mContext;
    public DeviceAdapter(LinkedList<MainActivity.Device> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return mData.size();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from( mContext ).inflate( R.layout.list_1, parent, false );
        TextView txt_aName = (TextView) convertView.findViewById( d_name );
        TextView txt_aSpeak = (TextView) convertView.findViewById( R.id.d_num );
        txt_aName.setText( mData.get( position ).getname() );
        txt_aSpeak.setText( mData.get( position ).getnum() );
        return convertView;
    }
}
