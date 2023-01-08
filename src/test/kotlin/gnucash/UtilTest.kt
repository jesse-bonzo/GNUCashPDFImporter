package gnucash

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilTest {
    @Test
    fun testToUUID() {
        val guid = "f64cd6a3298d4f3ea44f06083c13c548"
        val uuid = guid.toUUID()
        assertEquals(guid, uuid.toGuid())
        assertEquals(uuid, uuid.toGuid().toUUID())
        assertEquals(guid, uuid.toGuid().toUUID().toGuid())
        assertEquals(uuid, uuid.toGuid().toUUID().toGuid().toUUID())
    }
}