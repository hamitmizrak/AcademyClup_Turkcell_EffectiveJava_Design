package com.hamitmizrak.good.domain;


import lombok.*;

//@Data  // Getter+Setter+toString+equals
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LombokDeneme {

    // Field
    private int id;
    private String name;
    private String surname;
    private String description;



    public static void main(String[] args) {
        LombokDeneme deneme = LombokDeneme.builder()
                .id(1)
                .name("name")
                .surname("surname")
                .description("description")
                .build();

        System.out.println(deneme);
    }
}
