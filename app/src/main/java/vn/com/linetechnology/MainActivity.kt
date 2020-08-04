package vn.com.linetechnology

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import vn.com.linetechnology.data.SampleMovie
import vn.com.linetechnology.data.sampleJsonData
import vn.com.linetechnology.image.ImageLoader
import java.util.*

class MainActivity : AppCompatActivity() {
    var model: SampleMovie? = null
    val imageLoader = ImageLoader()
    var currentImage: Int = 0

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
        model?.image?.forEach { img ->
            imageLoader.load(baseContext, img, { percent: Int, byteLen: Int ->
                tv_progress.text = "$percent - $byteLen"
            }, { bitmap: Bitmap? ->
                im_movie_image.setImageBitmap(bitmap)
            })
        }
    }

    private fun changeImage() {
        val list = model!!.image!!.asSequence().toList()
        if (currentImage < list.size - 1)
            currentImage++
        else currentImage = 0
        val img = list[currentImage]

        imageLoader.load(baseContext, img, { percent: Int, byteLen: Int ->
            tv_progress.text = "$percent - $byteLen"
        }, { bitmap: Bitmap? ->
            im_movie_image.setImageBitmap(bitmap)
        })
    }

    fun onClickOnImage(view: View) {
        changeImage()
    }
}
