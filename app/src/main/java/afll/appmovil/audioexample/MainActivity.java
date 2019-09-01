package afll.appmovil.audioexample;

//import android.media.AudioFormat;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import android.util.Log;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.SeekBar;
import android.widget.TextView;


import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.resample.RateTransposer;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd.Parameters;




public class MainActivity extends AppCompatActivity {


    TextView noteText;
    TextView pitchText;
    SeekBar pitchShifter;
    TextView barProgress;

    //ESTAS VARIABLES SON CON LAS QUE TRABAJA EL PITCH SHIFTER EN JAVA. VER EJEMPLO EN TARSOS.DSP
/*  private double currentFactor=1;
    private double sampleRate;
    private WaveformSimilarityBasedOverlapAdd wsola;
    private RateTransposer rateTransposer;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check for permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat .requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
        }


        noteText = findViewById(R.id.noteText);
        pitchText = findViewById(R.id.pitchText);
        pitchShifter = findViewById(R.id.pitchShifter);
        barProgress = findViewById(R.id.barValue);

        //ESTE METODO ESCUCHA LOS CAMBIOS QUE SE HAGAN EN LA SEEKBAR Y MODIFICA UN TEXTVIEW POR AHORA
        pitchShifter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                barProgress.setText(i+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        TarsosDSPAudioFormat myFormat = new TarsosDSPAudioFormat(22050,16,1,true,true);

        AndroidAudioPlayer myPlayer = new AndroidAudioPlayer(myFormat,22050,AudioManager.STREAM_MUSIC);

        //ESTO TAMBIEN VIENE DEL EJEMPLO DE PITCH SHIFTER DE TARSOS.DSP
        //sampleRate=myFormat.getSampleRate();
        //rateTransposer = new RateTransposer(currentFactor);
        //wsola = new WaveformSimilarityBasedOverlapAdd(Parameters.musicDefaults(currentFactor, sampleRate));


        //ESTE METODO ES PARA MOSTRAR LA FRECUENCIA
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processPitch(pitchInHz);
                    }
                });
            }
        };

        //wsola.setDispatcher(dispatcher);
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        //dispatcher.addAudioProcessor(wsola);
        //dispatcher.addAudioProcessor(rateTransposer);
        dispatcher.addAudioProcessor(pitchProcessor);
        dispatcher.addAudioProcessor(myPlayer);



        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }

    public void processPitch(float pitchInHz) {
        pitchText.setText(pitchInHz+"Hz");

        if(pitchInHz >= 110 && pitchInHz < 123.47) {
            //A
            noteText.setText("A");
        }
        else if(pitchInHz >= 123.47 && pitchInHz < 130.81) {
            //B
            noteText.setText("B");
        }
        else if(pitchInHz >= 130.81 && pitchInHz < 146.83) {
            //C
            noteText.setText("C");
        }
        else if(pitchInHz >= 146.83 && pitchInHz < 164.81) {
            //D
            noteText.setText("D");
        }
        else if(pitchInHz >= 164.81 && pitchInHz <= 174.61) {
            //E
            noteText.setText("E");
        }
        else if(pitchInHz >= 174.61 && pitchInHz < 185) {
            //F
            noteText.setText("F");
        }
        else if(pitchInHz >= 185 && pitchInHz < 196) {
            //G
            noteText.setText("G");
        }
    }


}
