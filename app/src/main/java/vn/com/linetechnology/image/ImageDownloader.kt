package vn.com.linetechnology.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.AsyncTask
import android.util.LruCache
import java.io.*
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class ImageLoader() {
    private lateinit var memoryCache: LruCache<String, Bitmap>

    init {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = maxMemory / 8

        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {

            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.byteCount / 1024
            }
        }
    }

    fun load(
        context: Context,
        url: String,
        onProgressUpdate: onDownloadProgressUpdate,
        onCompleted: onDecodeBitmapCompleted
    ) {
        println("ImageLoader.load: $url")
        val cachedBitmap = checkCachedBitmap(url)
        if (cachedBitmap == null) {
            ImageDownloader(context, { percent: Int, byteLen: Int ->
                println("ImageLoader.load: $url $percent% $byteLen")
                onProgressUpdate(percent, byteLen)
            }, { byteLen: Int, output: File? ->
                println("ImageLoader.load: $url $output $byteLen")
                ImageDecoder(context, onCompleted).execute(output)
            }).execute(url)
        } else {
            println("ImageLoader.load: $url from cache")
            onCompleted(cachedBitmap)
        }
    }

    private fun checkCachedBitmap(url: String): Bitmap? {
        return memoryCache.get(url)
    }

}

typealias onDownloadProgressUpdate = (percent: Int, byteLen: Int) -> Unit
typealias onDownloadCompleted = (byteLen: Int, output: File?) -> Unit
typealias onDecodeBitmapCompleted = (bitmap: Bitmap?) -> Unit

class ImageDownloader(
    context: Context,
    private val onDownloadProgressUpdate: onDownloadProgressUpdate,
    private val onDownloadCompleted: onDownloadCompleted
) :
    AsyncTask<String, Int, File>() {
    private var mContext: WeakReference<Context> = WeakReference(context)
    private var inputStreamLen = 0
    private var outStreamLen = 0
    private var output: File? = null

    override fun doInBackground(vararg params: String?): File? {
        try {
            val inputUrl = params[0]!!
            this.output = getCachedImageFileFromUrl(this.mContext.get()!!, inputUrl)
            downloadToFile(inputUrl, this.output)
            return this.output
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        this.onDownloadProgressUpdate(values[0] ?: 0, this.outStreamLen)
    }

    override fun onPostExecute(result: File?) {
        super.onPostExecute(result)
        this.onDownloadCompleted(this.outStreamLen, result)
    }

    private fun downloadToFile(input: String, output: File?) {
        return try {
            val url = URL(input)
            val connection: HttpURLConnection = url
                .openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            copyInputStreamToFile(connection.inputStream, output)
//            connection.disconnect()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun copyInputStreamToFile(inputStream: InputStream, file: File?) {
        var out: OutputStream? = null
        try {
            if (file != null) {
                if (file.exists()) {
                    file.delete()
                }
                file.createNewFile()
            }

            this.inputStreamLen = inputStream.available()
            out = FileOutputStream(file!!)
            val buf = ByteArray(1024)
            var len: Int
            this.outStreamLen = 0
            while (inputStream.read(buf).also { len = it } > 0) {
                this.outStreamLen = len
                out.write(buf, 0, len)
                //FIXME percent
                publishProgress((this.outStreamLen * 100.0 / this.inputStreamLen.toFloat()).toInt())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

fun getCachedImageFileFromUrl(context: Context, url: String): File? {
    return File(context.cacheDir, "imageCache/${url.hashCode()}.i")
}