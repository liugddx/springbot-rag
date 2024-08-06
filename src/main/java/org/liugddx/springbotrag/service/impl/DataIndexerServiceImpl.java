package org.liugddx.springbotrag.service.impl;

import org.liugddx.springbotrag.service.DataIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class DataIndexerServiceImpl implements DataIndexer {

    @Value("classpath:/data/InfoQ-NoSql-trend-Report.pdf")
    private Resource documentResource;
    private final VectorStore vectorStore;

    Logger logger = LoggerFactory.getLogger(DataIndexerServiceImpl.class);

    public DataIndexerServiceImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


    @Override
    public void loadData() {
        DocumentReader documentReader = null;
        if(this.documentResource.getFilename() != null && this.documentResource.getFilename().endsWith(".pdf")){
            this.logger.info("Loading PDF document");
            documentReader = new PagePdfDocumentReader(this.documentResource,
                    PdfDocumentReaderConfig.builder()
                            .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                    .withNumberOfBottomTextLinesToDelete(3)
                                    .withNumberOfTopPagesToSkipBeforeDelete(3)
                                    .build())
                            .withPagesPerDocument(3)
                            .build());
        }else if (this.documentResource.getFilename() != null && this.documentResource.getFilename().endsWith(".txt")) {
            documentReader = new TextReader(this.documentResource);
        } else if (this.documentResource.getFilename() != null && this.documentResource.getFilename().endsWith(".json")) {
            documentReader = new JsonReader(this.documentResource);
        }
        if(documentReader != null){
            var textSplitter = new TokenTextSplitter();
            this.logger.info("Loading text document to qdrant vector database");
            this.vectorStore.accept(textSplitter.apply(documentReader.get()));
            this.logger.info("Loaded text document to qdrant vector database");
        }
    }

    @Override
    public long count() {
        return this.vectorStore.similaritySearch("*").size();
    }
}
