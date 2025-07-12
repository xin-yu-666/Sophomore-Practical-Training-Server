package com.training.ai;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import okhttp3.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AutomatedNewsJob implements ApplicationRunner {
    // Dify配置
    private static final String DIFY_API_URL = "http://127.0.0.1/v1/chat-messages";
    private static final String DIFY_API_KEY = "app-jdx3Y3NOJiGug8tsRDvdyRBY";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new Gson();

    // 你的后端新增动态API
    private static final String NEWS_API_URL = "http://localhost:8080/api/news";

    // RSS源地址
    private static final String RSS_URL = "https://techcrunch.com/feed/";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String token = getAdminToken();
        Set<String> existingTitles = fetchExistingTitles(); // 获取已存在标题
        List<SyndEntry> newsList = fetchNews(RSS_URL, 5);
        for (SyndEntry entry : newsList) {
            String title = entry.getTitle();
            if (existingTitles.contains(title)) {
                System.out.println("已存在，跳过: " + title);
                continue;
            }
            String topic = title + " " + (entry.getDescription() != null ? entry.getDescription().getValue() : "");
            System.out.println("抓取新闻: " + topic);
            JsonObject aiResult = generateNewsContent(topic);
            String answerJson = aiResult.has("answer") ? aiResult.get("answer").getAsString() : "";
            String titleForDb = title;
            String summary = "";
            String content = "";
            if (!answerJson.isEmpty()) {
                try {
                    JsonObject answerObj = JsonParser.parseString(answerJson).getAsJsonObject();
                    if (answerObj.has("title")) titleForDb = cleanContent(answerObj.get("title").getAsString());
                    if (answerObj.has("summary")) summary = cleanSummary(answerObj.get("summary").getAsString());
                    if (answerObj.has("content")) content = cleanContent(answerObj.get("content").getAsString());
                } catch (Exception e) {
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\\"(title|summary|content)\\\"\\s*:\\s*\\\"([\\\\s\\\\S]*?)\\\"");
                    java.util.regex.Matcher m = p.matcher(answerJson);
                    while (m.find()) {
                        String key = m.group(1);
                        String val = m.group(2);
                        if ("title".equals(key)) titleForDb = cleanContent(val);
                        if ("summary".equals(key)) summary = cleanSummary(val);
                        if ("content".equals(key)) content = cleanContent(val);
                    }
                    if (content.isEmpty()) content = cleanContent(answerJson);
                }
            }
            // 截断标题长度，防止超长导致数据库报错
            if (titleForDb.length() > 100) {
                titleForDb = titleForDb.substring(0, 100);
            }
            System.out.println("AI生成内容: " + content);
            if (content == null || content.trim().isEmpty()) {
                System.out.println("内容为空，跳过入库，标题：" + titleForDb);
                continue; // 跳过本条新闻
            }
            postNews(titleForDb, summary, content, token);
        }
    }

    // 步骤A: 获取RSS新闻
    public static List<SyndEntry> fetchNews(String rssUrl, int maxCount) throws Exception {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(new URL(rssUrl)));
        List<SyndEntry> entries = feed.getEntries();
        return entries.subList(0, Math.min(maxCount, entries.size()));
    }

    // 步骤C: 调用Dify生成AI内容
    public static JsonObject generateNewsContent(String topic) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        Map<String, String> inputs = new HashMap<>();
        inputs.put("query", topic);
        payload.put("inputs", inputs);
        payload.put("query", topic);
        payload.put("response_mode", "blocking");
        payload.put("user", "news-bot");
        String jsonPayload = gson.toJson(payload);
        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(DIFY_API_URL)
                .addHeader("Authorization", "Bearer " + DIFY_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            System.out.println("Dify原始响应: " + responseBody);
            return JsonParser.parseString(responseBody).getAsJsonObject();
        }
    }

    // 步骤E: 调用后端API入库
    public static void postNews(String title, String summary, String content, String token) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("summary", summary);
        data.put("content", content);
        data.put("author", "AI内容机器人");
        data.put("status", 0);
        String json = gson.toJson(data);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request.Builder builder = new Request.Builder()
                .url(NEWS_API_URL)
                .post(body);
        if (token != null) {
            builder.addHeader("Authorization", "Bearer " + token);
        }
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println("入库结果: " + response.body().string());
        }
    }

    // 新增：从后端获取已存在新闻标题
    public static Set<String> fetchExistingTitles() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:8080/api/news/titles")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("警告：获取已存在标题失败，HTTP状态码：" + response.code() + "，返回内容：" + response.body().string());
                return new HashSet<>();
            }
            String responseBody = response.body().string();
            List list = null;
            try {
                list = gson.fromJson(responseBody, List.class);
            } catch (Exception e) {
                System.out.println("警告：解析已存在标题JSON失败，返回内容：" + responseBody);
            }
            if (list == null) {
                System.out.println("警告：后端未返回有效标题列表，自动跳过去重。返回内容：" + responseBody);
                return new HashSet<>();
            }
            return new HashSet<>(list);
        }
    }

    // 新增：获取管理员Token
    public static String getAdminToken() throws IOException {
        Map<String, String> login = new HashMap<>();
        login.put("username", "admin");
        login.put("password", "1234567");
        String json = gson.toJson(login);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("http://localhost:8080/api/auth/login")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String resp = response.body().string();
            JsonObject obj = JsonParser.parseString(resp).getAsJsonObject();
            // 兼容data为字符串token
            if (obj.has("data") && !obj.get("data").isJsonNull()) {
                return obj.get("data").getAsString();
            } else {
                // 如果登录失败，抛出明确的异常或返回null
                throw new IOException("获取Admin Token失败，后端返回: " + resp);
            }
        }
    }

    // 用于正文内容，保留全部段落
    public static String cleanContent(String text) {
        if (text == null) return "";
        text = text.replaceAll("<think[\\s\\S]*?</think>", "");
        text = text.replaceAll("<think[^>]*>", "");
        text = text.replaceAll("[-#*`>.]", "");
        text = text.replaceAll("\\n{2,}", "\n").trim();
        return text;
    }
    // 用于简介/摘要，只保留首段
    public static String cleanSummary(String text) {
        String cleaned = cleanContent(text);
        String[] lines = cleaned.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) return line.trim();
        }
        return cleaned;
    }
} 