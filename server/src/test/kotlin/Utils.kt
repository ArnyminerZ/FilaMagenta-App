import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses
import org.junit.Assert

fun expectToBlock(thread: Thread, waitCount: Long, waitUnits: TimeUnit) {
    val start = System.currentTimeMillis()
    while (System.currentTimeMillis() - start < waitUnits.toMillis(waitCount)) {
        if (thread.state == Thread.State.WAITING) {
            return
        }
        Thread.sleep(50)
    }
    Assert.fail("Timed out while waiting for thread to block")
}

/**
 * Retrieves all the members of a given class that are of a specified nested class type.
 *
 * @param kClass The class for which to retrieve the members.
 * @param childrenTypeName The name of the nested class type.
 * @param list The set of classes to consider during the retrieval, defaulting to an empty set.
 *
 * @return A set of classes that are members of the given class and match the specified nested class type.
 */
fun getMembersOf(
    kClass: KClass<*>,
    childrenTypeName: String,
    list: Set<KClass<*>> = emptySet()
): Set<KClass<*>> {
    val mutable = list.toMutableSet()
    val classes = kClass.nestedClasses
    for (sub in classes) {
        if (sub.superclasses.find { it.simpleName == childrenTypeName } != null) {
            mutable.add(sub)
        }
        if (sub.nestedClasses.isNotEmpty()) {
            getMembersOf(sub, childrenTypeName, mutable).also { mutable.addAll(it) }
        }
    }
    return mutable
}
