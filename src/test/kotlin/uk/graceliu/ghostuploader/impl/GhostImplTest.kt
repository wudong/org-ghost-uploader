package uk.graceliu.ghostuploader.impl

import org.junit.Test
import uk.graceliu.ghostuploader.Post
import uk.graceliu.ghostuploader.PostMetaData

class GhostImplTest {
    @Test
    fun login() {
        val ghostImpl = GhostImpl("", "", "")
        val retrieveToken = ghostImpl.retrieveToken(ghostImpl.loginUrl,
                "wudong.liu@gmail.com", "yuepan2008")
        println(retrieveToken)
    }

    @Test
    fun getPosts() {
        val ghostImpl = GhostImpl("", "", "")
        ghostImpl.login("wudong.liu@gmail.com", "yuepan2008")
        val posts = ghostImpl.getPosts()
        posts.forEach(System.out::println)
    }

    @Test
    fun createPost() {
        val ghostImpl = GhostImpl("", "", "")
        ghostImpl.login("wudong.liu@gmail.com", "yuepan2008")
        val createdPost = ghostImpl.createPost(Post(PostMetaData("Some title", "", listOf(),
                null, null, null, null), "Some content"))
        println(createdPost)
    }

    @Test
    fun getPostSlug(){
        val ghostImpl = GhostImpl("", "", "")
        ghostImpl.login("wudong.liu@gmail.com", "yuepan2008")

        val postSlug = ghostImpl.getPostSlug("someti tle tobe")
        println(postSlug)
    }

    @Test
    fun updatePost() {
        val ghostImpl = GhostImpl("", "", "")
        ghostImpl.login("wudong.liu@gmail.com", "yuepan2008")
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