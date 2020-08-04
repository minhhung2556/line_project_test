package vn.com.linetechnology

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import vn.com.linetechnology.data.SampleMovie
import vn.com.linetechnology.data.sampleJsonData

class MainActivity : AppCompatActivity() {
    var model: SampleMovie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()
        setData()
    }

    private fun setData() {
        tv_movie_title.text = model?.title ?: ""
    }

    private fun loadData() {
        model = SampleMovie.fromJson(sampleJsonData)
        loadImage()
    }

    private fun loadImage() {
    }
}
