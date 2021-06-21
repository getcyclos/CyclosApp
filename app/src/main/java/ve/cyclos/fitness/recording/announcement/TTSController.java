/*
 * Copyright (c) 2020 Gabriel Estrada <dev@getcyclos.com>
 *
 * This file is part of CyclosApp
 *
 * CyclosApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CyclosApp is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ve.cyclos.fitness.recording.announcement;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import ve.cyclos.fitness.recording.WorkoutRecorder;
import ve.cyclos.fitness.recording.event.TTSReadyEvent;

public class TTSController {

    private final TextToSpeech textToSpeech;
    private boolean ttsAvailable;

    private final AnnouncementMode currentMode;

    private final AudioManager audioManager;

    public TTSController(Context context) {
        this.textToSpeech = new TextToSpeech(context, this::ttsReady);
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.currentMode = AnnouncementMode.getCurrentMode(context);
    }

    private void ttsReady(int status) {
        ttsAvailable = status == TextToSpeech.SUCCESS && textToSpeech.setLanguage(Locale.getDefault()) >= 0;
        if (ttsAvailable) {
            textToSpeech.setOnUtteranceProgressListener(new TextToSpeechListener());
        }
        EventBus.getDefault().post(new TTSReadyEvent(ttsAvailable));
    }

    public void speak(WorkoutRecorder recorder, Announcement announcement) {
        if (!announcement.isAnnouncementEnabled()) {
            return;
        }
        String text = announcement.getSpokenText(recorder);
        if (!text.equals("")) {
            speak(text);
        }
    }

    private int speakId = 1;

    public void speak(String text) {
        if (!ttsAvailable) {
            // Cannot speak
            return;
        }
        if (currentMode == AnnouncementMode.HEADPHONES && !isHeadsetOn()) {
            // Not allowed to speak
            return;
        }
        Log.d("Recorder", "TTS speaks: " + text);
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, "announcement" + (++speakId));
    }

    private boolean isHeadsetOn() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean bluetoothHeadsetConnected = mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;

        return audioManager.isWiredHeadsetOn() || bluetoothHeadsetConnected;
    }

    public void destroy() {
        textToSpeech.shutdown();
    }

    public boolean isTtsAvailable() {
        return ttsAvailable;
    }

    private class TextToSpeechListener extends UtteranceProgressListener {

        @Override
        public void onStart(String utteranceId) {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_SYSTEM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        }

        @Override
        public void onDone(String utteranceId) {
            audioManager.abandonAudioFocus(null);
        }

        @Override
        public void onError(String utteranceId) {
        }
    }
}
