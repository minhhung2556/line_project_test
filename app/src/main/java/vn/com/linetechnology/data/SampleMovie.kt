package vn.com.linetechnology.data

import org.json.JSONObject

data class SampleMovie(val title: String?, val image: List<String>?) {
    companion object{
        fun fromJson(jsonStr: String): SampleMovie? {
            return try {
                val jo = JSONObject(jsonStr)
                val title = jo.getString("title")
                val image = jo.get("image") as Iterable<*>
                SampleMovie(title, image.map { it.toString() })
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}