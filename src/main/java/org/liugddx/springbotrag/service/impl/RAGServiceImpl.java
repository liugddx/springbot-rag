package org.liugddx.springbotrag.service.impl;

import org.liugddx.springbotrag.service.RAGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RAGServiceImpl implements RAGService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("classpath:/prompts/system.pmt")
    private Resource systemPromptResource;

    private final VectorStore vectorStore;
    private final ChatClient aiClient;

    public RAGServiceImpl(VectorStore vectorStore, ChatClient aiClient) {
        this.vectorStore = vectorStore;
        this.aiClient = aiClient;
    }

    @Override
    public String findAnswer(String query) {
        // Combine system message retrieval and AI model call into a single operation
        ChatResponse aiResponse = aiClient.call(new Prompt(List.of(
                getRelevantDocs(query),
                new UserMessage(query))));

        // Log only necessary information, and use efficient string formatting
        logger.info("Asked AI model and received response.");
        return aiResponse.getResult().getOutput().getContent();
    }

    private Message getRelevantDocs(String query) {
        List<Document> similarDocuments = vectorStore.similaritySearch(query);

        // Log the document count efficiently
        if (logger.isInfoEnabled()) {
            logger.info("Found {} relevant documents.", similarDocuments.size());
        }

        // Streamline document content retrieval
        String documents = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(this.systemPromptResource);
        return systemPromptTemplate.createMessage(Map.of("documents", documents));
    }

}