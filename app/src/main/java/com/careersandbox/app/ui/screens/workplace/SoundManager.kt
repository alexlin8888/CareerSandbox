package com.careersandbox.app.ui.screens.workplace

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.careersandbox.app.R

/* =====================================================================
   SoundManager —— 沙盒音效/音樂
   場景 BGM(無縫循環) + UI 音效 + 靜音開關(記憶)。muted 為 Compose 可觀察狀態。
   音檔在 res/raw:bgm_warm/tense/night/neutral, sfx_tap/confirm/back/toggle/notify。
   ===================================================================== */
object SoundManager {

    var muted by mutableStateOf(false)
        private set

    private var bgm: MediaPlayer? = null
    private var currentBgm: Int = -1
    private var pool: SoundPool? = null
    private val sfxIds = HashMap<Int, Int>()
    private var prefs: SharedPreferences? = null
    private var initialized = false

    private val sfxList = listOf(
        R.raw.sfx_tap, R.raw.sfx_confirm, R.raw.sfx_back, R.raw.sfx_toggle, R.raw.sfx_notify,
    )

    fun init(context: Context) {
        if (initialized) return
        initialized = true
        val app = context.applicationContext
        prefs = app.getSharedPreferences("sandbox_audio", Context.MODE_PRIVATE)
        muted = prefs?.getBoolean("muted", false) ?: false
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        pool = SoundPool.Builder().setMaxStreams(4).setAudioAttributes(attrs).build()
        sfxList.forEach { res -> sfxIds[res] = pool!!.load(app, res, 1) }
    }

    /** 切換場景 BGM(同一首則不重啟) */
    fun playBgm(context: Context, resId: Int) {
        init(context)
        if (currentBgm == resId && bgm != null) return
        runCatching { bgm?.release() }
        currentBgm = resId
        bgm = MediaPlayer.create(context.applicationContext, resId)?.apply {
            isLooping = true
            setVolume(1.0f, 1.0f)
            if (!muted) start()
        }
    }

    fun stopBgm() {
        runCatching { bgm?.release() }
        bgm = null
        currentBgm = -1
    }

    /** 播 UI 音效(靜音時不播) */
    fun sfx(resId: Int) {
        if (muted) return
        val id = sfxIds[resId] ?: return
        pool?.play(id, 1f, 1f, 1, 0, 1f)
    }

    /** 切換靜音並記憶;解除靜音時恢復 BGM */
    fun toggleMute(context: Context) {
        init(context)
        muted = !muted
        prefs?.edit()?.putBoolean("muted", muted)?.apply()
        if (muted) {
            runCatching { bgm?.pause() }
        } else {
            runCatching { bgm?.start() }
            sfx(R.raw.sfx_toggle)
        }
    }
}
