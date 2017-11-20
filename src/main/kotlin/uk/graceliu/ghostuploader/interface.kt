package uk.graceliu.ghostuploader

import java.io.File
import java.io.Reader
import java.io.StringReader
import java.nio.file.Path
import java.util.stream.Stream

interface GhostInterface {
    fun login(user: String, password: String)
    fun getPosts(): Iterable<Post>
    fun createPost(post: Post) : Post
    fun updatePost(post: Post): Post
    fun deletePost(post: Post): Boolean
}

interface PostProvider {
    fun postStream(path: Path) : Stream<Post>
    fun post(path: Path): Post?
}

interface MetaDataParser {
    fun parse(contentString: String): PostMetaData? = parse(StringReader(contentString))
    fun parse(contentReader: Reader): PostMetaData?
    fun update(meta: PostMetaData, file: Path)
}

interface Uploader {
    fun upload(file: Path) : Boolean
}