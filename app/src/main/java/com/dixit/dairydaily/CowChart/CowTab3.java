package com.dixit.dairydaily.CowChart;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.Models.RateChartModel;
import com.dixit.dairydaily.MyAdapters.RateChartAdapter;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.R;

import java.util.ArrayList;
import java.util.List;

public class CowTab3 extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "CowTab3";

    public CowTab3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<RateChartModel> list = new ArrayList<>();
        DbHelper dbHelper = new DbHelper(getContext());
        String nameOfSnf = getActivity().getIntent().getStringExtra("Name");
        if(nameOfSnf.equals("Buffalo")){

            Cursor data = dbHelper.getBuffaloSNFTable();

            if(data.getCount() != 0){
                while(data.moveToNext()){
                    list.add(new RateChartModel(data.getString(0), data.getString(3)));
                    //Log.d(TAG, "CowTab1: " + data.getString(0) + " " + data.getString(1));
                }
            }else{

            }
            RateChartAdapter adapter = new RateChartAdapter(getContext(),list);
            recyclerView.setAdapter(adapter);
        }
        else if(nameOfSnf.equals("Cow")) {
            Cursor data = dbHelper.getSNFTable();
            list.add(new RateChartModel(data.getColumnName(3), ""));
            if (data.getCount() != 0) {
                while (data.moveToNext()) {
                    list.add(new RateChartModel(data.getString(0), data.getString(3)));
                    //Log.d(TAG, "CowTab1: " + data.getString(0) + " " + data.getString(1));
                }
            } else {

            }
            RateChartAdapter adapter = new RateChartAdapter(getContext(),list);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
