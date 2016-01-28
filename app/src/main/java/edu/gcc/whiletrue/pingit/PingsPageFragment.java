package edu.gcc.whiletrue.pingit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PingsPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PingsPageFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_pings_page, container, false);
    }

}
