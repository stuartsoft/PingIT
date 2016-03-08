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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
        //Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pings_page, container, false);

        GetPingsTask getPings = new GetPingsTask(ParseUser.getCurrentUser(),
                rootView, inflater.getContext());

        //Run the background async task to get the user's pings
        getPings.execute();

        return rootView;
    }

    private class GetPingsTask extends AsyncTask<String, Void, Integer> {
        ParseUser user;
        final View view;
        Context context;
        List <ParseObject> pingsList;

        public GetPingsTask (ParseUser user, View view, Context context) {
            this.user = user;
            this.view = view;
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            try { //Query Parse for the user's pings
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Pings");
                query.whereEqualTo("User", user);
                pingsList = query.find();
            } catch (ParseException e) {return e.getCode();}//return exception code
            return 0;//no issues
        }

        @Override
        protected void onPostExecute(Integer errorCode) {
            if (errorCode == 0) { //Populate the pings list if everything is clear
                ArrayList<Ping> pingData = new ArrayList<Ping>();

                for (int i = 0; i < pingsList.size(); i++) {
                    //Format the raw date into something more readable
                    SimpleDateFormat formatter =
                            new SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a");

                    Date rawDate = pingsList.get(i).getDate("Date");
                    String formattedDate = formatter.format(rawDate);

                    //Populate each Ping object
                    pingData.add(new Ping(pingsList.get(i).getString("Title"),
                            pingsList.get(i).getString("Message"), formattedDate));
                }

                //Put the new Pings into the list
                pingArrayAdapter = new PingArrayAdapter(getContext(),
                        R.layout.ping_list_template, pingData);
                ListView pingsListView = (ListView) view.findViewById(R.id.listview_pings);
                pingsListView.setAdapter(pingArrayAdapter);
            }
            else {
                Log.e(getString(R.string.log_error),
                        "onPostExecute: User has no network connection. Cannot load pings.");
                //TODO: Add a custom page here for when the user has no network connection
                //and allow them to refresh with a button
            }
        }
    }
}