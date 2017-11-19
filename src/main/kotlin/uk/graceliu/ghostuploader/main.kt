package uk.graceliu.ghostuploader

import uk.graceliu.ghostuploader.impl.UploaderImpl
import java.nio.file.Paths

fun main(args: Array<String>) {
    //val (baseUrl, userName, password, fileName) = Configuration.getConfigurationFromArgs(args)

    val fileName = "testdata/backtracking.org"
    val uploaderImpl = UploaderImpl()
    val upload = uploaderImpl.upload(Paths.get(fileName))
    println(upload)

}