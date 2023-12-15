package model

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.filamagenta.android.applicationContext
import org.junit.Before

abstract class AndroidTestEnvironment {
    /**
     * Provides the target context to be used.
     */
    protected val targetContext: Context by lazy {
        InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Before
    fun mockApplicationContext() {
        applicationContext = targetContext
    }
}
