package com.selimhorri.app.helper.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ApiResponse<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Boolean success;
    
    private String message;
    
    @JsonInclude(value = Include.NON_NULL)
    private String path;
    
    private HttpStatus status;
    
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    @JsonInclude(value = Include.NON_NULL)
    private T result;
    
    @JsonInclude(value = Include.NON_NULL)
    private Collection<T> collection;
    
}
