package edu.gcc.whiletrue.pingit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PingsPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class PingsPageFragment extends Fragment{

    ArrayList<Ping> pingsList;
    PingArrayAdapter pingArrayAdapter;

    public class PingArrayAdapter extends ArrayAdapter<Ping> {
        Context myContext;
        int myResource;
        ArrayList<Ping> myPings;

        public PingArrayAdapter(Context context, int resource, ArrayList<Ping> objects) {
            super(context, resource, objects);
            myContext = context;
            myResource = resource;
            myPings = objects;
        }

        @Override //Gets the data into a presentable form to be displayed.
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)myContext).getLayoutInflater();

            //Get references for view elements
            View row = inflater.inflate(myResource, parent, false);
            TextView titleLine = (TextView) row.findViewById(R.id.ping_template_title);
            TextView messageLine = (TextView) row.findViewById(R.id.ping_template_message);
            TextView dateLine = (TextView) row.findViewById(R.id.ping_template_date);

            //Set the values from the data.
            titleLine.setText(myPings.get(position).getTitle());
            messageLine.setText(myPings.get(position).getMessage());
            dateLine.setText(myPings.get(position).getDate());
            return row;
        }
    }

    public PingsPageFragment() {
        // Required empty public constructor
    }

    public static PingsPageFragment newInstance(ArrayList<Ping> data) {
        Bundle b = new Bundle();
        b.putSerializable("pinglist", data);

        PingsPageFragment fragment = new PingsPageFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();

        if (bundle != null)
            pingsList = (ArrayList<Ping>)bundle.getSerializable("pinglist");
        else {
            pingsList = new ArrayList<Ping>();
            //pingsList.add(new Ping());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pings_page, container, false);

        //Put the new Pings into the list
        pingArrayAdapter = new PingArrayAdapter(getContext(),
                R.layout.ping_list_template, pingsList);
        ListView pingsListView = (ListView) rootView.findViewById(R.id.listview_pings);
        pingsListView.setAdapter(pingArrayAdapter);
        return rootView;
    }


}