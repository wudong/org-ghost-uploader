package uk.graceliu.ghostuploader.impl

import uk.graceliu.ghostuploader.GhostInterface

import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.UrlEncodedContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.http.json.JsonHttpContent

import com.google.api.client.json.JsonFactory
import com.google.api.client.json.JsonObjectParser
import com.google.api.client.json.jackson.JacksonFactory
import uk.graceliu.ghostuploader.GhostConfig
import uk.graceliu.ghostuploader.Post
import uk.graceliu.ghostuploader.PostMetaData
import java.net.URLEncoder


class GhostImpl constructor(ghostConfig: GhostConfig) : GhostInterface {

    //private val logger = KotlinLogging.logger {}

    val loginUrl = "${ghostConfig.baseUrl}/authentication/token"
    val postUrl = "${ghostConfig.baseUrl}/posts/"
    val postSlugUrl = "${ghostConfig.baseUrl}/slugs/post"

    val client_id= ghostConfig.clientId
    val client_secret = ghostConfig.clientSecret
    val grant_type = ghostConfig.grantType
    val username = ghostConfig.userName
    val password = ghostConfig.password

    //token will be filled when login
    var token : GhostToken? = null

    companion object {
        val HTTP_TRANSPORT: HttpTransport = NetHttpTransport()
        val JSON_FACTORY: JsonFactory = JacksonFactory()
        val REQUEST_FACTORY = HTTP_TRANSPORT.createRequestFactory({
                request-> request.setParser(JsonObjectParser(JSON_FACTORY))
        })
    }

    override fun login(user: String, password: String) {
        this.token = retrieveToken(loginUrl, user, password)
    }

    fun login() {
        login(username, password)
    }

    fun retrieveToken(loginUrl: String, user: String, password: String): GhostToken {
        val formContent = UrlEncodedContent(GhostLoginData(client_id, client_secret, grant_type, user, password).toMap())
        val request = REQUEST_FACTORY.buildPostRequest(GenericUrl(loginUrl), formContent)
        val response = request.execute()
        return response.parseAs(GhostToken::class.java)
    }

    fun <T> checkLogin(token: GhostToken?, block: ()->T): T {
        return if (token!==null){
            block()
        }else{
            login()
            block()
        }
    }

    override fun getPosts(): Iterable<Post> {
        return checkLogin(this.token) {
            val url = GenericUrl(postUrl)
            url.put("formats", "plaintext")

            val buildGetRequest = REQUEST_FACTORY.buildGetRequest(url)
            buildGetRequest.headers.authorization = token!!.toBearAuthorizationString()

            val response = buildGetRequest.execute()

            val mappedPosts: Iterable<Post> = response.parseAs(GhostPostsResponse::class.java)
                    .posts.map { x->x.toPost()}
            mappedPosts
        }
    }

    fun getPostSlug(postTitle: String): String {
        return checkLogin(this.token) {
            val encode = URLEncoder.encode(postTitle)
            val url = GenericUrl("${postSlugUrl}/${encode}/")

            val buildGetRequest = REQUEST_FACTORY.buildGetRequest(url)
            buildGetRequest.headers.authorization = token!!.toBearAuthorizationString()

            val response = buildGetRequest.execute()
            val slugs = response.parseAs(GhostSlugs::class.java)
            slugs.slugs[0].slug
        }
    }

    override fun createPost(post: Post): Post {
        return checkLogin(this.token) {

            if (post.meta.slug===null){
                post.meta.slug = getPostSlug(post.meta.title)
            }
            val posts = GhostPosts()

            posts.posts = listOf(post.toGhostPost())

            val createdPost = postOrPut(postUrl) { url ->
                REQUEST_FACTORY.buildPostRequest(url, JsonHttpContent(JSON_FACTORY,
                        posts))
            }

            updateFileName(createdPost, post)
        }
    }

    override fun updatePost(post: Post): Post {
        return checkLogin(this.token) {

            val posts = GhostPosts()
            posts.posts = listOf(post.toGhostPost())

            val updatedPost = postOrPut("${this.postUrl}${post.meta.id}/") { url ->
                REQUEST_FACTORY.buildPutRequest(url, JsonHttpContent(JSON_FACTORY,
                        posts))
            }
            updateFileName(updatedPost, post)
        }
    }

    override fun deletePost(post: Post): Boolean {
        return checkLogin(this.token) {
            val response = REQUEST_FACTORY.buildDeleteRequest(GenericUrl(postUrl)).execute()
            response.parseAsString()=="OK"
        }
    }

    private fun updateFileName(updatePost: Post, originalPost: Post): Post{
        updatePost.meta.fileName = originalPost.meta.fileName
        return updatePost
    }

    private fun postOrPut(postUrl: String, requestProvider: (url: GenericUrl)->HttpRequest): Post {
        val url = GenericUrl(postUrl)
        //url.put("formats", "plaintext")
        val request = requestProvider(url)
        request.headers.authorization = token!!.toBearAuthorizationString()
        val response = request.execute()

        //TODO obviously the parse can fail and the ghostPost will be null
        val ghostPost = response.parseAs(GhostPosts::class.java)
        return ghostPost.posts[0].toPost()
    }

    private fun Post.toGhostPost(): GhostPost {
        val gp = GhostPost()

        gp.slug = this.meta.slug
        gp.title=this.meta.title
        gp.id = this.meta.id
        gp.mobiledoc =  JSON_FACTORY.toString(MobileDoc.generateMobileDoc(this.content))

        return gp
    }


    private fun GhostPost.toPost(): Post = Post(PostMetaData(this.title, null, listOf(),
            null, null, this.id, this.slug), this.plaintext)

    private fun GhostToken.toBearAuthorizationString(): String {
        return "${this.token_type} ${this.access_token}"
    }

    private fun GhostLoginData.toMap() = hashMapOf(
            "client_id" to this.client_id,
            "client_secret" to this.client_secret,
            "username" to this.username,
            "password" to this.password,
            "grant_type" to this.grant_type
    )


}


