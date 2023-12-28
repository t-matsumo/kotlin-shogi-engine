package infrastructure

import shogiengine.OutPutWriter
import java.io.PrintWriter

class OutPutWriterImpl(private val writer: PrintWriter): OutPutWriter {
    override fun println(message: String) {
        writer.println(message)
        writer.flush()
    }
}