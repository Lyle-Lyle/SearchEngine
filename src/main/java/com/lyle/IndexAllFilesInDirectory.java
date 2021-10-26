package com.lyle;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Index all text files under a directory.
 */
public class IndexAllFilesInDirectory {
    static int counter = 0;

    public static void main(String[] args) throws Exception {
        String indexPath = "F:\\Courses\\Elective Courses\\InformationRetrievalSystem\\SearchEngine\\data\\citeseer2_index";
        String docsPath = "F:\\Courses\\Elective Courses\\InformationRetrievalSystem\\SearchEngine\\data\\citeseer2";
        System.out.println("Indexing to directory '" + indexPath + "'...");

        //指定索引库的存放位置
        //索引库也可以放在内存中
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        //指定一个分析器对文档内容进行分析
        Analyzer analyzer = new StandardAnalyzer();
        //
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        //
        IndexWriter writer = new IndexWriter(dir, iwc);
        indexDocs(writer, Paths.get(docsPath));
        writer.close();
    }

    static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                indexDoc(writer, file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /** Indexes a single document */
    static void indexDoc(IndexWriter writer, Path file) throws IOException {
        InputStream stream = Files.newInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        String title = br.readLine();
        //创建document对象
        Document doc = new Document();
        //保存文件路径名到索引，StringField不分词
        doc.add(new StringField("path", file.toString(), Field.Store.YES));
        //不保存内容到索引,TextField分词
        doc.add(new TextField("contents", br));

        //保存title到索引
        doc.add(new TextField("title", title, Field.Store.YES));
        writer.addDocument(doc);
        counter++;
        if (counter % 1000 == 0)
            System.out.println("indexing " + counter + "-th file " + file.getFileName());
        ;
    }
}
