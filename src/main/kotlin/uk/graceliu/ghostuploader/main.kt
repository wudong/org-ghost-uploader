package uk.graceliu.ghostuploader

import uk.graceliu.ghostuploader.impl.UploaderImpl
import java.nio.file.Paths


fun main(args: Array<String>) {
    val conf = Configuration.getConfiguration(args)
    val uploaderImpl = UploaderImpl(conf)
    args.forEach { uploaderImpl.upload(Paths.get(it)) }
}