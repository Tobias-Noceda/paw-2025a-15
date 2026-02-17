// package ar.edu.itba.paw.webapp.exception.mapper;

// import javax.ws.rs.core.Response;
// import javax.ws.rs.core.Response.Status;
// import javax.ws.rs.ext.ExceptionMapper;
// import javax.ws.rs.ext.Provider;

// @Provider
// public class GenericExceptionMapper implements ExceptionMapper<Exception>{
    
//     @Override
//     public Response toResponse(Exception exception) {
//         exception.printStackTrace();
//         return Response.status(Status.INTERNAL_SERVER_ERROR).build();
//     }
// }
