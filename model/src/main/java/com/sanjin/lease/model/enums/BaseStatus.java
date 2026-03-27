package com.sanjin.lease.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.core.convert.converter.Converter;


public enum BaseStatus implements BaseEnum {


    ENABLE(1, "正常"),

    DISABLE(0, "禁用");


    @EnumValue
    @JsonValue
    private Integer code;

    private String name;

    BaseStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }
    
    /**
     * 根据 code 获取枚举
     */
    public static BaseStatus valueOfCode(Integer code) {
        for (BaseStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown BaseStatus code: " + code);
    }
    
    /**
     * Spring 转换器：将 Integer 转换为 BaseStatus
     */
    public static class IntegerToBaseStatusConverter implements Converter<Integer, BaseStatus> {
        @Override
        public BaseStatus convert(Integer source) {
            return valueOfCode(source);
        }
    }
    
    /**
     * Spring 转换器：将 String 转换为 BaseStatus
     */
    public static class StringToBaseStatusConverter implements Converter<String, BaseStatus> {
        @Override
        public BaseStatus convert(String source) {
            return valueOfCode(Integer.valueOf(source));
        }
    }
}
