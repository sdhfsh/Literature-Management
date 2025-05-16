package com.mcy.backend.utils;

import com.mcy.backend.entity.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

public class CitationUtil {

    public static String generate(Document doc, String style) {
        String author = safe(doc.getAuthor());
        String title = safe(doc.getTitle());
        String journal = safe(doc.getJournal());
        String publisher = safe(doc.getPublisher());
        String volume = safe(doc.getVolume());
        String issue = safe(doc.getIssue());
        String pages = safe(doc.getPages());
        String doi = safe(doc.getDoi());
        String url = safe(doc.getUrl());

        // 使用 Java 8 时间 API 处理年份
        String year = Optional.ofNullable(doc.getPublicationDate())
                .map(date -> {
                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return String.valueOf(localDate.getYear());
                }).orElse("n.d."); // n.d. = no date

        switch (style.toUpperCase()) {
            case "APA":
                // 示例: Zhang, W. (2023). Title. Journal, Volume(Issue), Pages. https://doi.org/xxx
                return String.format("%s (%s). %s%s%s%s%s%s",
                        author,
                        year,
                        title,
                        journalPart(journal),
                        volumeIssue(volume, issue),
                        pagesPart(pages),
                        doiPart(doi),
                        urlPart(url)
                ).trim();
            case "MLA":
                // 示例: Zhang, Wei. "Title." Journal, vol. X, no. Y, Year, pp. Z. URL.
                return String.format("%s. \"%s.\"%s%s%s%s%s",
                        author,
                        title,
                        journalMLAPart(journal),
                        volumeIssueMLA(volume, issue),
                        yearPart(year),
                        pagesMLAPart(pages),
                        urlPart(url)
                ).trim();
            case "GBT":
                // 示例: 张伟. Title[J]. Journal, Year, Volume(Issue): Pages.
                return String.format("%s. %s[J].%s%s%s%s",
                        author,
                        title,
                        journalPart(journal),
                        yearPart(year),
                        volumeIssue(volume, issue),
                        pagesPart(pages)
                ).trim();
            default:
                return "不支持的引用格式：" + style;
        }
    }

    private static String safe(String value) {
        return value == null || value.trim().isEmpty() ? "" : value.trim();
    }

    // 各引用格式中用于拼接的部分
    private static String journalPart(String journal) {
        return journal.isEmpty() ? "" : " " + journal;
    }

    private static String journalMLAPart(String journal) {
        return journal.isEmpty() ? "" : " " + journal + ",";
    }

    private static String volumeIssue(String volume, String issue) {
        if (volume.isEmpty() && issue.isEmpty()) return "";
        if (!volume.isEmpty() && !issue.isEmpty()) return " " + volume + "(" + issue + ")";
        return " " + (!volume.isEmpty() ? volume : issue);
    }

    private static String volumeIssueMLA(String volume, String issue) {
        StringBuilder sb = new StringBuilder();
        if (!volume.isEmpty()) sb.append(" vol. ").append(volume);
        if (!issue.isEmpty()) sb.append(", no. ").append(issue);
        return sb.toString();
    }

    private static String pagesPart(String pages) {
        return pages.isEmpty() ? "" : ", " + pages;
    }

    private static String pagesMLAPart(String pages) {
        return pages.isEmpty() ? "" : ", pp. " + pages + ".";
    }

    private static String yearPart(String year) {
        return year.equals("n.d.") ? "" : " " + year;
    }

    private static String doiPart(String doi) {
        return doi.isEmpty() ? "" : " https://doi.org/" + doi;
    }

    private static String urlPart(String url) {
        return url.isEmpty() ? "" : " " + url;
    }
}
