package com.tyrion.jrpc.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
}