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
    val imageLoader = ImageLoader()
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
        //TODO
    }

    private fun loadData() {
        val model = SampleMovie.fromJson(sampleJsonData)
        val list = ArrayList<String>()
        model?.image?.forEach { list += it }
        update(this.state.copy(images = list, currentImage = 0, model = model))
        loadImage()
    }

    private fun loadImage() {
        this.state.images?.forEach { img ->
            setImage(img)
        }
    }

    private fun changeImage() {
        if (this.state.images?.isNotEmpty() == true){
            val images = this.state.images
            var index:Int = this.state.currentImage?:0
            if (this.state.currentImage!! < images!!.size - 1)
                index++
            else index = 0
            val img = images[index]

            setImage(img)
        }
    }

    fun setImage(img: String){
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
