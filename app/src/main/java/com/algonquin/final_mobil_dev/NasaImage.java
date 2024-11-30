package com.algonquin.final_mobil_dev;

public class NasaImage {
    private String date;
    private String url;
    private String hdurl;

    public NasaImage(String date, String url, String hdurl) {
        this.date = date;
        this.url = url;
        this.hdurl = hdurl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHdurl() {
        return hdurl;
    }

    public void setHdurl(String hdurl) {
        this.hdurl = hdurl;
    }
}

