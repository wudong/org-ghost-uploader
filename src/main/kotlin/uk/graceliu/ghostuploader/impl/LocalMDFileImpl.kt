package uk.graceliu.ghostuploader.impl

import sun.jvm.hotspot.oops.Metadata
import uk.graceliu.ghostuploader.Post
import uk.graceliu.ghostuploader.PostProvider
import uk.graceliu.ghostuploader.MetaDataParser
import uk.graceliu.ghostuploader.PostMetaData
import java.io.*
import java.nio.file.*
import java.time.Instant
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport

const val MD_POSTFIX: String = ".md"
const val ORG_POSTFIX: String = ".org"

fun getMDFileFromOrgFile(orgFile: File) : File {
    val mdFileName = orgFile.absolutePath.replace(ORG_POSTFIX, MD_POSTFIX)
    return File(mdFileName)
}

class LocalOrgMDFileImpl constructor(val parser: MetaDataParser ): PostProvider {

    private fun File.convertFileToMeta(): PostMetaData? =
        parser.parse(FileReader(this))?.copy(fileName = this.absolutePath)

    fun PostMetaData.retrieveContent(): String? {
        val mdFileFromOrgFile = getMDFileFromOrgFile(File(this.fileName))
        return if (mdFileFromOrgFile.exists() && mdFileFromOrgFile.isFile && mdFileFromOrgFile.canRead()) {
                String(Files.readAllBytes(mdFileFromOrgFile.toPath()))
            }else null
    }

    /**
     * Given the basedir, return A file stream which contains
     * all the files that ends with the ORG_POSTFIX.
     *
     */
    fun fileStream(baseDir: File, postFix: String): Stream<File> {
        val iterator = baseDir.walk().iterator();

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator,
                Spliterator.ORDERED), false)
                .filter {file -> file.absolutePath.toLowerCase().endsWith(postFix)}
                .filter {file->!file.isDirectory}
    }

    fun metaDataStream(basePath: Path): Stream<PostMetaData> {
        val fileStream = fileStream(basePath.toFile(), ORG_POSTFIX)
        return fileStream.map { file -> file.convertFileToMeta() }
                .filter{ t: PostMetaData? -> t!=null }.map { t: PostMetaData? -> t!! }
    }

    override fun postStream(basePath: Path) : Stream<Post> {
        assert(basePath.toFile().isDirectory, {"The basepath provided for poststream must be a directory"})
        return this.metaDataStream(basePath)
                .map { m -> Pair(m, m.retrieveContent())}
                .filter { (_, content) -> content!==null }
                .map { (meta, content) -> Post(meta, content!!) }
    }

    override fun post(path: Path): Post? {
        assert(path.toFile().isFile , {"The basepath provided for post must be a file"})
        assert(path.toFile().extension.toLowerCase() == "org" , {"The basepath provided for post must be an org file"})

        val m = path.toFile().convertFileToMeta()
        return if (m!==null){
            val content = m.retrieveContent()
            if (content!==null) Post(m, content) else null
        }else{null}
    }
}

class OrgMetadataParserImpl : MetaDataParser {

    companion object {
        const val ORGMODE_META_PREFIX: String="#+"
        const val ORGMODE_META_SEPARATOR: String=":"
        const val ORGMODE_POSTID_KEY: String="POSTID"
        const val ORGMODE_DATA_KEY: String="DATE"
        const val ORGMODE_CATEGORY_KEY: String="CATEGORY"
        const val ORGMODE_TAGS_KEY: String="TAGS"
        const val ORGMODE_TITLE_KEY: String="TITLE"
        const val ORGMODE_SLUG_KEY : String = "SLUG"

    }

    override fun update(meta: PostMetaData, fileName: Path) {
        val tempPath = Files.createTempFile("ghostuploader", "org")
        val bufferedWriter = PrintWriter(FileWriter(tempPath.toFile()))

        //writing the header.
        meta.toMap().forEach {
            entry -> bufferedWriter.println(
                "$ORGMODE_META_PREFIX${entry.key}$ORGMODE_META_SEPARATOR ${entry.value}"
            )
        }

        BufferedReader(FileReader(meta.fileName)).useLines {
            val iterator = it.iterator()
            var skipped = false;
            while (iterator.hasNext()) {
                val line = iterator.next()
                if (!skipped && (line.isEmpty() || line.startsWith(ORGMODE_META_PREFIX))) {
                    //skip the original header.
                    continue
                }else{
                    skipped = true
                    bufferedWriter.println(line)
                }
            }
        }

        Files.move(tempPath, File(meta.fileName).toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    //parse header from the string.
    fun parseHeader(orgContent: Reader) : Map<String, String> {
        val result = mutableMapOf<String, String>()
        BufferedReader(orgContent).useLines {
            val iterator = it.iterator()
            while (iterator.hasNext()) {
                val line = iterator.next()
                if (line.isEmpty()) {
                    continue
                } else if (line.startsWith(ORGMODE_META_PREFIX)) {
                    val pair = line.substring(ORGMODE_META_PREFIX.length)
                            .split(ORGMODE_META_SEPARATOR)
                            .map(String::trim)
                    if (pair.size ==2){
                        result.put(pair[0].toUpperCase(), pair[1])
                    }
                } else {
                    break
                }
            }
        }
        return result.toMap()
    }

    /**
     * example format.
     <pre>
        #+BLOG: graceliu
        #+POSTID: 124
        #+ORG2BLOG:
        #+DATE: [2017-08-10 Thu 10:47]
        #+CATEGORY: Programming
        #+TAGS: algorithms
        #+SLUG: algorithms
        #+DESCRIPTION:
        #+TITLE: Backtracking topic
     </pre>
    */
    override fun parse(orgContent: Reader): PostMetaData? {
        val map = parseHeader(orgContent)
        return fromMap(map)
    }

    private fun fromMap(map: Map<String, String>) : PostMetaData? {

        fun String.toInstance() : Instant {
            return Instant.now()
        }

        fun splitTagString(tagString: String?): List<String> {
            val tags = tagString?.split(",") ?: listOf()
            return tags.stream().map { t -> t.trim().toLowerCase() }.collect(Collectors.toList())
        }

        val title = map[ORGMODE_TITLE_KEY]

        //only the title is mandatory.
        return if (title!==null)
            PostMetaData(title,
                    map[ORGMODE_CATEGORY_KEY],
                    splitTagString(map[ORGMODE_TAGS_KEY]),
                    map[ORGMODE_DATA_KEY]?.toInstance(),
                    null,
                    map[ORGMODE_POSTID_KEY],
                    map[ORGMODE_SLUG_KEY]
            ) else null
    }

    private fun PostMetaData.toMap(): Map<String, String?> {
        return mapOf( ORGMODE_POSTID_KEY to this.id,
                ORGMODE_SLUG_KEY to this.slug,
                ORGMODE_TITLE_KEY to this.title,
                ORGMODE_CATEGORY_KEY to this.category,
                ORGMODE_TAGS_KEY to this.tags.joinToString()
        )
    }

}