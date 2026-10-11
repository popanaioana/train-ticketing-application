package com.trainticketing.model;

import java.util.Objects;

public class Station {
    private String name;

    public Station() {

    }

    public Station(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof Station station)){
            return false;
        }
        return Objects.equals(name.toLowerCase(), station.name.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
}