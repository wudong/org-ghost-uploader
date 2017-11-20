package uk.graceliu.ghostuploader.impl

import mu.KotlinLogging
import uk.graceliu.ghostuploader.*
import java.nio.file.Path

class UploaderImpl : Uploader {

    private val logger = KotlinLogging.logger {}

    val ghost : GhostInterface = GhostImpl(baseUrl = "", clientSecret = "")
    val parser : MetaDataParser = OrgMetadataParserImpl()
    val postProvider: PostProvider = LocalOrgMDFileImpl(parser)

    override fun upload(postPath: Path) : Boolean {
        logger.info { "uploading post: ${postPath.toFile().absolutePath}" }
        val parsePost = postProvider.post(postPath)

        val post = parsePost?.let {
            val toUpdated = if(it.meta.id===null) {
                val created = ghost.createPost(it)
                logger.info { "Meta info created for new post: ${created.meta}" }
                it.copy(meta=created.meta)
            }else{ it }

            ghost.updatePost(toUpdated).also {
                logger.info { "Update ghost successful: $it" }
            }
        }

        if (post!==null){
            parser.update(post.meta, postPath)
            logger.info { "Updated ${postPath.toFile().absolutePath} with meta ${post.meta}" }
        }else {
            logger.info { "Uploading isn't successful" }
        }

        return post!==null
    }
}