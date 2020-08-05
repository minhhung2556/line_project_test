package vn.com.linetechnology

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import vn.com.linetechnology.data.SampleMovie
import vn.com.linetechnology.data.sampleJsonData
import vn.com.linetechnology.image.ImageLoader

data class MainActivityState(
    val images: ArrayList<String>?,
    val currentImage: Int? = 0,
    val model: SampleMovie?
)

class MainActivity : AppCompatActivity() {
    private val imageLoader = ImageLoader()
    var state: MainActivityState = initState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        update(initState())
        loadData()
    }

    private fun initState(): MainActivityState {
        return MainActivityState(
            null, null, null
        )
    }

    private fun update(state: MainActivityState) {
        this.state = state
        updateUI()
    }

    private fun updateUI() {
        tv_movie_title.text = state.model?.title ?: ""
    }

    private fun loadData() {
        val model = SampleMovie.fromJson(sampleJsonData)
        val list = ArrayList<String>()
        model?.image?.forEach { list += it }
        update(this.state.copy(images = list, currentImage = 0, model = model))
        loadImages()
    }

    private fun loadImages() {
        if (this.state.images?.isNotEmpty() != true) return

        tv_progress.visibility = View.VISIBLE
        val lastCallBack = { _: Bitmap? ->
            onDoneLoadingImages()
        }
        val len = this.state.images!!.size
        var img: String
        for (i in 0 until len) {
            img = this.state.images!![i]
            imageLoader.load(baseContext, img, { percent: Int, byteLen: Int ->
                tv_progress.text = "$percent - $byteLen"
            }, if (i == len - 1) lastCallBack else { bm: Bitmap? -> })
        }
    }

    private fun onDoneLoadingImages() {
        tv_progress.visibility = View.GONE
        setImage(null,this.state.images!![this.state.currentImage!!])
    }

    private fun changeImage() {
        if (this.state.images?.isNotEmpty() != true) return

        val images = this.state.images
        var index: Int = this.state.currentImage ?: 0
        if (this.state.currentImage!! < images!!.size - 1)
            index++
        else index = 0
        val img = images[index]
        val prevImg = images[this.state.currentImage!!]

        update(this.state.copy(currentImage = index))
        setImage(prevImg,img)
    }

    private fun setImage(prevImg: String?, img: String) {
        if(prevImg!=null) imageLoader.removeCache(prevImg)

        im_movie_image.setImageResource(R.color.colorLoadingGrey)

        imageLoader.load(baseContext, img, { _: Int, _: Int ->
        }, { bitmap: Bitmap? ->
            im_movie_image.setImageBitmap(bitmap)
        })
    }

    fun onClickOnImage(view: View) {
        changeImage()
    }
}
