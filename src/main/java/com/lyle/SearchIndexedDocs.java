package com.lyle;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;

public class SearchIndexedDocs {
    public static int N = 15;

//    private static String HighlightResult(Query query, String fieldName, String fieldContent,
//                                          Analyzer analyzer
//) throws InvalidTokenOffsetsException, IOException {
//        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
//        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
//
//        TokenStream tokenStream = analyzer.tokenStream(fieldName, new StringReader(fieldContent));
//        String str = highlighter.getBestFragment(tokenStream, fieldContent);
//        return(str);
//
//    }
    public static void main(String[] args) throws Exception {
        String index = "F:\\Courses\\Elective Courses\\InformationRetrievalSystem\\SearchEngine\\data\\citeseer2_index";
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        //两种方法创建查询对象
        QueryParser parser = new QueryParser("title", analyzer);
        //用户输入的关键词
        Query query = parser.parse("Distributed");
        //查询对象和返回评分最高的5个记录
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style='color:blue'>", "</span>");
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));

            TopDocs results = searcher.search(query, N);
            //查询到的结果数量
            System.out.println(results.totalHits + " total matching documents");
            if (results.totalHits >= N) {
                for (int i = 0; i < N; i++) {
                    //根据document的id找到document对象
                    Document doc = searcher.doc(results.scoreDocs[i].doc);
                    String path = doc.get("path");
                    System.out.println((i + 1) + ". " + path);
                    String title = doc.get("title");
                    if (title != null) {
                        //System.out.println("   Title: " + doc.get("title"));

                        TokenStream tokenStream = analyzer.tokenStream("title", new StringReader(title));
                        String fieldContent = highlighter.getBestFragment(tokenStream, title);
                        if (fieldContent == null) {
                            continue;
                        } else {
                            System.out.println("Title: "+ fieldContent);
                        }

//
                    }
//                    if (title != null) {
//                        String str = HighlightResult(query,"title",doc.get("title"),analyzer);
//                        System.out.println(str);
//                    }
                }
            }
        reader.close();
    }
}

