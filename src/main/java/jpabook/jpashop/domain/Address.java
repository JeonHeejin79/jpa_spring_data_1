package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address { // 값타입은 GETTER만 열어두는게 좋다. 생성자를 통한 생성만 가능하도록 하는게 좋다.

    @Column(length = 10)
    private String city;
    @Column(length = 20)
    private String street;
    @Column(length = 5)
    private String zipcode;

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    protected Address() {

    }
}
