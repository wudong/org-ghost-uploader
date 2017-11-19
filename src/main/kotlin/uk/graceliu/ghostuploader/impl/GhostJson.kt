package uk.graceliu.ghostuploader.impl

import com.google.api.client.json.GenericJson
import com.google.api.client.json.JsonString

// the default parameters is provided to enable
// parameterless constructor, which is required by Jackson parsing.
class GhostToken : GenericJson() {

    @com.google.api.client.util.Key
    var access_token: String = ""

    @com.google.api.client.util.Key
    var refresh_token: String= ""

    @com.google.api.client.util.Key
    var expires_in: Int = 0

    @com.google.api.client.util.Key
    var token_type: String= ""
}


class GhostPosts : GenericJson() {
    @com.google.api.client.util.Key
    var posts: List<GhostPost> = listOf()
}

class GhostPost : GenericJson() {
    @com.google.api.client.util.Key
    var id: String? = null
    @com.google.api.client.util.Key
    var uuid: String? = null
    @com.google.api.client.util.Key
    var title: String = ""
    @com.google.api.client.util.Key
    var plaintext: String = ""
    @com.google.api.client.util.Key
    var slug: String? = null
    @com.google.api.client.util.Key
    var status: String = "draft"
    @com.google.api.client.util.Key
    var mobiledoc: String? = null
}

class GhostSlugs : GenericJson() {
    @com.google.api.client.util.Key
    var slugs: List<GhostSlug> = listOf()
}

class GhostSlug : GenericJson() {
    @com.google.api.client.util.Key
    var slug: String = ""
}

class GhostPostsResponse: GenericJson() {

    @com.google.api.client.util.Key
    var posts: List<GhostPost> = listOf()
}

data class GhostLoginData(val client_id: String, val client_secret: String, val grant_type: String, val username: String,
                          val password: String)


class MobileDoc : GenericJson (){
    @com.google.api.client.util.Key
    var version: String = "0.3.1"
    @com.google.api.client.util.Key
    var markups: List<String> = listOf()
    @com.google.api.client.util.Key
    var atoms: List<String> = listOf()
    @com.google.api.client.util.Key
    var sections: List<List<Int>> = listOf(listOf(10,0))
    @com.google.api.client.util.Key
    var cards: List<List<Any>> = listOf()

    companion object {
        fun generateMobileDoc(markdownString: String) : MobileDoc{
            val mobileDoc = MobileDoc()
            val card = MobileDocCard()
            card.markdown = markdownString
            mobileDoc.cards = listOf(listOf("card-markdown", card))
            return mobileDoc
        }
    }
}

class MobileDocCard: GenericJson () {
    @com.google.api.client.util.Key
    var cardName: String="card-markdown"
    @com.google.api.client.util.Key
    var markdown: String=""
}




