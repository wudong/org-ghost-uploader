package uk.graceliu.ghostuploader

import uk.graceliu.ghostuploader.impl.UploaderImpl
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    //val (baseUrl, userName, password, fileName) = Configuration.getConfigurationFromArgs(args)

    val fileName = "testdata/backtracking.org"
    val uploaderImpl = UploaderImpl()
    val successful = uploaderImpl.upload(Paths.get(fileName))

    if (successful) exitProcess(0) else exitProcess(1)
}