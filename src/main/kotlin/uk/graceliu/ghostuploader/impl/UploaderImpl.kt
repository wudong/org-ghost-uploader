package uk.graceliu.ghostuploader.impl

import uk.graceliu.ghostuploader.*
import java.io.File
import java.io.FileReader
import java.nio.file.Path

class UploaderImpl : Uploader {

    val ghost : GhostInterface = GhostImpl(baseUrl = "", clientSecret = "")
    val parser : MetaDataParser = OrgMetadataParserImpl()
    val postProvider: PostProvider = LocalOrgMDFileImpl(parser)


    override fun upload(postPath: Path) {
        val parsePost = postProvider.post(postPath)

        parsePost?.let {
            val postToUpdate = if (it.meta.id===null) ghost.createPost(it) else it
            ghost.updatePost(postToUpdate)
        }.also {
            parser.update(it!!.meta, postPath)
        }

    }
}