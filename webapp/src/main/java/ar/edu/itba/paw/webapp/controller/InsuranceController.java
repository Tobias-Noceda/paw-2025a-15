package ar.edu.itba.paw.webapp.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.webapp.controller.util.PaginationBuilder;
import ar.edu.itba.paw.webapp.dto.input.InsuranceCreateDTO;
import ar.edu.itba.paw.webapp.dto.input.InsuranceEditDTO;
import ar.edu.itba.paw.webapp.dto.output.InsuranceDTO;
import ar.edu.itba.paw.webapp.exception.FileNotFoundException;
import ar.edu.itba.paw.webapp.exception.InsuranceNotFoundException;

@Path("/insurances")
@Component
public class InsuranceController {

    @Autowired
    private InsuranceService is;

    @Autowired
    private FileService fs;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response listInsurances(
        @QueryParam("supportedBy") final Long doctorId,
        @QueryParam("page") @DefaultValue("1") final int page,
        @QueryParam("pageSize") @DefaultValue("10") Integer pageSize
    ) {
        Map<String, String> queryParams = new HashMap<>();

        if (doctorId != null) {
            final List<InsuranceDTO> insurances = is.getInsurancesByDoctorId(doctorId) //TODO paginar?
                .stream().map(InsuranceDTO.mapper(uriInfo)).collect(Collectors.toList());

            queryParams.put("supportedBy", doctorId.toString());

            return PaginationBuilder.buildResponse(
                Response.ok(new GenericEntity<List<InsuranceDTO>>(insurances) {}),
                page, 
                pageSize, 
                insurances.size(), 
                queryParams, 
                uriInfo
            );
        }
        
        final List<InsuranceDTO> allInsurances = is.getInsurancesPage(page, pageSize)
            .stream().map(InsuranceDTO.mapper(uriInfo)).collect(Collectors.toList());
        
        int totalInsurances = is.getInsurancesCount();
        
        return PaginationBuilder.buildResponse(
            Response.ok(new GenericEntity<List<InsuranceDTO>>(allInsurances) {}), 
            page, 
            pageSize, 
            totalInsurances, 
            queryParams, 
            uriInfo
        );
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response createInsurance(@Valid InsuranceCreateDTO dto) {
        File picture = fs.findById(dto.getPictureId()).orElseThrow(FileNotFoundException::new);
        final Insurance insurance = is.create(dto.getName(), picture);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(insurance.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") final long id) {
        Insurance insurance = is.getInsuranceById(id).orElseThrow(InsuranceNotFoundException::new);
        return Response.ok(InsuranceDTO.fromInsurance(uriInfo, insurance)).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response edit(
        @PathParam("id") long id,
        @Valid InsuranceEditDTO dto
    ) {
        is.getInsuranceById(id).orElseThrow(InsuranceNotFoundException::new);
        is.edit(id, dto.getName(), dto.getPictureId());
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteById(@PathParam("id") final long id) {
        is.delete(id);
        return Response.noContent().build();
    }
}
