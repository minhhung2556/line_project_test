package vn.com.linetechnology.data

import org.json.JSONArray
import org.json.JSONObject

@Suppress("UNCHECKED_CAST")
operator fun <T> JSONArray.iterator(): Iterator<T>
        = (0 until length()).asSequence().map { get(it) as T }.iterator()

data class SampleMovie(val title: String?, val image: Iterator<String>?) {
    companion object{
        fun fromJson(jsonStr: String): SampleMovie? {
            return try {
                val jo = JSONObject(jsonStr)
                val title = jo.getString("title")
                val image = jo.getJSONArray("image").iterator<String>()
                SampleMovie(title, image)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}