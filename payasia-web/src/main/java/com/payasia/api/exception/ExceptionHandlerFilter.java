package com.payasia.api.exception;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;

/**
 * @author manojkumar2
 * @param : This ExceptionHandlerFilter class is used to handle framework error message
*/
public class ExceptionHandlerFilter extends OncePerRequestFilter {
	
	private static final Logger LOGGER = Logger.getLogger(ExceptionHandlerFilter.class);
	@Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch(RuntimeException e) {
            // custom error response class used across my project
        	ApiMessageHandler apiMessageHandler = null;
        	if(e!=null && e.getMessage().equals("403")){
        		apiMessageHandler = new ApiMessageHandler(Type.ERROR,"403","Access Denied");
             	response.setStatus(HttpStatus.SC_FORBIDDEN);
             	LOGGER.error(e.getMessage(), e);
             	
        	}else{
        	    apiMessageHandler = new ApiMessageHandler(Type.ERROR,"500",e.getMessage());
               	response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);               
               	LOGGER.error(e.getMessage(), e);
               	/* LOGGER.error("Caught exception while methodX. Please investigate: " 
                        + e 
                        + Arrays.asList(e.getStackTrace())
                        .stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining("\n"))
                );*/
        	}
        	response.getWriter().write(convertObjectToJson(apiMessageHandler));
    }
}

	public String convertObjectToJson(Object object) throws JsonProcessingException {
	    if (object == null) {
	        return null;
	    }
	    ObjectMapper mapper = new ObjectMapper();
	    return mapper.writeValueAsString(object);
	}

}
