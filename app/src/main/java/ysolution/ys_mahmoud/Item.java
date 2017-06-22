package ysolution.ys_mahmoud;



public class Item {
    String title;
    String publishedDate;
    String url;
    public Item(String title, String publishedDate, String url) {
        this.title = title.substring(1, title.length()-1);
        this.publishedDate = publishedDate.substring(1, 11);
        if(url != null)
            this.url = url.substring(1, url.length()-1);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
