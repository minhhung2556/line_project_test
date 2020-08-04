package vn.com.linetechnology

import org.junit.Test

import org.junit.Assert.*
import vn.com.linetechnology.data.SampleMovie
import vn.com.linetechnology.data.sampleJsonData

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun parseJsonIsCorrect() {
        val m = SampleMovie.fromJson(sampleJsonData)
        assertNotNull(m)
        assertNotNull(m?.title)
        assertNotEquals(m?.image?.size ?: 0, 0)
        assertNotNull(m?.image?.get(0))
    }
}
