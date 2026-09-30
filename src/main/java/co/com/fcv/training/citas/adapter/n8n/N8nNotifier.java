package co.com.fcv.training.citas.adapter.n8n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class N8nNotifier {
    private static final Logger log=LoggerFactory.getLogger(N8nNotifier.class);
    private final boolean enabled; private final String url; private final String token; private final RestClient client=RestClient.create();
    public N8nNotifier(@Value("${app.n8n.webhook-enabled:false}") boolean enabled,@Value("${app.n8n.webhook-url:}") String url,@Value("${app.n8n.webhook-bearer-token:}") String token){this.enabled=enabled;this.url=url;this.token=token;if(enabled&&(url.isBlank()||token.isBlank()))throw new IllegalStateException("N8N webhook requiere URL y bearer token");}
    public void statusChanged(Long appointmentId,String status,String source){if(!enabled)return;try{client.post().uri(url).header(HttpHeaders.AUTHORIZATION,"Bearer "+token).contentType(MediaType.APPLICATION_JSON).body(Map.of("schemaVersion","1","eventId",UUID.randomUUID().toString(),"eventType","APPOINTMENT_STATUS_CHANGED","appointmentId",appointmentId,"status",status,"source",source,"occurredAt",OffsetDateTime.now().toString())).retrieve().toBodilessEntity();}catch(Exception e){log.warn("No fue posible entregar evento n8n appointmentId={} status={}",appointmentId,status);}}
}
