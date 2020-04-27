package com.decalthon.helmet.stability.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MarkerDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MarkerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarkerDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String noteParam1;
    private String mParam2;
    private final String timeFormat = "yyyy-mm-dd, HH:mm:ss";

    private OnFragmentInteractionListener mListener;

    public MarkerDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarkerDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarkerDialogFragment newInstance(String param1, String param2) {
        MarkerDialogFragment fragment = new MarkerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            noteParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        /*ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
        actionbar.getCustomView().findViewById(R.id.back_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkerDialogFragment.this.dismiss();
            }
        });*/

        View view = inflater.inflate(R.layout.fragment_marker_dialog, container, false);
        TextView dtTemp = view.findViewById(R.id.date_time_view) ;
        dtTemp.setText(new SimpleDateFormat(timeFormat, Locale.getDefault()).format(new Date()));
        EditText textEditor = view.findViewById(R.id.text_editor_view);
//        TextEditor.setText(.toString());

        view.findViewById(R.id.close_marker_note_popup).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MarkerDialogFragment.this.dismiss();
                    }
        });

        view.findViewById(R.id.marker_data_save_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String check = textEditor.getText().toString();

                    }
                });

        view.findViewById(R.id.marker_data_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Note", noteParam1);
                if(getTargetFragment() != null) {
                    getTargetFragment().onActivityResult
                            (getTargetRequestCode(), Activity.RESULT_OK, intent);
                }
            }
        });

        if(textEditor.getText().toString().isEmpty()) {
            dtTemp.setText(new SimpleDateFormat(timeFormat, Locale.getDefault()).format(new Date()));
        }else{
            textEditor.setText(noteParam1);
        }
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
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
     * Dismiss the fragment and its dialog.  If the fragment was added to the
     * back stack, all back stack state up to and including this entry will
     * be popped.  Otherwise, a new transaction will be committed to remove
     * the fragment.
     */
    @Override
    public void dismiss() {
        super.dismiss();
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
