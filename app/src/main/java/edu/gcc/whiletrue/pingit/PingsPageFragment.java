package edu.gcc.whiletrue.pingit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PingsPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PingsPageFragment extends Fragment {

    private ArrayAdapter<String> myAdapter;

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

        ArrayList<String> pingData = new ArrayList<String>();
        pingData.add("Number one");
        pingData.add("Number two");
        pingData.add("Number three");

        myAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, pingData);

        ListView pingsListView = (ListView) rootView.findViewById(R.id.listview_pings);
        pingsListView.setAdapter(myAdapter);

        return rootView;
    }

}
