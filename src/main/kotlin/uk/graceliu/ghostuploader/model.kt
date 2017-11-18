package uk.graceliu.ghostuploader

import java.time.Instant

data class Post(val meta: PostMetaData, val content: String)

data class User(val userId: String, val userName: String, val token: String)

data class PostMetaData(val title: String,
                        val category: String?,val tags: List<String>,
                        val date: Instant?, var fileName: String?,
                        var id: String?, var slug: String?)