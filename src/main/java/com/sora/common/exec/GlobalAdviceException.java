package com.sora.common.exec;

import com.sora.common.http.RespBody;
import com.sora.common.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class GlobalAdviceException {

    @ExceptionHandler(value = Exception.class)
    public void handler(Exception e) throws IOException {
        String message = "Service internal exception";
        if (e instanceof NullPointerException) message = "Service null pointer exception.";
        log.error(execToString(e));
        Response.stream(new RespBody(500, message));
    }

    @ResponseStatus(value = HttpStatus.NOT_EXTENDED)
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public void unknown(NoHandlerFoundException e) throws IOException {
        log.error(execToString(e));
        Response.stream(new RespBody(404, e.getMessage()));
    }

    public String execToString(Exception e) {
        StringBuffer sb = new StringBuffer();
        Arrays.stream(e.getStackTrace()).filter(x -> x != null).forEach(x -> sb.append(x + "\n"));
        return e.getClass().getName() + ":" + e.getMessage() + "\n\t" + sb.toString();
    }

}
