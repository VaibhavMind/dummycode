package com.payasia.api.advice;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.common.util.PropReader;

/**
 * @author manojkumar2
 * @param : This class is used to handle API global exception. 
*/
@RestControllerAdvice
public class DefaultExceptionHandler {
 	
	private static final Logger LOGGER = Logger.getLogger(DefaultExceptionHandler.class);
 
	@RequestMapping(produces = {"application/json"})
    @ExceptionHandler({MissingServletRequestParameterException.class,
            UnsatisfiedServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class,
            ServletRequestBindingException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<?>  handleRequestException(Exception ex) {
		ApiMessageHandler apiMessageHandler = new ApiMessageHandler(Type.ERROR, HttpStatus.BAD_REQUEST.toString(),PropReader.getMessage("bad.request.error.msg"));
		LOGGER.error(ex.getMessage());
		return new ResponseEntity<>(apiMessageHandler,HttpStatus.BAD_REQUEST);
    }
	
    @RequestMapping(produces = {"application/json"})
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<?> handleUnsupportedMediaTypeException(HttpMediaTypeNotSupportedException ex) throws IOException {
    	ApiMessageHandler apiMessageHandler = new ApiMessageHandler(Type.ERROR, HttpStatus.UNSUPPORTED_MEDIA_TYPE.toString(),PropReader.getMessage("media.error.msg"));
    	LOGGER.error(ex.getLocalizedMessage());
    	return new ResponseEntity<>(apiMessageHandler,HttpStatus.UNSUPPORTED_MEDIA_TYPE);
   
    }

	@RequestMapping(produces = { "application/json" })
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<?>  handleUncaughtException(Exception ex) throws IOException {
		ApiMessageHandler apiMessageHandler =null;
    	if(ex!=null && ex.getMessage() !=null && ex.getMessage().equals("403")){
    	    apiMessageHandler = new ApiMessageHandler(Type.ERROR, "403","Access Denied");
    	    return new ResponseEntity<>(apiMessageHandler,HttpStatus.FORBIDDEN);
    	}else{
    		apiMessageHandler = new ApiMessageHandler(Type.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.toString(),PropReader.getMessage("internal.error.msg"));
    	}
		if (ex.getCause() != null) {
			LOGGER.error(ex.getCause().getMessage());
		} else {
			LOGGER.error(ex.getStackTrace());
		}
		return new ResponseEntity<>(apiMessageHandler,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}