package com.czettner.hypothesis;

import java.util.Date;

public class News {
    private String title;
    private String description;
    private String category;
    private String author;
    private Date publishedDate;

    /**
     * Constructor with all data
     * @param title
     * @param description
     * @param category
     * @param author
     * @param publishedDate
     */
    public News(String title, String description, String category, String author, Date publishedDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.author = author;
        this.publishedDate = publishedDate;
    }

    // BEGIN Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }
    // END Getters and setters
}
