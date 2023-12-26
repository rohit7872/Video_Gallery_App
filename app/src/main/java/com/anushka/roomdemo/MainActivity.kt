package com.anushka.roomdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), VideoDetailsFragment.VideoPlaybackListener {

    private lateinit var videoAdapter: VideoItemAdapter
    private lateinit var recyclerView: RecyclerView

    // Initialize the database directly in the activity
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "video_database"
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        // Initialize the adapter with an empty list initially
        videoAdapter = VideoItemAdapter(emptyList(), this::onVideoItemClick)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = videoAdapter

        val uploadButton: Button = findViewById(R.id.uploadButton)
        uploadButton.setOnClickListener {
            openVideoPicker()
        }

        loadAndPlayVideo()
    }

    // Change the return type to List<VideoEntity> to List<VideoEntity>?
    private suspend fun getVideoList(): List<VideoEntity> {
        // Retrieve the list of videos from the database
        return database.videoDao().getAllVideos() ?: emptyList()
    }

    private fun onVideoItemClick(videoId: Long) {
        // Handle item click, open fragment with video details
        val fragment = VideoDetailsFragment.newInstance(videoId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openVideoPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    private fun loadAndPlayVideo() {
        lifecycleScope.launch {
            val videos = database.videoDao().getAllVideos()

            // Log the size of the video list
            Log.d("vidiodebug", "Video list size: ${videos?.size}")

            // Update the video list in the adapter
            videoAdapter.updateVideoList(videos ?: emptyList())
        }
    }

    // Callback from VideoDetailsFragment when video playback starts
    override fun onVideoPlaybackStarted() {
        // Update RecyclerView visibility when video playback starts
        recyclerView.visibility = View.GONE
        recyclerView.requestLayout()
    }

    // Callback from VideoDetailsFragment when video playback is complete
    override fun onVideoPlaybackComplete() {
        // Update RecyclerView visibility when video playback is complete
        recyclerView.visibility = View.VISIBLE
    }

    // Override onActivityResult to handle the result of video picking
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // Get the selected video's path and title (you might want to prompt the user for the title)
                val videoPath = uri.toString()
                val videoTitle = "Untitled Video"

                lifecycleScope.launch {
                    // Insert the selected video into the database
                    database.videoDao().insert(
                        VideoEntity(
                            title = videoTitle,
                            description = "Default Description",
                            path = videoPath
                        )
                    )

                    // Refresh the video list in the adapter
                    val updatedVideos = database.videoDao().getAllVideos()
                    videoAdapter.updateVideoList(updatedVideos)

                    // Log the size of the updated video list
                    Log.d("vidiodebug", "Updated Video list size: ${updatedVideos.size}")
                }
            }
        }
    }

    companion object {
        private const val PICK_VIDEO_REQUEST = 1
    }
}
