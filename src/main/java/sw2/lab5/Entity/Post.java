package sw2.lab5.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "post")
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_Post;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Usuario user;
    private String title;
    private String content;
    private String summary;
    private boolean published;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public int getId_Post() {
        return id_Post;
    }

    public void setId_Post(int id_Post) {
        this.id_Post = id_Post;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
