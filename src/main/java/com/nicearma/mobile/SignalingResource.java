package com.nicearma.mobile;

import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.eventbus.MessageConsumer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/signaling")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SignalingResource {

    @Inject
    Vertx vertx;

    private final Logger log = LoggerFactory.getLogger(SignalingResource.class);

    @GET()
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {

        return "hello";
    }


    @POST()
    @Path("/connection-offer")
    public Response connectionOffer(ConnectionOffer connectionOffer) {
        log.info(connectionOffer);
        vertx.eventBus().sendAndForget(connectionOffer.to, JsonObject.mapFrom(connectionOffer));
        return Response.accepted().build();
    }

    @POST()
    @Path("/answer-connection-offer")
    public Response answareConnectionOffer(AnswerConnectionOffer answerConnectionOffer) {
        log.info(answerConnectionOffer);
        vertx.eventBus().sendAndForget(answerConnectionOffer.to, JsonObject.mapFrom(answerConnectionOffer).toString());
        return Response.accepted().build();
    }

    @POST()
    @Path("/candidate")
    public Response candidate(Candidate candidate) {
        log.info(candidate);
        vertx.eventBus().sendAndForget(candidate.to, JsonObject.mapFrom(candidate).toString());
        return Response.accepted().build();
    }

    @GET()
    @Path("/events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<Object> events(@QueryParam("user") String user) {
        log.info("events");
        log.info(user);
        MessageConsumer<Object> consumerByUser = vertx.eventBus().consumer(user);
        return consumerByUser.toMulti().map(r -> r.body());
    }


}