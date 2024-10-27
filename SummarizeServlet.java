/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/SummarizeServlet")
public class SummarizeServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:derby://localhost:1527/NLP";
    private static final String DB_USER = "JAI";
    private static final String DB_PASSWORD = "123";

    // Summarization entry-point for POST request
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String inputType = request.getParameter("inputType");
    String inputText = "";
    String filePath = request.getParameter("filePath");

    // Handle the input type: either a PDF file, Text file, or manual input
    if ("text".equals(inputType)) {
        inputText = request.getParameter("inputText");
    } else if ("pdf".equals(inputType)) {
        inputText = extractTextFromPDF(filePath);
    } else if ("txt".equals(inputType)) {
        inputText = readFileText(filePath);
    }

    // Summarize the text
    String summarizedText = summarizeText(inputText);

    // Save the summarization request to the database
    saveSummaryToDatabase(inputType, inputText, summarizedText);

    // Retrieve summary history from the database and store it in the session
    List<String> summaryHistory = getSummaryHistory();
    HttpSession session = request.getSession();
    session.setAttribute("summaryHistory", summaryHistory);

    // Set attributes for the summary and word counts
    session.setAttribute("summary", summarizedText);
    session.setAttribute("originalWordCount", countWords(inputText));
    session.setAttribute("summarizedWordCount", countWords(summarizedText));

    // Send summary to JSP
    response.sendRedirect("summary.jsp");
}

// Method to count words in a given text
private int countWords(String text) {
    if (text == null || text.isEmpty()) {
        return 0;
    }
    return text.split("\\s+").length; // Splitting by whitespace
}

    // Extract text from a PDF file
    private String extractTextFromPDF(String pdfPath) throws IOException {
        File file = new File(pdfPath);
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return cleanText(pdfStripper.getText(document)); // Clean the extracted text
        }
    }

    // Read text from a file
    private String readFileText(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                contentBuilder.append(scanner.nextLine()).append("\n");
            }
        }
        return cleanText(contentBuilder.toString()); // Clean the file text
    }

    // Clean the text by removing unnecessary characters or symbols
    private String cleanText(String text) {
        return text.replaceAll("\\?", "").trim();
    }

    // Summarize text using advanced techniques like TF-IDF
    private String summarizeText(String text) {
        if (text == null || text.isEmpty()) return "No text provided.";

        // Setup Stanford NLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Annotate the text
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        // Extract sentences from the text
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        // Calculate TF-IDF scores for words
        Map<String, Double> tfidfScores = calculateTFIDF(sentences);

        // Rank sentences based on TF-IDF and sentence position
        List<String> rankedSentences = rankSentences(sentences, tfidfScores, 5);

        // Join the ranked sentences into a final summary
        return String.join(" ", rankedSentences);
    }

    // Calculate TF-IDF scores for words
    private Map<String, Double> calculateTFIDF(List<CoreMap> sentences) {
        Map<String, Integer> wordFreq = new HashMap<>();
        Map<String, Double> tfidfScores = new HashMap<>();
        int totalWordCount = 0;

        // Calculate word frequency
        for (CoreMap sentence : sentences) {
            String[] words = sentence.toString().toLowerCase().split("\\W+");
            totalWordCount += words.length;
            for (String word : words) {
                if (!word.isEmpty()) {
                    wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
                }
            }
        }

        // Calculate TF (Term Frequency)
        for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
            double tf = (double) entry.getValue() / totalWordCount;
            tfidfScores.put(entry.getKey(), tf);
        }

        // Inverse Document Frequency (IDF) based on word frequency
        for (Map.Entry<String, Double> entry : tfidfScores.entrySet()) {
            String word = entry.getKey();
            double idf = Math.log(1 + (1.0 / (wordFreq.get(word) + 1))); // Simple IDF assumption
            tfidfScores.put(word, entry.getValue() * idf);
        }

        return tfidfScores;
    }

    // Rank sentences based on TF-IDF scores and sentence position
    private List<String> rankSentences(List<CoreMap> sentences, Map<String, Double> tfidfScores, int limit) {
        PriorityQueue<Map.Entry<String, Double>> rankedSentences = new PriorityQueue<>(
                (a, b) -> b.getValue().compareTo(a.getValue())
        );

        // Score each sentence based on TF-IDF and position (earlier sentences have slightly higher weight)
        for (int i = 0; i < sentences.size(); i++) {
            CoreMap sentence = sentences.get(i);
            String sentenceText = sentence.toString();
            double score = calculateSentenceScore(sentenceText, tfidfScores) * (1 + 0.05 * (sentences.size() - i));

            rankedSentences.add(new AbstractMap.SimpleEntry<>(sentenceText, score));
        }

        // Extract top N sentences
        List<String> topSentences = new ArrayList<>();
        for (int i = 0; i < limit && !rankedSentences.isEmpty(); i++) {
            topSentences.add(rankedSentences.poll().getKey());
        }

        return topSentences;
    }

    // Calculate the score of a sentence based on TF-IDF scores
    private double calculateSentenceScore(String sentence, Map<String, Double> tfidfScores) {
        String[] words = sentence.toLowerCase().split("\\W+");
        double score = 0.0;

        for (String word : words) {
            score += tfidfScores.getOrDefault(word, 0.0);
        }

        return score;
    }

    // Method to save the summary to the database
    private void saveSummaryToDatabase(String inputType, String inputText, String summarizedText) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO SUMMARIZATION_HISTORY (INPUT_TYPE, INPUT_TEXT, SUMMARIZED_TEXT, TIMESTAMP) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, inputType);
                pstmt.setString(2, inputText);
                pstmt.setString(3, summarizedText);
                pstmt.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
                pstmt.executeUpdate();
                System.out.println("Summary successfully stored in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve summary history from the database
    private List<String> getSummaryHistory() {
        List<String> summaries = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT SUMMARIZATION_TEXT FROM SUMMARIZATION_HISTORY ORDER BY TIMESTAMP DESC"; // Use correct table name
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    summaries.add(rs.getString("SUMMARIZED_TEXT")); // Use the correct column name
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }
}
