package org.pareco.tokenization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.Request;

public class CoreService extends AbstractHandler {
  private static final String contentType = "application/json";
  private static final String contentEncoding = "utf-8";
  private static final ObjectMapper objectMapper = DefaultObjectMapper.get();

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    response.setContentType(String.format("%s;charset=%s", contentType, contentEncoding));

    InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream());
    CoreDocument coreDocument = new CoreDocument(CharStreams.toString(inputStreamReader));
    Properties properties = new Properties();

    properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
    properties.setProperty("ner.applyFineGrained", "false");
    properties.setProperty("ner.combinationMode", "NORMAL");
    properties.setProperty("ner.useSUTime", "false");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

    pipeline.annotate(coreDocument);

    final List<CoreEntityMention> coreEntityMentions = coreDocument.entityMentions();

    final ImmutableList.Builder<Map<String, String>> builder = ImmutableList
      .builderWithExpectedSize(coreEntityMentions.size());

    coreEntityMentions.forEach(entityMention -> {
      builder.add(ImmutableMap.<String, String>builder()
        .put("type", entityMention.entityType())
        .put("value", entityMention.text())
        .build());
    });

    response.setStatus(HttpServletResponse.SC_CREATED);

    objectMapper.writeValue(response.getWriter(), builder.build());

    baseRequest.setHandled(true);
  }
}
