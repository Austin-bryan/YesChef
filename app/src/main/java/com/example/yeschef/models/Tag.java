package com.example.yeschef.models;

public class Tag {
    private String name;

    public Tag(String name) {
        this.name = name;
    }

    // Getter
    public String getName() {
        return name;
    }

    // Optionally, add validation or other logic in setters
    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Tag name cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Tag tag = (Tag) obj;
        return name.equals(tag.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
