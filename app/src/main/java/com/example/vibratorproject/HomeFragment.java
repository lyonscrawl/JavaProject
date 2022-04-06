package com.example.vibratorproject;

import static com.example.vibratorproject.MainActivity.v;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static boolean onVibrate = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ShimmerTextView stv;
    private Shimmer shimmer;
    private SeekBar seekBarPause;
    private SeekBar seekBarVibrate;
    private TextView seekBarVibrateText;
    private TextView seekBarPauseText;
    private TextView messageText;
    private Button bStart;
    private LinearLayout block2;
    private FrameLayout frame;

    public HomeFragment() {
        // Required empty public constructor
    }

    public boolean getOnVibrate(){
        return onVibrate;
    }

    public void setOnVibrate(boolean onVibrate){
        this.onVibrate = onVibrate;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        setOnVibrate(false);
        v.cancel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Button
        bStart = view.findViewById(R.id.bStart);
        messageText = view.findViewById(R.id.msg_info);
        messageText.setText("Pour commencer la vibration");
        block2 = view.findViewById(R.id.block2);
        block2.setVisibility(View.INVISIBLE);
        frame = view.findViewById(R.id.fragHome);
        frame.setBackgroundColor(0xFF3700B3);
        bStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View viewer) {
                if(getOnVibrate()) {
                    //Deactivate
                    setOnVibrate(false);
                    messageText.setText("Pour commencer la vibration");
                    bStart.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFFFF")));
                    block2.setVisibility(View.INVISIBLE);
                    frame.setBackgroundColor(0xFF3700B3);
                    v.cancel();
                }
                else {
                    //Activate
                    setOnVibrate(true);
                    messageText.setText("Pour arrêter la vibration");
                    bStart.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#FF03DAC5")));
                    block2.setVisibility(View.VISIBLE);
                    frame.setBackgroundColor(0xFF6200EE);

                    if (v.hasVibrator()) {
                        // Start without a delay
                        // Vibrate for 500 milliseconds
                        // Sleep for 500 milliseconds
                        long[] pattern = {0, (seekBarVibrate.getProgress() * 10), (seekBarPause.getProgress() * 10)};
                        // Each element then alternates between vibrate, sleep, vibrate, sleep...
                        //long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
                        // The '0' here means to repeat indefinitely / -1 once
                        // '0' is actually the index at which the pattern keeps repeating from (the start)
                        // To repeat the pattern from any other point, you could increase the index, e.g. '1'
                        v.vibrate(pattern, 0);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Erreur, votre Android ne dispose pas de fonctions vibratoires !")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // START THE GAME!
                                    }
                                });
                        // Create the AlertDialog object and return it
                        builder.create();
                        builder.show();
                    }
                }
            }
        });

        //Shimmer
        stv = (ShimmerTextView) view.findViewById(R.id.shimmer_info);
        shimmer = new Shimmer();
        shimmer.start(stv);

        //SeekBar
        seekBarVibrate = view.findViewById(R.id.vibrateTime);
        seekBarPause = view.findViewById(R.id.pauseTime);
        seekBarVibrateText = view.findViewById(R.id.vibrateTimeText);
        seekBarPauseText = view.findViewById(R.id.pauseTimeText);

        seekBarVibrateText.setText("Réglage du temps de vibration : " + (seekBarVibrate.getProgress() * 10)  + "ms");
        seekBarPauseText.setText("Réglage du temps de pause : " + (seekBarPause.getProgress() * 10) + "ms");


        seekBarPause.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // updated continuously as the user slides the thumb
                seekBarPauseText.setText("Réglage du temps de pause : " + (progress * 10) + "ms");
                long[] pattern = {0, (seekBarVibrate.getProgress() * 10), (progress * 10)};
                if (v.hasVibrator()) {
                    v.vibrate(pattern, 0);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Erreur, votre Android ne dispose pas de fonctions vibratoires !")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // START THE GAME!
                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.create();
                    builder.show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // called when the user first touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // called after the user finishes moving the SeekBar
            }
        });
        seekBarVibrate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // updated continuously as the user slides the thumb
                seekBarVibrateText.setText("Réglage du temps de vibration : " + (progress * 10) + "ms");
                long[] pattern = {0, (progress * 10), (seekBarPause.getProgress()  * 10)};
                if (v.hasVibrator()) {
                    v.vibrate(pattern, 0);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Erreur, votre Android ne dispose pas de fonctions vibratoires !")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // START THE GAME!
                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.create();
                    builder.show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // called when the user first touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // called after the user finishes moving the SeekBar
            }
        });

        return view;
    }
}