package ma.nttdata.externals.module.interview.service.impl;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.config.SharePointConfig;
import ma.nttdata.externals.module.interview.service.RecordingUploadServ;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RecordingUploadServImpl implements RecordingUploadServ {

    private final SharePointConfig sharePointConfig;
    private GraphServiceClient graphClient;


    private GraphServiceClient getGraphClient() {
        if(graphClient == null) {
            ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                    .clientId(sharePointConfig.getClientId())
                    .clientSecret(sharePointConfig.getClientSecret())
                    .tenantId(sharePointConfig.getTenantId())
                    .build();

            TokenCredentialAuthProvider authProvider =
                    new TokenCredentialAuthProvider(Collections.
                            singletonList("https://graph.microsoft.com/.default"),
                            credential);
            graphClient = GraphServiceClient.builder()
                    .authenticationProvider(authProvider)
                    .buildClient();
        }
        return graphClient;
    }

}
