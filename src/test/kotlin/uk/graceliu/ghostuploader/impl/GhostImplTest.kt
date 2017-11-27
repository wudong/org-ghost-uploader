package uk.graceliu.ghostuploader.impl

import com.google.api.client.util.Lists
import org.junit.Test
import uk.graceliu.ghostuploader.Configuration
import uk.graceliu.ghostuploader.Post
import uk.graceliu.ghostuploader.PostMetaData

class GhostImplTest {

    private val config = Configuration.getConfiguration(arrayOf())

    @Test
    fun login() {
        val ghostImpl = GhostImpl(config)
        val retrieveToken = ghostImpl.retrieveToken(ghostImpl.loginUrl,
                config.userName, config.userName)
        println(retrieveToken)
    }

    @Test
    fun getPosts() {
        val ghostImpl = GhostImpl(config)
        ghostImpl.login()
        val posts = ghostImpl.getPosts()
        posts.forEach(System.out::println)
    }

    @Test
    fun createPost() {
        val ghostImpl = GhostImpl(config)
        ghostImpl.login()
        val createdPost = ghostImpl.createPost(Post(PostMetaData("Some title", "", listOf(),
                null, null, null, null), "Some content"))
        println(createdPost)
    }

    @Test
    fun getPostSlug(){
        val ghostImpl = GhostImpl(config)
        ghostImpl.login()

        val postSlug = ghostImpl.getPostSlug("someti tle tobe")
        println(postSlug)
    }

    @Test
    fun updatePost() {
        val ghostImpl = GhostImpl(config)
        ghostImpl.login()
        val createdPost = ghostImpl.createPost(Post(PostMetaData("Wudong Test", "", listOf(),
                null, null, null, null), "Some content"))
        val copy = createdPost.copy(content = "# TITLE \n ## Subtitles \n\n What a great content")
        val updatePost = ghostImpl.updatePost(copy)
        println(updatePost)
    }

    @Test
    fun deletePost() {
    }

}