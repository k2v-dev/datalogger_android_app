package com.decalthon.helmet.stability.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.model.MarkerNote;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.fragment.app.FragmentManager;
import de.greenrobot.event.EventBus;

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
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    // TODO: Rename and change types of parameters
    private String marker_note;
    private String note_type;
    private Date marker_date;
    private Date lst_edited_date;
    private final String timeFormat = "yyyy-MMM-dd, HH:mm:ss";

    private OnFragmentInteractionListener mListener;

    public MarkerDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param marker_note Parameter 1.
     * @param note_type Parameter 2.
     * @param marker_timestamp Parameter 3
     * @return A new instance of fragment MarkerDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarkerDialogFragment newInstance(String marker_note, String note_type, long marker_timestamp, long note_timestamp) {
        MarkerDialogFragment fragment = new MarkerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, marker_note);
        args.putString(ARG_PARAM2, note_type);
        args.putLong(ARG_PARAM3, marker_timestamp);
        args.putLong(ARG_PARAM4, note_timestamp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            marker_note = getArguments().getString(ARG_PARAM1);
            note_type = getArguments().getString(ARG_PARAM2);
            long ts = getArguments().getLong(ARG_PARAM3);
            marker_date = new Date(ts);
            ts = getArguments().getLong(ARG_PARAM4);
            if(ts == 0){
                lst_edited_date = null;
            }else{
                lst_edited_date = new Date(ts);
            }

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

        dtTemp.setText(new SimpleDateFormat(timeFormat, Locale.getDefault()).format(marker_date));
        if(note_type.equalsIgnoreCase("2")){
            dtTemp.setVisibility(View.INVISIBLE);
        }else{
            dtTemp.setVisibility(View.VISIBLE);
        }
        EditText textEditor = view.findViewById(R.id.text_editor_view);
        TextView lstEditDt = view.findViewById(R.id.last_edit_tv) ;
        if(lst_edited_date != null){
            String dateStr = HtmlCompat.fromHtml("<i>Last edited:</i>", 0)+new SimpleDateFormat(timeFormat, Locale.getDefault()).format(lst_edited_date);
            lstEditDt.setText(dateStr);
        }else{
            lstEditDt.setText("");
        }

        textEditor.setText(marker_note);
//        TextEditor.setText(.toString());

        view.findViewById(R.id.close_marker_note_popup).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MarkerDialogFragment.this.dismiss();
                    }
        });
        view.findViewById(R.id.cancelButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MarkerDialogFragment.this.dismiss();
                    }
                });
        view.findViewById(R.id.deleteButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteConfirmDialog();
                    }
                });

        view.findViewById(R.id.marker_data_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker_note = textEditor.getText().toString();
//                Intent intent = new Intent();
//                intent.putExtra("Note", marker_note);
//                if(getTargetFragment() != null) {
//                    getTargetFragment().onActivityResult
//                            (getTargetRequestCode(), Activity.RESULT_OK, intent);
//                }
                EventBus.getDefault().post(new MarkerNote(note_type, marker_note));
                dismiss();
            }
        });

//        if(textEditor.getText().toString().isEmpty()) {
//            dtTemp.setText(new SimpleDateFormat(timeFormat, Locale.getDefault()).format(marker_date));
//        /*}else{
//            textEditor.setText(noteParam1);*/
//        }
        return view;
    }

    private void showDeleteConfirmDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Alert")
                .setMessage("Do you want to delete this note?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new MarkerNote(note_type, ""));
                        MarkerDialogFragment.this.dismiss();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
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
