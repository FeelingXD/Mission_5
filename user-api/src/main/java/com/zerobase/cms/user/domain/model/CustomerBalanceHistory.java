package com.zerobase.cms.user.domain.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBalanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(targetEntity = Customer.class ,fetch = FetchType.LAZY)
    private Customer customer;
    private Integer changeMoney;
    private Integer  currentMoney;

    private String  fromMessage;

    private String description;


}
