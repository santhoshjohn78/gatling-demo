package example;


import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.RawFileBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.http.HttpDsl.*;

public class Search extends Simulation {
    // Protocol Definition
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://localhost:8080/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .header("apikey","mykey")
            .userAgentHeader("Gatling/Performance Test");

    ScenarioBuilder scn = CoreDsl.scenario("Load Test Search Provider")
            .exec(http("create token request")
                    .post("/token")
                    .header("Content-Type", "application/json")
                    .check(status().is(200))
                    .check(CoreDsl.bodyString().saveAs("token"))
            )
            .exec(
                    http("Api request")
                    .post("/search")
                    .header("Accept-Encoding","gzip")
                    .header("Authorization","Bearer ${token}")
                    .body(RawFileBody("RequestBody.json"))
                    .check(status().is(200))
            );

    public ProviderSearch() {
        this.setUp(scn.injectOpen(constantUsersPerSec(5).during(Duration.ofSeconds(2))))
                .protocols(httpProtocol);
    }
}
