package vn.com.linetechnology.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import android.view.WindowManager
import java.io.File
import java.lang.ref.WeakReference


class ImageDecoder(
    context: Context,
    private val onCompleted: onDecodeBitmapCompleted
) :
    AsyncTask<File, Int, Bitmap>() {
    private var mContext: WeakReference<Context> = WeakReference(context)

    override fun doInBackground(vararg params: File?): Bitmap? {
        try {
            val input = params[0]!!
            val wm =
                mContext.get()!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val bm = getBitmap(input, display.width, display.height)
            return bm
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        this.onCompleted(result)
    }
}

fun getBitmap(file: File, screenW: Int, screenH: Int): Bitmap? {
    return try {
        val opts = BitmapFactory.Options()
//        opts.outWidth = screenW
        opts.outHeight = screenH
        BitmapFactory.decodeFile(file.path,opts)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}