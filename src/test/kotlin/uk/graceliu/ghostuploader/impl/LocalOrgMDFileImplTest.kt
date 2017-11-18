package uk.graceliu.ghostuploader.impl

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import org.hamcrest.Matchers.*
import java.nio.file.Paths

class LocalOrgMDFileImplTest {

    @Test
    fun `convert orgfile name to md file name correctoy`() {
        val mdFileFromOrgFile = getMDFileFromOrgFile(File("/abc/edc/org.org"))
        assertThat(mdFileFromOrgFile.absolutePath, equalTo("/abc/edc/org.md"))
    }

    @Test
    fun `file stream works properly`(){
        LocalOrgMDFileImpl().
                fileStream(File("."), ".kt" )
                .forEach(System.out::println)
    }

    @Test
    fun `metadata stream works properly`(){
        val count = LocalOrgMDFileImpl()
                .metaDataStream(Paths.get(".", "testdata"))
                .peek(System.out::println)
                .count()
        assertThat(count.toInt(), equalTo(2))
    }

    @Test
    fun `post stream should filter out the org files that without a md file`(){
        val count = LocalOrgMDFileImpl()
                .postStream(Paths.get(".", "testdata"))
                .peek(System.out::println)
                .count()
        assertThat(count.toInt(), equalTo(1))
    }


    @Test
    fun `post stream should retrieve the content of the corresponding md file`(){
        val content = """
            -   Backtracking incrementally builds candidates to the solutions, and
            """.trimIndent()
        val post = LocalOrgMDFileImpl()
                .postStream(Paths.get(".", "testdata"))
              //  .peek(System.out::println)
                .findFirst().get()
        assertThat(post.meta.title, equalTo("Backtracking"))
        assertThat(post.content, startsWith(content))
    }

    @Test
    fun `get single post from the given path` (){
        val content = """
            -   Backtracking incrementally builds candidates to the solutions, and
            """.trimIndent()

        val post = LocalOrgMDFileImpl()
                .post(Paths.get(".", "testdata", "backtracking.org"))
        assertThat(post!!.meta.title, equalTo("Backtracking"))
        assertThat(post!!.content, startsWith(content))
    }

}