package com.example.Chatboot.config;


import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Component
public class RagDataLoader {
    @Value("classpath:/pdf/CV.pdf")
    private Resource pdfResource;
    @Value("store-data-v1.json")
    private String storeFile;

@Bean

    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel){
        SimpleVectorStore vectorStore=new SimpleVectorStore(embeddingModel);
        String fileStore= Path.of("src","main","resources","store")
                .toAbsolutePath()+"/"+storeFile;
        File file=new File(fileStore);
        if(!file.exists()){
            PagePdfDocumentReader pagePdfDocumentReader=new PagePdfDocumentReader(pdfResource);
            List<Document> documents= pagePdfDocumentReader.get();
            TextSplitter textSplitter= new TokenTextSplitter();
            List<Document> chunks= textSplitter.split(documents);
            vectorStore.accept(chunks);
            vectorStore.save(file);


        }else{
            vectorStore.load(file);
        }
        return vectorStore;
    }




}
