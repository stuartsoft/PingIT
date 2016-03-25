package edu.gcc.whiletrue.pingit;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */


public class PingsLoadingFragment extends Fragment {


    public interface PingsPageInterface {
        void displayPingsList(ArrayList<Ping> data);
        void loadPings();
    }

    private PingsPageInterface mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (PingsPageInterface) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement pingsPageInterface");
        }
    }

    public PingsLoadingFragment() {
        // Required empty public constructor
    }

    public static PingsLoadingFragment newInstance() {
        PingsLoadingFragment fragment = new PingsLoadingFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pings_page, container, false);

        GetPingsTask getPings = new GetPingsTask(ParseUser.getCurrentUser(),
                rootView, inflater.getContext());

        getPings.execute();

        return inflater.inflate(R.layout.fragment_pings_loading, container, false);
    }

    private class GetPingsTask extends AsyncTask<String, Void, Integer> {
        ParseUser user;
        final View view;
        Context context;
        List<ParseObject> pingsList;

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
                    //TODO figure out why this is throwing an error when the user has no pings
                    //something to do with Date.getTime() being called on a null object reference
                }

                mCallback.displayPingsList(pingData);


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
