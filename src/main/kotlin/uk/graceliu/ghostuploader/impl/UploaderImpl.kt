package uk.graceliu.ghostuploader.impl

import mu.KotlinLogging
import uk.graceliu.ghostuploader.*
import java.nio.file.Path
import java.util.stream.Stream

class UploaderImpl(conf: Configuration) : Uploader {

    private val logger = KotlinLogging.logger {}
    private val ghost : GhostInterface = GhostImpl(conf)
    private val parser : MetaDataParser = OrgMetadataParserImpl()
    private val postProvider: PostProvider = LocalOrgMDFileImpl(parser)

    override fun upload(postPath: Path) : Boolean {
        val theFile = postPath.toFile()
        return if (theFile.exists()){
            val stream =
                when {
                    theFile.isDirectory -> uploadDir(postPath)
                    theFile.isFile -> uploadFile(postPath)
                    else -> Stream.empty()
                }
            stream.map{ x-> processUpload(x, postPath) }.anyMatch{ x->!x }
        } else {
            logger.debug { "Specified file does not exist: ${theFile.absolutePath} " }
            false
        }
    }

    private fun processUpload(post: Post, postPath: Path) : Boolean {
        val toUpdated = if (post.meta.id === null) {
            val created = ghost.createPost(post)
            logger.debug { "Meta info created for new post: ${created.meta}" }
            post.copy(meta = created.meta)
        } else {
            post
        }

        ghost.updatePost(toUpdated).also {
            logger.debug { "Update ghost successful: $it" }
        }

        parser.update(post.meta, postPath)
        logger.debug { "Updated ${postPath.toFile().absolutePath} with meta ${post.meta}" }

       return true
    }

    private fun uploadFile(file: Path) : Stream<Post> {
        val post = postProvider.post(file)
        return if (post!==null) Stream.of(post) else Stream.empty()
    }

    private fun uploadDir(dir: Path) : Stream<Post> = postProvider.postStream(dir)

}