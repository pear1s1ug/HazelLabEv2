package com.carrie.hazellabev2.dto;

import java.util.List;

public class RegionComunaDTO {
    // Atributos
    private String region;
    private List<String> comunas;

    // Constructor
    public RegionComunaDTO(String region, List<String> comunas) {
        this.region = region;
        this.comunas = comunas;
    }

    // Getters y setters
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public List<String> getComunas() { return comunas; }
    public void setComunas(List<String> comunas) { this.comunas = comunas; }
}
