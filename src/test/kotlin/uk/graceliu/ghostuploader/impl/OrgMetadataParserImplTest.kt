package uk.graceliu.ghostuploader.impl

import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Test


class OrgMetadataParserImplTest {

    @Test
    fun `should parsing org file correctly`(){
        val content = """
            #+BLOG: graceliu
            #+POSTID: 124
            #+ORG2BLOG:
            #+DATE: [2017-08-10 Thu 10:47]
            #+CATEGORY: Programming
            #+TAGS: algorithms, theory
            #+DESCRIPTION:
            #+TITLE: Backtracking topic
            SOME OTHER CONTENT CONTINUE
            SOME OTHER CONTENT CONTINUE
            SOME OTHER CONTENT CONTINUE
            """.trimIndent()

        val parser = OrgMetadataParserImpl()
        val data = parser.parse(content)!!

        assertThat(data.id, equalTo("124"))
        assertThat(data.title, equalTo("Backtracking topic"))
        assertThat(data.category, equalTo("Programming"))
        assertThat(data.tags, equalTo(listOf("algorithms", "theory")))
        //assertThat(data.fileName, equalTo("someOrgFile.org"))
    }
}