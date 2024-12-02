package com.algonquin.final_mobil_dev;

/**
 * Represents a NASA image with its associated details.
 */
public class NasaImage {
    private String date;
    private String url;
    private String hdurl;

    /**
     * Constructs a new NasaImage with the specified date, URL, and HD URL.
     *
     * @param date The date of the image.
     * @param url The URL of the image.
     * @param hdurl The HD URL of the image.
     */
    public NasaImage(String date, String url, String hdurl) {
        this.date = date;
        this.url = url;
        this.hdurl = hdurl;
    }

    /**
     * Gets the date of the image.
     *
     * @return The date of the image.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the image.
     *
     * @param date The new date of the image.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the URL of the image.
     *
     * @return The URL of the image.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the image.
     *
     * @param url The new URL of the image.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the HD URL of the image.
     *
     * @return The HD URL of the image.
     */
    public String getHdurl() {
        return hdurl;
    }

    /**
     * Sets the HD URL of the image.
     *
     * @param hdurl The new HD URL of the image.
     */
    public void setHdurl(String hdurl) {
        this.hdurl = hdurl;
    }
}