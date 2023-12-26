package com.anushka.roomdemo

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class VideoDetailsFragment : Fragment() {

    interface VideoPlaybackListener {
        fun onVideoPlaybackStarted()
        fun onVideoPlaybackComplete()
    }

    private var videoId: Long = 0
    private var exoPlayer: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var isPlayerInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoId = it.getLong(ARG_VIDEO_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerView = view.findViewById(R.id.exoPlayerView)
        if (!isPlayerInitialized) {
            initializePlayer()
            isPlayerInitialized = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as? VideoPlaybackListener)?.onVideoPlaybackStarted()
    }

    override fun onDetach() {
        super.onDetach()
        releasePlayer()
        (context as? VideoPlaybackListener)?.onVideoPlaybackComplete()
    }

    private fun initializePlayer() {
        if (exoPlayer == null) {
            exoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        }

        playerView.player = exoPlayer

        val videoDao = MyApp.getDatabase(requireActivity().application).videoDao()
        lifecycleScope.launchWhenCreated {
            val video = videoDao.getVideoById(videoId)
            video?.let {
                val mediaItem = MediaItem.fromUri(Uri.parse(it.path))
                exoPlayer?.setMediaItem(mediaItem)
                exoPlayer?.prepare()
                exoPlayer?.play()
            }
        }
    }

    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    companion object {
        private const val ARG_VIDEO_ID = "videoId"

        fun newInstance(videoId: Long): VideoDetailsFragment {
            val fragment = VideoDetailsFragment()
            val args = Bundle()
            args.putLong(ARG_VIDEO_ID, videoId)
            fragment.arguments = args
            return fragment
        }
    }
}
