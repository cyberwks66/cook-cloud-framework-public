package com.cooksys.airline.model;

public class Airport {
    private long airportId;
    private String name;
    private String city;
    private String country;
    private String iata;
    private String icao;
    private String latitude;
    private String longitude;
    private String altitude;
    private String timezone;
    private String dst;
    private String dbTimezone;
    private String type;
    private String source;

    public Airport() {
    }

    public Airport(long airportId, String name, String city, String country, String iata, String icao, String latitude, String longitude, String altitude, String timezone, String dst, String dbTimezone, String type, String source) {
        this.airportId = airportId;
        this.name = name;
        this.city = city;
        this.country = country;
        this.iata = iata;
        this.icao = icao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timezone = timezone;
        this.dst = dst;
        this.dbTimezone = dbTimezone;
        this.type = type;
        this.source = source;
    }

    public void setAirportId(long airportId) {
        this.airportId = airportId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public void setDbTimezone(String dbTimezone) {
        this.dbTimezone = dbTimezone;
    }

    public long getAirportId() {

        return airportId;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getIata() {
        return iata;
    }

    public String getIcao() {
        return icao;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getDst() {
        return dst;
    }

    public String getDbTimezone() {
        return dbTimezone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
