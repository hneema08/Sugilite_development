package edu.cmu.hcii.sugilite.sharing.model;

import javax.annotation.Nullable;

/**
 * @author toby
 * @date 9/24/19
 * @time 1:39 PM
 */
public class SugiliteRepoListing {
    private int id;
    private String title;
    private String author;

    public SugiliteRepoListing (int id, String title, @Nullable String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public SugiliteRepoListing (int id, String title) {
       this(id, title, null);
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }
}
