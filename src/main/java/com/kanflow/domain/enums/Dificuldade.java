package com.kanflow.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Dificuldade {
    Baixa("Baixa"),
    Medio("M\u00e9dia"),
    Alta("Alta");

    private final String json;

    Dificuldade(String json) {
        this.json = json;
    }

    @JsonValue
    public String getJson() {
        return json;
    }

    @JsonCreator
    public static Dificuldade fromJson(String value) {
        if (value == null) {
            return null;
        }
        if ("Media".equalsIgnoreCase(value)) {
            return Medio;
        }
        for (Dificuldade d : values()) {
            if (d.json.equals(value) || d.name().equalsIgnoreCase(value)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Dificuldade invalida: " + value);
    }
}
