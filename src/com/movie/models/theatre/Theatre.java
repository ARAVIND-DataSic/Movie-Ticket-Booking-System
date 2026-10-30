package com.movie.models.theatre;

public class Theatre {
    private final String id;
    private String name;
    private String address;
    private String city;

    public Theatre(String id, String name, String address, String city) {
        this.id = requireText(id, "id");
        setName(name);
        setAddress(address);
        setCity(city);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }

    public void setName(String name) { this.name = requireText(name, "name"); }
    public void setAddress(String address) { this.address = requireText(address, "address"); }
    public void setCity(String city) { this.city = requireText(city, "city"); }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(field + " must not be blank.");
        return value.trim();
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + address + " | " + city;
    }
}
