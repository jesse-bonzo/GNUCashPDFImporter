import java.nio.file.Files
import java.util.zip.GZIPOutputStream
import kotlin.io.path.deleteIfExists
import kotlin.io.path.outputStream
import kotlin.test.Test
import kotlin.test.assertTrue

class IsGzippedTest {
    @Test
    fun testIsGzipped() {
        val tempFile = Files.createTempFile(null, null)
        try {
            tempFile.outputStream().use { outputStream ->
                GZIPOutputStream(outputStream).use { gzipOutput ->
                    gzipOutput.bufferedWriter().write("Hello World!")
                }
            }
            assertTrue(isGzipped(tempFile.toFile()))
        } finally {
            tempFile.deleteIfExists()
        }
    }
}