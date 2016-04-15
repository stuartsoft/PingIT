package edu.gcc.whiletrue.pingit;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PingsPageFragment extends Fragment{

    ArrayList<Ping> pingsList;
    PingArrayAdapter pingArrayAdapter;
    private networkStatusCallback mNetworkCallback;
    private View fragmentRootView;
    private ListView pingsListView;
    private TextView noPingsTxt;
    private SwipeRefreshLayout swipeRefreshLayout;
    final int delay = 5000; //milliseconds
    public CheckPingUpdates checkPingUpdates; //async task for refreshing pings

    public interface networkStatusCallback {
        public boolean checkNetworkStatus();
    }

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

        @Override
        public int getCount() {
            return myPings.size();
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
        }

        try {
            mNetworkCallback = (networkStatusCallback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement networkStatusCallback");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_pings_page, container, false);

        //Put the new Pings into the list
        pingArrayAdapter = new PingArrayAdapter(getContext(),
                R.layout.ping_list_template, pingsList);
        pingsListView = (ListView) rootView.findViewById(R.id.listview_pings);
        noPingsTxt = (TextView) rootView.findViewById(R.id.noPingsTxt);
        pingsListView.setAdapter(pingArrayAdapter);
        fragmentRootView = rootView;
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        hideShowList();

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener(){
                    @Override
                    public void onRefresh() {
                        checkPingUpdates = new CheckPingUpdates(ParseUser.getCurrentUser(),
                                fragmentRootView, fragmentRootView.getContext());
                        checkPingUpdates.execute();
                    }
                }
        );

        return rootView;
    }

    private void hideShowList(){
        if (pingArrayAdapter.myPings.size() == 0){
            noPingsTxt.setVisibility(View.VISIBLE);
            pingsListView.setVisibility(View.GONE);
        }
        else{
            noPingsTxt.setVisibility(View.GONE);
            pingsListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (checkPingUpdates != null)
            checkPingUpdates.cancel(true);//cancel any updates
    }

    private class CheckPingUpdates extends AsyncTask<String, Void, Integer> {
        ParseUser user;
        final View view;
        Context context;
        List<ParseObject> pingsList;

        public CheckPingUpdates (ParseUser user, View view, Context context) {
            this.user = user;
            this.view = view;
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            if (!mNetworkCallback.checkNetworkStatus()){
                return -1;
            }
            try { //Query Parse for the user's pings
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Pings");
                query.whereEqualTo("User", user);
                query.orderByDescending("createdAt");
                pingsList = query.find();
            } catch (ParseException e) {return e.getCode();}//return exception code
            return 0;//no issues
        }

        @Override
        protected void onPostExecute(Integer errorCode) {
            if (errorCode == 0) { //Populate the pings list if everything is clear
                ArrayList<Ping> pingData = new ArrayList<Ping>();

                for (int i = 0; i < pingsList.size(); i++) {
                    try {
                        //Format the raw date into something more readable
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a");

                        Date rawDate = pingsList.get(i).getDate("Date");
                        String formattedDate = formatter.format(rawDate);

                        //Populate each Ping object
                        pingData.add(new Ping(pingsList.get(i).getString("Title"),
                                pingsList.get(i).getString("Message"), formattedDate));
                    }catch(Exception e){}
                }
                //update the adapter and listview
                pingArrayAdapter.myPings = pingData;
                pingArrayAdapter.notifyDataSetChanged();
                hideShowList();
                swipeRefreshLayout.setRefreshing(false);
            }
            else {
                Log.e(getString(R.string.log_error), "Could not fetch pings");
            }
        }
    }

}