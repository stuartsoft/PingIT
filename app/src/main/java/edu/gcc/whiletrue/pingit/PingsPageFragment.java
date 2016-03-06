package edu.gcc.whiletrue.pingit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PingsPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PingsPageFragment extends Fragment {

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

        @Override // Gets the data into a presentable form to be displayed.
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)myContext).getLayoutInflater();

            // Get references for view elements
            View row = inflater.inflate(myResource, parent, false);
            TextView titleLine = (TextView) row.findViewById(R.id.ping_template_title);
            TextView messageLine = (TextView) row.findViewById(R.id.ping_template_message);
            TextView dateLine = (TextView) row.findViewById(R.id.ping_template_date);

            // Set the values from the data.
            titleLine.setText(myPings.get(position).getTitle());
            messageLine.setText(myPings.get(position).getMessage());
            dateLine.setText(myPings.get(position).getDate());
            return row;
        }
    }

    PingArrayAdapter pingArrayAdapter;

    public PingsPageFragment() {
        // Required empty public constructor
    }

    public static PingsPageFragment newInstance() {
        PingsPageFragment fragment = new PingsPageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pings_page, container, false);

        ArrayList<Ping> pingData = new ArrayList<Ping>();

        pingData.add(new Ping());
        pingData.add(new Ping("This is a title", "This is a message", "01/01/1970 at 12:00 AM"));
        pingData.add(new Ping("Y2K", "We're all going to die", "12/31/1999 at 11:59 PM"));

        pingArrayAdapter = new PingArrayAdapter(getContext(), R.layout.ping_list_template, pingData);
        ListView pingsListView = (ListView) rootView.findViewById(R.id.listview_pings);
        pingsListView.setAdapter(pingArrayAdapter);

        return rootView;
    }
    //Asynchronous task to make sure this doesn't lag. Check what Stuart did with register/login
    //Async tasks have a before method, a run in background method, and a post request method
    //Pre and post request are run on the UI and only background one is run in background
    //Want to have nothing for pre-run; background methods makes query, gets response, and interpret response
    //On post, actually create new pings
    //Constructor, do in background, and on post execute
}