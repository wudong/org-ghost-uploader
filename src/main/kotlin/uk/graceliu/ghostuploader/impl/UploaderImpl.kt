package uk.graceliu.ghostuploader.impl

import mu.KotlinLogging
import uk.graceliu.ghostuploader.*
import java.nio.file.Path

class UploaderImpl(conf: Configuration) : Uploader {

    private val logger = KotlinLogging.logger {}

    val ghost : GhostInterface = GhostImpl(conf)
    val parser : MetaDataParser = OrgMetadataParserImpl()
    val postProvider: PostProvider = LocalOrgMDFileImpl(parser)

    override fun upload(postPath: Path) : Boolean {
        logger.debug { "Uploading post: ${postPath.toFile().absolutePath}" }
        val parsePost = postProvider.post(postPath)
        if (parsePost==null) {
            logger.debug { "${postPath.toFile().absolutePath} cannot be convert to MD file" }
            return false
        } else {
            val post = parsePost.let {
                val toUpdated = if (it.meta.id === null) {
                    val created = ghost.createPost(it)
                    logger.debug { "Meta info created for new post: ${created.meta}" }
                    it.copy(meta = created.meta)
                } else {
                    it
                }

                ghost.updatePost(toUpdated).also {
                    logger.debug { "Update ghost successful: $it" }
                }
            }

            if (post !== null) {
                parser.update(post.meta, postPath)
                logger.debug { "Updated ${postPath.toFile().absolutePath} with meta ${post.meta}" }
            } else {
                logger.debug { "Uploading ${postPath.toFile().absolutePath} with meta ${post.meta} failed" }
            }
            return post!==null
        }
    }
}